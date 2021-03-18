package com.team10.trojancheckinout;

import android.app.Application;
import android.util.Log;

import com.google.zxing.WriterException;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;

import java.io.IOException;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Server.initialize();
    }
}
