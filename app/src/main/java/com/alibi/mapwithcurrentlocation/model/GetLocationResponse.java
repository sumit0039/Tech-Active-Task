package com.alibi.mapwithcurrentlocation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetLocationResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("locations")
    @Expose
    private List<Location> locations = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

}
