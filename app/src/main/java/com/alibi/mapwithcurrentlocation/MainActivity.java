package com.alibi.mapwithcurrentlocation;

import android.Manifest;
import android.app.PendingIntent;
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
    private Handler mHandler;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    SupportMapFragment supportMapFragment;
    ActivityMainBinding mainBinding;
    SharedPreferences sharedPreferences;
    UserDetailsPrefrennce userDetailsPrefrennce;
    MapApiController mapApiController;
    Location location;
    private final LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location eachLocation : locationResult.getLocations()) {
                Toast.makeText(MainActivity.this,"chal nahi raha hi",Toast.LENGTH_LONG).show();
                userDetailsPrefrennce.saveStringData(SharedPreferenceConfig.LAT,String.valueOf(eachLocation.getLatitude()));
                userDetailsPrefrennce.saveStringData(SharedPreferenceConfig.LONG,String.valueOf(eachLocation.getLongitude()));
                getUpdateLocation(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN), String.valueOf(eachLocation.getLatitude()), String.valueOf(eachLocation.getLongitude()));
                getLocation(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN));

                Log.d("jkdfsdf",userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN)+"");
            }
        }
    };
    List<com.alibi.mapwithcurrentlocation.model.Location> updatedLocations;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        easyWayLocation = new EasyWayLocation(this, false,false,this);


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        userDetailsPrefrennce = new UserDetailsPrefrennce(this);
        userDetailsPrefrennce.saveBooleanData(SharedPreferenceConfig.IS_USER_LOGIN, true);
//        instance = this;
        mapApiController = new MapApiController(this);
        updatedLocations = new ArrayList<>();
//        addUpdatedLocations();
        sharedPreferences = getSharedPreferences("Map", MODE_PRIVATE);





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
//                        UpdateLocation();
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
                startService(new Intent(MainActivity.this,MyLocationService.class));
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
//                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();
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
                        getLoggedOut(userDetailsPrefrennce.getStringData(SharedPreferenceConfig.TOKEN));
                        finish();
                        stopService(new Intent(MainActivity.this,MyLocationService.class));
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

//    public void updateTextView(final String value) {
//        MainActivity.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mainBinding.latLog.setText(value);
//            }
//        });
//    }
//
//    private PendingIntent getPendingIntent() {
//        Intent intent = new Intent(this, MyLocationService.class);
//        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
//        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    private void UpdateLocation() {
//        buildLocationRequest();
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
//    }
//
//    private void buildLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(4000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setSmallestDisplacement(10f);
//    }

    private void checkSettingAndStartLocationUpdate() {

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest)
                .build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(locationSettingsRequest);

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // settings of device are satisfied and we can start locationUpdates
                StartLocationUpdate();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(MainActivity.this, 1001);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }

                }
            }
        });

    }

    private void StartLocationUpdate() {
        Toast.makeText(MainActivity.this,"chal raha hi",Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }
    private void StopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(MainActivity.this,"Start",Toast.LENGTH_LONG).show();
        checkSettingAndStartLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        StopLocationUpdate();
    }

    @Override
    public void onBackPressed() {
        mMap.clear();
        showCurrentLocationInMap();
    }

}