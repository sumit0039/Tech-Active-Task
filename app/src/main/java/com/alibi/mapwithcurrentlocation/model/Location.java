package com.alibi.mapwithcurrentlocation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("_time")
    @Expose
    private String time;

    public Location(String lat, String lng, String time) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Location{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
