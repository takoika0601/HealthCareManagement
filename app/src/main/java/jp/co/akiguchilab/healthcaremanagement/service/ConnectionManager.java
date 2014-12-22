package jp.co.akiguchilab.healthcaremanagement.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionManager {
    private static final String TAG = ConnectionManager.class.getSimpleName();

    private ConnectivityManager connectivityManager = null;

    public ConnectionManager(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isConnecting() {
        boolean result = false;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                result = true;
            }
        }
        return result;
    }
}
