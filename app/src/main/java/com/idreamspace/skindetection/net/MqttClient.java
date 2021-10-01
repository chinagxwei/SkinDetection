package com.idreamspace.skindetection.net;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.idreamspace.skindetection.app.Component;
import com.idreamspace.skindetection.config.Event;
import com.idreamspace.skindetection.config.UriConfig;
import com.idreamspace.skindetection.utils.Util;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MqttClient implements Component {

    private static MqttClient instance;
    private Application app;

    private int errorCount = 0;

    //声明一个MQTT客户端对象
    private final MqttAndroidClient mMqttClient;

    private final MqttConnectOptions options;

    private final String clientId;

    private static final String TAG = "MqttClient";

    public boolean isConnect() {
        return mMqttClient.isConnected();
    }

    //单例模式
    public static MqttClient getInstance(Application app) {
        if (null == instance) {
            synchronized (MqttClient.class) {
                instance = new MqttClient(app);
            }
        }
        return instance;
    }

    private MqttClient(Application app) {
        this.app = app;
        //连接时使用的clientId, 必须唯一, 一般加时间戳
        clientId = Util.getSystemIdByCache(app.getBaseContext());
        String uri = String.format("tcp://%s:%s", UriConfig.MACHINE_REGISTER_IP, UriConfig.MACHINE_REGISTER_PORT);
        mMqttClient = new MqttAndroidClient(app.getApplicationContext(), uri, clientId);
        options = new MqttConnectOptions();
        //设置自动重连
        options.setAutomaticReconnect(false);
        // 缓存,
        options.setCleanSession(true);
        // 设置超时时间，单位：秒
        options.setConnectionTimeout(45);
        // 心跳包发送间隔，单位：秒
        options.setKeepAliveInterval(30);
        // 用户名
        options.setUserName("username");
        // 密码
        options.setPassword("password".toCharArray());
    }


    //连接到服务器
    public boolean connect() throws MqttException {
        // 设置MQTT监听
//        mMqttClient.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//                Log.d(TAG, "connectionLost: 连接断开");
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                Log.d(TAG, "消息到达");
//                String data = new String(message.getPayload(), StandardCharsets.UTF_8);
//                Log.d(TAG, data);
//                JSONObject jsonObject = new JSONObject(data);
//                if (jsonObject.has("event") && jsonObject.has("data")) {
//                    switch (jsonObject.getString("event")) {
//                        case Event.SET_QRCODE:
//                            Log.d(TAG, "设置二维码");
//                            Log.d(TAG, jsonObject.getString("data"));
//                            break;
//                        case Event.LOGIN:
//                            Log.d(TAG, "用户扫码登录");
//                            Log.d(TAG, jsonObject.getString("data"));
//                            break;
//                    }
//                }
//
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//
//            }
//        });

        //进行连接
        mMqttClient.connect(options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d(TAG, "onSuccess: 连接成功");
                try {
                    //连接成功后订阅主题
                    mMqttClient.subscribe(clientId + "-topic", 2);

                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d(TAG, "onFailure: 连接失败");
//                try {
//                    mMqttClient.disconnect();
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
            }
        });
        return true;
    }

    public void handler(MqttCallback callback) {
        mMqttClient.setCallback(callback);
    }

    public void disconnect() {
        try {
            mMqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "onFailure: 断开失败");
        }
    }
}
