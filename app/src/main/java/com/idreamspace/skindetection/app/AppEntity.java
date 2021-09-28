package com.idreamspace.skindetection.app;

import android.app.Application;

import com.idreamspace.skindetection.net.MqttClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppEntity extends Application {
    private static AppEntity app;

    private static AppContainer container;

    public static AppEntity getInstance() {
        return app;
    }

    public <T> T getComponent(Class<T> key) {
        return container.getComponent(key);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        container = new AppContainer(this);
    }

}
