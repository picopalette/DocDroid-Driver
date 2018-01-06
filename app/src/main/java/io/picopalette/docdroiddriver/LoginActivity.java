package io.picopalette.docdroiddriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameET;
    private EditText mPasswordET;
    private RequestQueue queue;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = getSharedPreferences("Mypref",0);
        editor = mSharedPreferences.edit();
        editor.putString("url","http://172.16.8.43:8000/");
        editor.commit();
        mUsernameET = (EditText) findViewById(R.id.usernameEditText);
        mPasswordET = (EditText) findViewById(R.id.passwordEditText);
        Button mLoginButton = (Button) findViewById(R.id.loginButton);
        TextView signUpTextView = (TextView) findViewById(R.id.signupTextView);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),ipactivity.class);
                startActivity(intent);
            }
        });


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = mUsernameET.getText().toString();
                String userPass = mPasswordET.getText().toString();

                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("phone", user);
                    loginData.put("password", userPass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url;

                url = mSharedPreferences.getString("url", "") + "api/loginDriver";

                JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, loginData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            editor.putString("phone",response.getString("phone") );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.apply();
                        Intent intent;
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError || error.networkResponse == null) {
                            Toast.makeText(getApplicationContext(), "can't connect to server", Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse.statusCode == 401) {
                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                NetworkHelper.getInstance(getBaseContext()).addToRequestQueue(loginRequest);
            }
        });



    }

}

