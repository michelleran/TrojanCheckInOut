package com.team10.trojancheckinout;

import android.app.Application;

import com.team10.trojancheckinout.model.Server;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Server.initialize();
    }
}
