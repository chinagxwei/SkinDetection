package com.idreamspace.skindetection.app;

import android.app.Application;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.idreamspace.skindetection.net.HttpUpload;
import com.idreamspace.skindetection.net.MqttClient;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppContainer {
    private Application app;
    private HashMap<Class<?>, Object> container;

    public AppContainer(Application app) {
        this.app = app;
        this.container = new HashMap<Class<?>, Object>();
        this.container.put(ExecutorService.class, Executors.newFixedThreadPool(3));
        this.container.put(MqttClient.class, MqttClient.getInstance(this.app));
        this.container.put(HttpUpload.class, new HttpUpload());
    }

    public <T> T getComponent(Class<T> key) {
        return (T) this.container.get(key);
    }
}
