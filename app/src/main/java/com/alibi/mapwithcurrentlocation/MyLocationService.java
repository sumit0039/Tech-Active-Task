package com.alibi.mapwithcurrentlocation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.alibi.mapwithcurrentlocation.config.SharedPreferenceConfig;
import com.alibi.mapwithcurrentlocation.config.UserDetailsPrefrennce;
import com.alibi.mapwithcurrentlocation.config.listener.AppListener;
import com.alibi.mapwithcurrentlocation.model.GetLocationResponse;
import com.alibi.mapwithcurrentlocation.model.Location;
import com.alibi.mapwithcurrentlocation.model.UpdateLocationResponse;
import com.alibi.mapwithcurrentlocation.util.MapApiController;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.List;

public class MyLocationService extends Service {
    UserDetailsPrefrennce userDetailsPrefrennce;
    MapApiController mapApiController;
    List<com.alibi.mapwithcurrentlocation.model.Location> updatedLocations;


//    private LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(@NonNull LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            if (locationResult != null && locationResult.getLastLocation() != null){
//                double latitude = locationResult.getLastLocation().getLatitude();
//                double longitude = locationResult.getLastLocation().getLongitude();
//                Log.d("LocationUpdate", latitude + "" + longitude);
//
//            }
//        }
//    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"Service Statrt",Toast.LENGTH_LONG).show();
        mapApiController = new MapApiController(getApplicationContext());
        userDetailsPrefrennce = new UserDetailsPrefrennce(getApplicationContext());
        updatedLocations = new ArrayList<>();
        Log.d("bbscsnc",userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN)+"");
        Log.d("bbscsnc",userDetailsPrefrennce.getStringData(SharedPreferenceConfig.LAT)+"");
        Log.d("bbscsnc",userDetailsPrefrennce.getStringData(SharedPreferenceConfig.LONG)+"");
            mapApiController.updateLocation(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN), userDetailsPrefrennce.getStringData(SharedPreferenceConfig.LAT), userDetailsPrefrennce.getStringData(SharedPreferenceConfig.LONG), new AppListener.OnUpdateLocation() {
                @Override
                public void onSuccess(UpdateLocationResponse updateLocationResponseListener) {
                    if (updateLocationResponseListener.getSuccess() == true) {
                        Toast.makeText(getApplicationContext(), "Location Update Successfully", Toast.LENGTH_LONG).show();
                        getLocation(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN));
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                }
            });


        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "service destroy", Toast.LENGTH_LONG).show();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }
    private void getLocation(String tok) {
        mapApiController.getLocation(tok, new AppListener.OnGetLocation() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(GetLocationResponse getLocationResponse) {
                updatedLocations.addAll(getLocationResponse.getLocations());
                Log.d("bcc", updatedLocations + "");
//                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }


//    private void startLocationService(){
//        String channelId = "location_notification_channel";
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent resultIntent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                getApplicationContext(),0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(
//                getApplicationContext(),channelId
//        );
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("Location Service");
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        builder.setContentText("Running");
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(false);
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null){
//                NotificationChannel notificationChannel = new NotificationChannel(
//                        channelId,"Location Service",NotificationManager.IMPORTANCE_HIGH
//                );
//                notificationChannel.setDescription("This channel is used by location service");
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(4000);
//        locationRequest.setFastestInterval(2000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
}