package com.alibi.mapwithcurrentlocation.config.network;

import com.alibi.mapwithcurrentlocation.model.GetLocationResponse;
import com.alibi.mapwithcurrentlocation.model.UpdateLocationResponse;
import com.alibi.mapwithcurrentlocation.response.LogInResponsePage;
import com.alibi.mapwithcurrentlocation.response.LogOutResponsePage;
import com.alibi.mapwithcurrentlocation.util.MapApp;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("login")
    Call<LogInResponsePage> getLoggedIn(@Body Map<String,String> userNamePassword);

    @POST("logout")
    Call<LogOutResponsePage> getLoggedOut(@Body Map<String,String> token);

    @POST ("location-update")
    Call<UpdateLocationResponse> updateLocation(@Body Map<String,String> map);


    @GET("get-locations")
    Call<GetLocationResponse> getLocation(@Query("token") String token);

}
