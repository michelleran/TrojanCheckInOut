package com.team10.trojancheckinout.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CSVParser {

    private static final String TAG = "CSVParser";
    public static final String CAPACITY_REGEX = "^-$|^[0-9]+$";

    public static ArrayList<String[]> parseCSV(CSVReader data) {
        ArrayList<String[]> information = new ArrayList<String[]>();
        if (data == null) {
            return null;
        }
        try {
            String[] nextLine = data.readNext();
            if (nextLine == null || nextLine.length < 2) {
                return null;
            }
            while (nextLine != null) {
                String opType = nextLine[0];
                if (opType.equals("U") || opType.equals("A")) {

                    Log.d(TAG, "parseCSV: Update or Add");
                    String [] entry = new String [4];
                    if (nextLine.length >= 3) {
                        entry[0] = opType;
                        entry[1] = nextLine[1];
                        boolean valid = Pattern.matches(CAPACITY_REGEX, nextLine[2]);
                        if (valid) {
                            entry[2] = nextLine[2];
                            Log.d(TAG, entry[2]);
                        } else {
                            entry[0] = "W";
                            entry[2] = null;
                            Log.d(TAG, "parseCSV: error");
                        }
                        if (nextLine.length >= 4) {
                            entry[3] = nextLine[3];
                        }
                        information.add(entry);
                    }
                    else {
                        ArrayList<String[]> error = new ArrayList<String[]>();
                        String [] err = new String[1];
                        err[0] = "FE";
                        error.add(err);
                        return error;
                    }

                }

                else if (opType.equals("D")) {
                    Log.d(TAG, "parseCSV: Delete");
                    String [] entry = new String [4];
                    entry[0] = opType;
                    entry[1] = nextLine[1];
                    information.add(entry);
                }
                nextLine = data.readNext();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "parseCSV: info " + information.size());

        return information;
    }

}
