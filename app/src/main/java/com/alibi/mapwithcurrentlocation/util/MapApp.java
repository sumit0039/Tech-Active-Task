package com.alibi.mapwithcurrentlocation.util;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

@SuppressWarnings("ALL")
public class MapApp extends Application {
    private static Context mContext;
    private static ProgressDialog pDialog;

    /**
     * set Current activity context
     *
     * @param context
     */
    public static void setCurrentActvityContext(Context context) {
        mContext = context;

    }

    /**
     * get current activity context
     *
     * @return
     */
    public static Context getCurrentActivityContext() {
        return mContext;
    }

    /**
     * cancel progress dialog if shown
     */
    public static void hideProgressDialog() {
        try {
            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception exception) {
            Log.e("hiding progress dialog", exception.getMessage());
        }
    }

    /**
     * shows progress dialog
     */
//    public static void showProgressDialog() {
//        try {
//            if (pDialog != null)
//                pDialog.dismiss();
//
//            pDialog = new ProgressDialog(getCurrentActivityContext(), R.style.MyAlertDialogStyle);
//            try {
//                pDialog.show();
//            } catch (WindowManager.BadTokenException e) {
//
//            }
//            pDialog.setCancelable(false);
//            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            pDialog.setContentView(R.layout.prograss_bar_dialog);
//            if (!pDialog.isShowing()) {
//                pDialog.show();
//            }
//        } catch (Exception exception) {
//            Log.e("progressdial", exception.getMessage());
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
