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
        try {
            Server.addBuilding("film", 25, new Callback<Building>() {
                @Override
                public void onSuccess(Building result) {
                    Log.d("server", "Hi");

                }

                @Override
                public void onFailure(Exception exception) {
                    exception.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}
