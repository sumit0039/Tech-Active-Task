package com.alibi.mapwithcurrentlocation.config.network;


import androidx.databinding.library.baseAdapters.BuildConfig;

import com.alibi.mapwithcurrentlocation.config.NetworkConfig;
import com.alibi.mapwithcurrentlocation.config.SharedPreferenceConfig;
import com.alibi.mapwithcurrentlocation.config.UserDetailsPrefrennce;
import com.alibi.mapwithcurrentlocation.util.MapApp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static final Interceptor REQUEST_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            UserDetailsPrefrennce userDetailsPrefrennce = UserDetailsPrefrennce.getInstance(MapApp.getCurrentActivityContext());
//            Log.d("uewfiugew",userDetailsPrefrennce.getStringData(SharedPreferenceConfig.DEVICE_ID));
//            Log.d("uewfiugew",userDetailsPrefrennce.getStringData(SharedPreferenceConfig.USER_ID));
//            Log.d("uewfiugew",userDetailsPrefrennce.getStringData(SharedPreferenceConfig.USER_TYPE));
            request = request.newBuilder()
                    .addHeader("Accept", "application/json")
//                    .addHeader("Deviceid", userDetailsPrefrennce.getStringData(SharedPreferenceConfig.DEVICE_ID) == null ?
//                            "" : userDetailsPrefrennce.getStringData(SharedPreferenceConfig.DEVICE_ID))
//                    .addHeader("Userid", userDetailsPrefrennce.getStringData(SharedPreferenceConfig.USER_ID) == null ?
//                            "" : userDetailsPrefrennce.getStringData(SharedPreferenceConfig.USER_ID))
//                    .addHeader("Type", userDetailsPrefrennce.getStringData(SharedPreferenceConfig.USER_TYPE) == null ?
//                            "" : userDetailsPrefrennce.getStringData(SharedPreferenceConfig.USER_TYPE))
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = chain.proceed(request);
            return response;
        }
    };
    private static Retrofit retrofit = null;
    private static HttpLoggingInterceptor interceptor;
    private static OkHttpClient client;

    static Retrofit getClient() {
        interceptor = new HttpLoggingInterceptor();
        if (retrofit == null) {
            if (BuildConfig.DEBUG) {
                interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                client = new OkHttpClient.Builder()
                        .addInterceptor(REQUEST_INTERCEPTOR)
                        .addInterceptor(interceptor)
//                        .connectTimeout(10, TimeUnit.MINUTES)
//                        .readTimeout(5, TimeUnit.MINUTES)
//                        .writeTimeout(5, TimeUnit.MINUTES)
                        .build();

            } else {
                interceptor = new HttpLoggingInterceptor();
                client = new OkHttpClient.Builder()
                        .addInterceptor(REQUEST_INTERCEPTOR)
                        .addInterceptor(interceptor)
//                        .connectTimeout(10, TimeUnit.MINUTES)
//                        .readTimeout(5, TimeUnit.MINUTES)
//                        .writeTimeout(5, TimeUnit.MINUTES)
                        .build();
            }
            retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkConfig.GET_BASE_URL())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static ApiInterface getInterface() {
        return getClient().create(ApiInterface.class);
    }

}
