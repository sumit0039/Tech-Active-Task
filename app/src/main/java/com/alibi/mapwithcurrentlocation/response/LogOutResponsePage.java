package com.alibi.mapwithcurrentlocation.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogOutResponsePage {

    @SerializedName("success")
    @Expose
    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
