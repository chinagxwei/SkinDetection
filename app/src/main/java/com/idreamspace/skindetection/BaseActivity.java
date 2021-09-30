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
import androidx.lifecycle.ViewModelProvider;

import com.idreamspace.skindetection.app.AppEntity;
import com.idreamspace.skindetection.config.Event;
import com.idreamspace.skindetection.lifecycle.AppVIewModel;
import com.idreamspace.skindetection.net.MqttClient;
import com.idreamspace.skindetection.receivers.AppReceiver;
import com.idreamspace.skindetection.utils.Util;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = "MainActivity.";

    protected boolean actionBarIsShow = false;

    protected AppReceiver appReceiver;

    protected AppVIewModel model;

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver();
        this.connectMqttServer();
        this.handlerMqttMessage();
        model = new ViewModelProvider(this).get(AppVIewModel.class);
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
        this.appReceiver.getAppIsConnected().observe(this, isConnected -> {
            Log.d(TAG, "receiver network type: " + isConnected);
            if (!isConnected) {
                connectMqttServer();
            }
        });
    }

    protected void connectMqttServer() {
        AppEntity app = (AppEntity) getApplication();
        MqttClient mqttClient = app.getComponent(MqttClient.class);
        try {
            Log.d(TAG, "is connect: " + mqttClient.isConnect());
            if (!mqttClient.isConnect()) {
                mqttClient.connect();
            }
        } catch (MqttException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    protected void disconnectMqttServer() {
        AppEntity app = (AppEntity) getApplication();
        MqttClient mqttClient = app.getComponent(MqttClient.class);
        if (mqttClient.isConnect()) {
            mqttClient.disconnect();
        }
    }

    protected void handlerMqttMessage() {
        AppEntity app = (AppEntity) getApplication();
        MqttClient mqttClient = app.getComponent(MqttClient.class);
        mqttClient.handler(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connectionLost: 连接断开");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "消息到达");
                String data = new String(message.getPayload(), StandardCharsets.UTF_8);
                Log.d(TAG, data);
                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.has("event") && jsonObject.has("data")) {
                    switch (jsonObject.getString("event")) {
                        case Event.SET_QRCODE:
                            Log.d(TAG, "设置二维码");
                            String qrcode = jsonObject.getString("data");
                            Log.d(TAG, qrcode);
                            model.setQrcode(qrcode);
                            break;
                        case Event.LOGIN:
                            Log.d(TAG, "用户扫码登录");
                            String openid = jsonObject.getString("data");
                            Log.d(TAG, openid);
                            model.setOpenid(openid);
                            break;
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(this.appReceiver);
        this.disconnectMqttServer();
        super.onDestroy();
    }
}
