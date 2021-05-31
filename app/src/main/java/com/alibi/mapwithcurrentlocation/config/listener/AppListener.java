package com.alibi.mapwithcurrentlocation.config.listener;

import com.alibi.mapwithcurrentlocation.model.GetLocationResponse;
import com.alibi.mapwithcurrentlocation.model.UpdateLocationResponse;
import com.alibi.mapwithcurrentlocation.response.LogInResponsePage;
import com.alibi.mapwithcurrentlocation.response.LogOutResponsePage;

public class AppListener {

    public interface OnLoggedIn {
        void onSuccess(LogInResponsePage logInResponsePageListener);

        void onFailure(String message);
    }

    public interface OnLoggedOut {
        void onSuccess(LogOutResponsePage logOutResponsePageListener);

        void onFailure(String message);
    }

    public interface OnUpdateLocation {
        void onSuccess(UpdateLocationResponse updateLocationResponseListener);

        void onFailure(String message);
    }

    public interface OnGetLocation {
        void onSuccess(GetLocationResponse getLocationResponse);

        void onFailure(String message);
    }
}
