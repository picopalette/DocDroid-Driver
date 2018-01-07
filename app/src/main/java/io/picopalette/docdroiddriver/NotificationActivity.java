package io.picopalette.docdroiddriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.sip.SipSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationActivity extends AppCompatActivity {
    private String url;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mSharedPreferences = getSharedPreferences("Mypref",0);


        progressDialog = new ProgressDialog(NotificationActivity.this);
        progressDialog.setMessage("Checking for a new Person");
        progressDialog.show();
        String phone = mSharedPreferences.getString("phone","");
        Log.d("phonenumber",phone);
        url = mSharedPreferences.getString("url", "") + "api/getLocationUser?phone="+phone;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Double lat = Double.valueOf(response.getString("lat"));
                    Double log = Double.valueOf(response.getString("log"));
                    latLng = new LatLng(lat,log);
                    String URL = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&destination="+latLng;
                    Uri location = Uri.parse(URL);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                    progressDialog.dismiss();

                    startActivity(mapIntent);



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        });
        NetworkHelper.getInstance(NotificationActivity.this).addToRequestQueue(jsonObjectRequest);


    }
}
