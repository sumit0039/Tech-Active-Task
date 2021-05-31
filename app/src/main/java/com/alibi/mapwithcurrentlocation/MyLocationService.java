package com.alibi.mapwithcurrentlocation;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.alibi.mapwithcurrentlocation.config.SharedPreferenceConfig;
import com.alibi.mapwithcurrentlocation.config.UserDetailsPrefrennce;
import com.alibi.mapwithcurrentlocation.config.listener.AppListener;
import com.alibi.mapwithcurrentlocation.model.GetLocationResponse;
import com.alibi.mapwithcurrentlocation.model.UpdateLocationResponse;
import com.alibi.mapwithcurrentlocation.util.MapApiController;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MyLocationService extends Service {
    UserDetailsPrefrennce userDetailsPrefrennce;
    MapApiController mapApiController;
    List<com.alibi.mapwithcurrentlocation.model.Location> updatedLocations;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                userDetailsPrefrennce = new UserDetailsPrefrennce(getApplicationContext());
                mapApiController = new MapApiController(getApplicationContext());
                updatedLocations = new ArrayList<>();
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                userDetailsPrefrennce.saveStringData(SharedPreferenceConfig.LAT,String.valueOf(locationResult.getLastLocation().getLatitude()));
                userDetailsPrefrennce.saveStringData(SharedPreferenceConfig.LONG,String.valueOf(locationResult.getLastLocation().getLongitude()));
                Log.d("LocationUpdate", latitude + "" + longitude);
                getUpdateLocation(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN),userDetailsPrefrennce.getStringData(SharedPreferenceConfig.LAT),userDetailsPrefrennce.getStringData(SharedPreferenceConfig.LONG));
                Toast.makeText(getApplicationContext(),"Update Location Successfully",Toast.LENGTH_LONG).show();

            }
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String action = intent.getAction();
            if (action != null){
                if (action.equals(Constant.ACTION_START_LOCATION_SERVICE)){
                    startLocationService();
                }else if (action.equals(Constant.ACTION_STOP_LOCATION_SERVICE)){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent,flags,startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "service destroy", Toast.LENGTH_LONG).show();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constant.LOCATION_SERVICE_ID,builder.build());

    }
    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    private void getUpdateLocation(String tok, String lat, String log) {
        mapApiController.updateLocation(tok, lat, log, new AppListener.OnUpdateLocation() {
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

}