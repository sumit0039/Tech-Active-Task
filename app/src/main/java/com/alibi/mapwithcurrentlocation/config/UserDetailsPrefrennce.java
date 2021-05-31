package com.alibi.mapwithcurrentlocation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

@SuppressWarnings("ALL")
public class UserDetailsPrefrennce {
    private static UserDetailsPrefrennce userDetailsPrefrennce;
    private SharedPreferences sharedPreferences;

    public static UserDetailsPrefrennce getInstance(Context context) {
        if (userDetailsPrefrennce == null) {
            userDetailsPrefrennce = new UserDetailsPrefrennce(context);
        }
        return userDetailsPrefrennce;
    }

    public UserDetailsPrefrennce(Context context) {
        sharedPreferences = context.getSharedPreferences(SharedPreferenceConfig.SHAREDPREFRENCE_MEMORY_NAME, Context.MODE_PRIVATE);
    }

    //save String data in sharedPrefrence
    public void saveStringData(String key, String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    //save Int data in sharedPrefrence
    public void saveIntData(String key, int value){
        SharedPreferences.Editor prefsEditor=sharedPreferences.edit();
        prefsEditor.putInt(key,value);
        prefsEditor.commit();
    }

    //save boolean data in sharedPrefrence
    public void saveBooleanData(String key, boolean value){
        SharedPreferences.Editor prefsEditor=sharedPreferences.edit();
        prefsEditor.putBoolean(key,value);
        prefsEditor.commit();
    }


    //save long data in sharedPrefrence
    public void saveLongData(String key, long value){
        SharedPreferences.Editor prefsEditor=sharedPreferences.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.commit();
    }

    //save float data in sharedPrefrence
    public void saveFloatData(String key, float value){
        SharedPreferences.Editor prefsEditor=sharedPreferences.edit();
        prefsEditor.putFloat(key, value);
        prefsEditor.commit();
    }

    //get String data from sharedPrefrence based on the key
    public String getStringData(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, null);
        }
        return null;
    }

    //get int data from sharedPrefrence based on the key
    public int getIntData(String key){
        if(sharedPreferences!=null){
            return sharedPreferences.getInt(key,0);
        }
        return 0;
    }

    //get boolean data from sharedPrefrence based on the key
    public boolean getBooleanData(String key){
        if(sharedPreferences!=null){
            return sharedPreferences.getBoolean(key,false);
        }
        return false;
    }

    //get long data from sharedPrefrence based on the key
    public long getLongData(String key){
        if(sharedPreferences!=null){
            return sharedPreferences.getLong(key,0);
        }
        return 0;
    }

    //get float data from sharedPrefrence based on the key
    public float getFloatData(String key){
        if(sharedPreferences!=null){
            return sharedPreferences.getFloat(key,0);
        }
        return 0;
    }

    //delete data from sharedPrefrence data based on the key
    public String deleteData(String key){
        if(sharedPreferences!=null){
            sharedPreferences.edit().remove(key).commit();
            return "deleted";
        }
        return "not deleted";
    }

    //clear sharedPrefrence memory
    public void deleteSharedPrefrenceMemory(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Log.d("val","deleted");
    }

    //Get All sharedPrefrence data
    public void getAllSharedPrefrenceData(){
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }


}
