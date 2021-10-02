package com.idreamspace.skindetection.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.idreamspace.skindetection.receivers.AppReceiver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class Util {

    public final static String UTIL_TAG = "Util.";

    public static String getFileContent(String filename, Context context) {
        try {
            byte[] bytes = new byte[16];
            FileInputStream fip = context.openFileInput(filename);
            int len;
            String uuid = "";
            while ((len = fip.read(bytes)) != -1) {
                uuid = new String(bytes, 0, len, StandardCharsets.UTF_8);
            }
            fip.close();
            return uuid;
        } catch (IOException e) {
            return "";
        }
    }

    public static String setFileContent(String filename, Context context) {
        try {
            String uniqueID = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(uniqueID.getBytes(StandardCharsets.UTF_8));
            fos.close();
            return uniqueID;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getSystemIdByCache(Context context) {
        String filename = "system_id";
        String id = Util.getFileContent(filename, context);
        if (id.equals("")) {
            id = Util.setFileContent(filename, context);
        }
        return "skin-" + id;
    }

    public static void hideSystemUI(Activity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public static void showSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
