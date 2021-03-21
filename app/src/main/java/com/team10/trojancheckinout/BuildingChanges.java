package com.team10.trojancheckinout;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;

import org.w3c.dom.Text;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuildingChanges#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildingChanges extends Fragment {

    private static final String ARG_REQUEST_TYPE = "requestType";
    private static final String ARG_BUILDING_ID = "buildingId";
    private static final String ARG_BUILDING_NAME = "buildingName";
    private static final String ARG_MAX_CAPACITY = "buildingMaxCapacity";
    private static final String ARG_QR_CODE = "qrCode";

//    private static final String ARG_PARAM2 = "param2";

    private String requestType;
    private String buildingId;
    private String buildingName;
    private int buildingMaxCapacity;
    private String buildingQR;
    private long lastDownload;
//    private String mParam2;

    private EditText edtBCname;
    private EditText edtBcMaxCap;
    private Button btnBcConfirm;
    private Button btnBcCancel;
    private TextView txtBcName;
    private ProgressBar pbBcLoading;
    private ImageView imgBcQR;
    private DownloadManager mgr=null;

    public BuildingChanges() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static BuildingChanges newInstance(String requestType, Building building) {
        BuildingChanges fragment = new BuildingChanges();
        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_TYPE, requestType);
        if (building != null) {
            args.putString(ARG_BUILDING_ID, building.getId());
            args.putString(ARG_BUILDING_NAME, building.getName());
            args.putInt(ARG_MAX_CAPACITY, building.getMaxCapacity());
            args.putString(ARG_QR_CODE, building.getQrCodeUrl());
        }
        else {
            args.putString(ARG_BUILDING_ID, null);
            args.putString(ARG_BUILDING_NAME, null);
            args.putInt(ARG_MAX_CAPACITY, -1);
            args.putString(ARG_QR_CODE, null);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getString(ARG_BUILDING_ID);
            requestType = getArguments().getString(ARG_REQUEST_TYPE);
            buildingName = getArguments().getString(ARG_BUILDING_NAME);
            buildingMaxCapacity = getArguments().getInt(ARG_MAX_CAPACITY);
            buildingQR = getArguments().getString(ARG_QR_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_building_changes, container, false);


        edtBCname = (EditText) rootView.findViewById(R.id.edtBcName);
        edtBcMaxCap = (EditText) rootView.findViewById(R.id.edtBcMaxCap);
        btnBcConfirm = (Button) rootView.findViewById(R.id.btnBcConfirm);
        btnBcCancel = (Button) rootView.findViewById(R.id.btnBcCancel);
        txtBcName = (TextView) rootView.findViewById(R.id.txtBcName);
        pbBcLoading = (ProgressBar) rootView.findViewById(R.id.pbBcLoading);
        imgBcQR = (ImageView) rootView.findViewById(R.id.imgBcQR);

        mgr = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);


        pbBcLoading.setVisibility(View.INVISIBLE);

        btnBcCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        if (requestType.equals("ADD")) {
            setViewAdd();
        }
        else if (requestType.equals("EDIT")) {
            setViewEdit();
        }
        else if (requestType.equals("QR")) {
            setViewQR();
        }


        return rootView;
    }

    public void setViewQR() {
        edtBCname.setVisibility(View.INVISIBLE);
        edtBcMaxCap.setVisibility(View.INVISIBLE);
        txtBcName.setVisibility(View.VISIBLE);
        imgBcQR.setVisibility(View.VISIBLE);

        btnBcConfirm.setText("Download");

        if (buildingName != null) {
            txtBcName.setText("Building: " + buildingName);
        }

        Glide.with(getActivity()).load(buildingQR).override(400, 400).centerCrop().into(imgBcQR);


        BroadcastReceiver onDownloadComplete=new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Toast.makeText(ctxt, "Download Finished", Toast.LENGTH_SHORT).show();
            }
        };

        btnBcConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(buildingQR);
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .mkdirs();
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                        .setDescription("Downloading QR Code")
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                "qr_code_" + buildingName);
                lastDownload = mgr.enqueue(req);

                getContext().registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            }
        });

    }

    public void setViewEdit() {
        edtBCname.setVisibility(View.INVISIBLE);
        imgBcQR.setVisibility(View.INVISIBLE);
        txtBcName.setVisibility(View.VISIBLE);
        edtBcMaxCap.setVisibility(View.VISIBLE);

        btnBcConfirm.setText("Confirm");


        if (buildingName != null) {
            txtBcName.setText("Building: " + buildingName);
        }
        if (buildingMaxCapacity != -1) {
            edtBcMaxCap.setText(String.valueOf(buildingMaxCapacity));
        }


        edtBcMaxCap.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (edtBcMaxCap.getText().toString().trim().equals("")){
                        edtBcMaxCap.setError("Maximum Capacity cannot be empty");
                    }
                    else if (Integer.parseInt(edtBcMaxCap.getText().toString().trim()) < 0) {
                        edtBcMaxCap.setError("Maximum Capacity cannot be negative");
                    }
                }
            }
        });

        btnBcConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtBcMaxCap.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(), "Maximum Capacity cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(edtBcMaxCap.getText().toString().trim()) < 0) {
                    Toast.makeText(getContext(), "Maximum Capacity cannot be negative", Toast.LENGTH_SHORT).show();
                }
                else {
                    int maxCapacity = Integer.parseInt(edtBcMaxCap.getText().toString().trim());
                    pbBcLoading.setVisibility(View.VISIBLE);
                    Server.setBuildingMaxCapacity(buildingId, maxCapacity, new Callback<Building>() {
                        @Override
                        public void onSuccess(Building result) {
                            pbBcLoading.setVisibility(View.INVISIBLE);
                            if (result != null) {
                                Log.d(TAG, "onSuccess: Maximum Capacity of Building " + result.getName() + " set to " + result.getMaxCapacity());
                                Toast.makeText(getContext(), "Maximum Capacity of Building " + result.getName() + " set to " +  result.getMaxCapacity(), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "onSuccess: Building is null");
                            }
                            if (getActivity() != null) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                fm.popBackStack();
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            pbBcLoading.setVisibility(View.INVISIBLE);
                            if (exception != null) {
                                Log.e(TAG, "onFailure: Building edit error: " + exception.getMessage(), exception);
                                if (exception.getMessage() != null && exception.getMessage().length() < 100) {
                                    if (getContext() != null) {
                                        Toast.makeText(getContext(), "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    if (getContext() != null) {
                                        Toast.makeText(getContext(), "Error. Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Log.e(TAG, "onFailure: Building edit error");
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Error. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    public void setViewAdd() {
        txtBcName.setVisibility(View.INVISIBLE);
        imgBcQR.setVisibility(View.INVISIBLE);
        edtBCname.setVisibility(View.VISIBLE);
        edtBcMaxCap.setVisibility(View.VISIBLE);

        btnBcConfirm.setText("Confirm");


        edtBCname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if (edtBCname.getText().toString().trim().equals("")){
                        edtBCname.setError("Building name cannot be empty");
                    }
                }
            }
        });

        edtBcMaxCap.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    if (edtBcMaxCap.getText().toString().trim().equals("")){
                        edtBcMaxCap.setError("Maximum Capacity cannot be empty");
                    }
                    else if (Integer.parseInt(edtBcMaxCap.getText().toString().trim()) < 0) {
                        edtBcMaxCap.setError("Maximum Capacity cannot be negative");
                    }
                }
            }
        });

        btnBcConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtBCname.getText().toString().trim().equals("") || edtBcMaxCap.getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), "Please fill out all of the following fields", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(edtBcMaxCap.getText().toString().trim()) < 0) {
                    Toast.makeText(getContext(), "Maximum Capacity cannot be negative", Toast.LENGTH_SHORT).show();
                }
                else {
                    String buildingName = edtBCname.getText().toString().trim();
                    int maxCapacity = Integer.parseInt(edtBcMaxCap.getText().toString().trim());
                    pbBcLoading.setVisibility(View.VISIBLE);
                    Server.addBuilding(buildingName, maxCapacity, new Callback<Building>() {
                        @Override
                        public void onSuccess(Building result) {
                            pbBcLoading.setVisibility(View.INVISIBLE);
                            if (result != null) {
                                Log.d(TAG, "onSuccess: Building with ID " + result.getId() + " added");
                            } else {
                                Log.d(TAG, "onSuccess: Building is null");
                            }
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Building " + result.getName() + " successfully added", Toast.LENGTH_SHORT).show();
                            }
                            if (getActivity() != null) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                fm.popBackStack();
                            }

                        }

                        @Override
                        public void onFailure(Exception exception) {
                            pbBcLoading.setVisibility(View.INVISIBLE);
                            if (exception != null) {
                                Log.e(TAG, "onFailure: Building add error", exception);
                            } else {
                                Log.e(TAG, "onFailure: Building add error");
                            }
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Building could not be added. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}