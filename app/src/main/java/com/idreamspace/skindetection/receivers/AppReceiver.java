package com.idreamspace.skindetection.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AppReceiver extends BroadcastReceiver {
    private static final String TAG = ".AppReceiver";

    private int checkNetConnectIndex = 1;

    private MutableLiveData<Boolean> appIsConnected = new MutableLiveData<>();

    public LiveData<Boolean> getAppIsConnected() {
        return appIsConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            checkWifiStateChange(intent);
        }

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            checkNetworkStateChange(intent);
        }

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            checkConnectivity(context);
        }

    }

    @SuppressLint("LongLogTag")
    private void checkWifiStateChange(Intent intent) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                Log.d(TAG, "wifi state disabled");
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                Log.d(TAG, "wifi state disabling");
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                Log.d(TAG, "wifi state enabling");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Log.d(TAG, "wifi state enabled");
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Log.d(TAG, "wifi state unknown");
                break;
            default:
                break;


        }
    }

    @SuppressLint("LongLogTag")
    private void checkNetworkStateChange(Intent intent) {
        Parcelable parcelableExtra = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (null != parcelableExtra) {
            NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
            NetworkInfo.State state = networkInfo.getState();
            boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
            if (checkNetConnectIndex == 3) {
                Log.d(TAG, "[" + System.currentTimeMillis() + "]" + "isConnected: " + isConnected);
                appIsConnected.setValue(isConnected);
                checkNetConnectIndex = 1;
            }
            checkNetConnectIndex++;
        }
    }

    @SuppressLint("LongLogTag")
    private void checkConnectivity(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i(TAG, "CONNECTIVITY_ACTION");
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    Log.e(TAG, "当前WiFi连接可用 ");
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    Log.e(TAG, "当前移动网络连接可用 ");
                }
            } else {
                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
            }
            Log.e(TAG, "info.getTypeName(): " + activeNetwork.getTypeName());
            Log.e(TAG, "getSubtypeName(): " + activeNetwork.getSubtypeName());
            Log.e(TAG, "getState(): " + activeNetwork.getState());
            Log.e(TAG, "getDetailedState(): "
                    + activeNetwork.getDetailedState().name());
            Log.e(TAG, "getDetailedState(): " + activeNetwork.getExtraInfo());
            Log.e(TAG, "getType(): " + activeNetwork.getType());
        } else {   // not connected to the internet
            Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
        }
    }
}
