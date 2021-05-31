package com.alibi.mapwithcurrentlocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibi.mapwithcurrentlocation.config.UserDetailsPrefrennce;


public class StringUtils {

    public static void launchActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
    }


    public static void deleteSharedPreferenceDataForlogout(Context context) {
        UserDetailsPrefrennce userDetailsPrefrennce = UserDetailsPrefrennce.getInstance(context);
        userDetailsPrefrennce.deleteSharedPrefrenceMemory();
//        DbFactory.buildDb(context).clearAllTables();
        ((Activity) context).finishAffinity();
        ((Activity) context).startActivity(new Intent(context, LoginActivity.class));
    }
}
