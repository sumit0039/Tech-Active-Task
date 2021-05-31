package com.alibi.mapwithcurrentlocation.model;

import java.time.LocalTime;

public class UpdatedLocations {

    private Double latitude;
    private Double longitude;
    private LocalTime time;

    public UpdatedLocations(Double latitude, Double longitude, LocalTime time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    @Override
    public String toString() {
        return "UpdatedLocations{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                '}';
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
