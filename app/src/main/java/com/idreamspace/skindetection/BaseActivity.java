package com.idreamspace.skindetection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.idreamspace.skindetection.receivers.AppReceiver;
import com.idreamspace.skindetection.utils.Util;

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = "MainActivity.";

    protected boolean actionBarIsShow = false;

    protected BroadcastReceiver appReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Handler mHandler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            // process incoming messages here
            switch (msg.what) {
                case 0:
                    hidAppWindow();
                    break;
                case 1:
                    Log.d(TAG, Integer.toString(msg.arg1));
                    break;
                case 2:
                    Log.d(TAG, Integer.toString(msg.arg1));
                    break;
            }
        }
    };

    protected void hidAppWindow() {
        ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.hide();
        }
        Util.hideSystemUI(this);
        this.actionBarIsShow = false;
    }

    protected void showAppWindow() {
        getSupportActionBar().show();
        if (!this.actionBarIsShow) {
            mHandler.sendEmptyMessageDelayed(0, 4500);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.startActivity(new Intent(Settings.ACTION_SETTINGS));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void registerReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.appReceiver = new AppReceiver();
        this.registerReceiver(this.appReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.appReceiver);
    }
}
