package com.alibi.mapwithcurrentlocation;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.alibi.mapwithcurrentlocation.config.SharedPreferenceConfig;
import com.alibi.mapwithcurrentlocation.config.UserDetailsPrefrennce;
import com.alibi.mapwithcurrentlocation.config.listener.AppListener;
import com.alibi.mapwithcurrentlocation.databinding.ActivityMainBinding;
import com.alibi.mapwithcurrentlocation.model.GetLocationResponse;
import com.alibi.mapwithcurrentlocation.model.UpdateLocationResponse;
import com.alibi.mapwithcurrentlocation.model.UpdatedLocations;
import com.alibi.mapwithcurrentlocation.response.LogOutResponsePage;
import com.alibi.mapwithcurrentlocation.util.MapApiController;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.easywaylocation.EasyWayLocation.LOCATION_SETTING_REQUEST_CODE;

public class MainActivity extends FragmentActivity{
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    SupportMapFragment supportMapFragment;
    ActivityMainBinding mainBinding;
    SharedPreferences sharedPreferences;
    UserDetailsPrefrennce userDetailsPrefrennce;
    MapApiController mapApiController;
    List<com.alibi.mapwithcurrentlocation.model.Location> updatedLocations;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        userDetailsPrefrennce = new UserDetailsPrefrennce(this);
        userDetailsPrefrennce.saveBooleanData(SharedPreferenceConfig.IS_USER_LOGIN, true);
        mapApiController = new MapApiController(this);
        updatedLocations = new ArrayList<>();





        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mainBinding.username.setText(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.USER_NAME));

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        showCurrentLocationInMap();
                        startLocationService();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


        mainBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertBoxForLogout();
            }
        });

        mainBinding.fetchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                for (int i = 0; i < updatedLocations.size(); i++) {
                    if (i == updatedLocations.size() - 1) {
                        LatLng latLng = new LatLng(Double.parseDouble(updatedLocations.get(i).getLat()), Double.parseDouble(updatedLocations.get(i).getLng()));
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(" " + updatedLocations.get(i).getTime());
                        mMap.addMarker(markerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6));
                    } else {
                        addMarkerToMap(updatedLocations.get(i));
                    }
                }
            }
        });
    }
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service :
                                  activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (MyLocationService.class.getName().equals(service.service.getClassName())){
                    if (service.foreground){
                        return true;
                    }
                }

            }
            return false;
        }
        return false;
    }
    private void startLocationService(){
        if (!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(),MyLocationService.class);
            intent.setAction(Constant.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Location service Start",Toast.LENGTH_LONG).show();


        }
    }
    private void stopServiceLocation(){
        if (isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(),MyLocationService.class);
            intent.setAction(Constant.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Location service Stop",Toast.LENGTH_LONG).show();

        }
    }

    private void addMarkerToMap(com.alibi.mapwithcurrentlocation.model.Location location) {
        LatLng latLng = new LatLng(Double.valueOf(location.getLat()), Double.valueOf(location.getLng()));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(" " + location.getTime());
        mMap.addMarker(markerOptions);
    }


    private void getLoggedOut(String token) {
        mapApiController.onLoggedOut(token, new AppListener.OnLoggedOut() {
            @Override
            public void onSuccess(LogOutResponsePage logOutResponsePageListener) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, "Logout Failed", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showCurrentLocationInMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
//                Log.d("TAG", "onSuccess: " + location.getLatitude() + "\n" + location.getLongitude());
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        mMap = googleMap;
                        getUpdateLocation(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        Log.d("MainActivity", "onMapReady: " + location.getLatitude() + "\t" + location.getLongitude());
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        mMap.setMyLocationEnabled(true);
                        mMap.addMarker(markerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    }
                });
            }
        });
    }

    private void showAlertBoxForLogout() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Are you sure want to logout?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        StringUtils.deleteSharedPreferenceDataForlogout(MainActivity.this);
                        StringUtils.launchActivity(MainActivity.this, LoginActivity.class);
                        stopServiceLocation();
                        getLoggedOut(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN));
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void getUpdateLocation(String tok, String lat, String log) {
        mapApiController.updateLocation(tok, lat, log, new AppListener.OnUpdateLocation() {
            @Override
            public void onSuccess(UpdateLocationResponse updateLocationResponseListener) {
                if (updateLocationResponseListener.getSuccess() == true) {
                    Toast.makeText(MainActivity.this, "Location Update Successfully", Toast.LENGTH_LONG).show();
                    getLocation(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN));
                } else {
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        mMap.clear();
        showCurrentLocationInMap();
    }

}