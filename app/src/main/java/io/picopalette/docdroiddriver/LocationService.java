package io.picopalette.docdroiddriver;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sendNotification("You are Live");
        sharedPreferences = getSharedPreferences("Mypref", 0);
        editor = sharedPreferences.edit();
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext()); // this = context
        final String url = sharedPreferences.getString("url", "") + "api/setLocation";


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("phone",sharedPreferences.getString("phone",""));
                    jsonObject.put("lat",String.valueOf(location.getLatitude()));
                    jsonObject.put("log",String.valueOf((location.getLongitude())));

                    Log.d("finalll", String.valueOf(jsonObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("response", String.valueOf(response));

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id",sharedPreferences.getString("phone",""));
                        return params;
                    }
                };

                NetworkHelper.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
               sendNotification("You are Live");


            }

            @Override
            public void onProviderDisabled(String s) {

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(89);
                sendNotification("You have disabled your network and location services enable it again to share it with your friends");
                Log.d("providerdis",s);
                stopSelf();


            }
        };


        LocationManager locationManager = (LocationManager) getBaseContext()
                .getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d("error","error");
            return;
        }
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, locationListener);



    }

    private void sendNotification(String s) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("DOCDROID DRIVER");
        mBuilder.setContentText(s);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());

    }

}
