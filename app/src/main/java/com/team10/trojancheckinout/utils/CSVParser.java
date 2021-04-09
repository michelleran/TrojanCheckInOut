package com.team10.trojancheckinout.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CSVParser {

    private static final String TAG = "CSVReader" ;

    public static void readCSV() {

        Log.d(TAG, "readCSV: started");
//        Log.d(TAG, Environment.getExternalStorageDirectory().toString());
        File addresses = new File("file:///android_asset/myFile.txt");

        if (addresses != null) {
            try {
                FileReader fr = new FileReader(addresses.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            Log.d(TAG, addresses.getAbsolutePath());
        }
    }

}
