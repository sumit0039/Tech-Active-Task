package com.alibi.mapwithcurrentlocation.util;

import android.content.Context;
import android.widget.Toast;

import com.alibi.mapwithcurrentlocation.config.UserDetailsPrefrennce;
import com.alibi.mapwithcurrentlocation.config.listener.AppListener;
import com.alibi.mapwithcurrentlocation.config.network.ApiClient;
import com.alibi.mapwithcurrentlocation.config.network.ApiInterface;
import com.alibi.mapwithcurrentlocation.model.GetLocationResponse;
import com.alibi.mapwithcurrentlocation.model.UpdateLocationResponse;
import com.alibi.mapwithcurrentlocation.response.LogInResponsePage;
import com.alibi.mapwithcurrentlocation.response.LogOutResponsePage;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapApiController {

    private final Context context;
    private final ApiInterface apiService;
    private final UserDetailsPrefrennce userDetailsPrefrennce;

    public MapApiController(Context context) {
        this.context = context;
        apiService = ApiClient.getInterface();
        userDetailsPrefrennce = UserDetailsPrefrennce.getInstance(context);
    }

    public void onLoggedIn(String userName, String password, final AppListener.OnLoggedIn onLoggedInListener) {
        Map<String, String> userNameAndPassword = new HashMap<>();
        userNameAndPassword.put("username", userName);
        userNameAndPassword.put("password", password);

        apiService.getLoggedIn(userNameAndPassword)
                .enqueue(new Callback<LogInResponsePage>() {
                    @Override
                    public void onResponse(Call<LogInResponsePage> call, Response<LogInResponsePage> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess()) {
                                onLoggedInListener.onSuccess(response.body());
                            } else {
                                Toast.makeText(context, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            onLoggedInListener.onFailure("" + response.body().getSuccess());
                        }
                    }

                    @Override
                    public void onFailure(Call<LogInResponsePage> call, Throwable t) {
                        onLoggedInListener.onFailure(t.getMessage());
                    }
                });
    }

    public void onLoggedOut(String token, final AppListener.OnLoggedOut onLoggedOutListener) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", token);

        apiService.getLoggedOut(tokens)
                .enqueue(new Callback<LogOutResponsePage>() {
                    @Override
                    public void onResponse(Call<LogOutResponsePage> call, Response<LogOutResponsePage> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess()) {
                                onLoggedOutListener.onSuccess(response.body());
                            }
                        } else {
                            onLoggedOutListener.onFailure("Logout Failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<LogOutResponsePage> call, Throwable t) {
                        onLoggedOutListener.onFailure(t.getMessage());
                    }
                });
    }

    public void getLocation(String token, final AppListener.OnGetLocation onGetLocation) {

        apiService.getLocation(token)
                .enqueue(new Callback<GetLocationResponse>() {
                    @Override
                    public void onResponse(Call<GetLocationResponse> call, Response<GetLocationResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess()) {
                                onGetLocation.onSuccess(response.body());
                            }
                        } else {
                            onGetLocation.onFailure("Failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetLocationResponse> call, Throwable t) {
                        onGetLocation.onFailure(t.getMessage());
                    }
                });
    }

    public void updateLocation(String token, String latitude, String longitude, final AppListener.OnUpdateLocation onUpdateLocationListener){
        Map<String,String> map = new HashMap<>();
        map.put("token",token);
        map.put("latitude",latitude);
        map.put("longitude",longitude);
        apiService.updateLocation(map)
                .enqueue(new Callback<UpdateLocationResponse>() {
                    @Override
                    public void onResponse(Call<UpdateLocationResponse> call, Response<UpdateLocationResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess()) {
                                onUpdateLocationListener.onSuccess(response.body());
                            }
                        } else {
                            onUpdateLocationListener.onFailure("Failed");
                        }
                    }


                    @Override
                    public void onFailure(Call<UpdateLocationResponse> call, Throwable t) {
                        onUpdateLocationListener.onFailure(t.getMessage());
                    }
                });
    }
}
