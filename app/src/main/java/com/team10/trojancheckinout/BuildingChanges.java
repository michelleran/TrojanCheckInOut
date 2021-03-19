package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

//    private static final String ARG_PARAM2 = "param2";

    private String requestType;
    private String buildingId;
    private String buildingName;
    private int buildingMaxCapacity;
//    private String mParam2;

    private EditText edtBCname;
    private EditText edtBcMaxCap;
    private Button btnBcConfirm;
    private Button btnBcCancel;
    private TextView txtBcName;

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
        }
        else {
            args.putString(ARG_BUILDING_ID, null);
            args.putString(ARG_BUILDING_NAME, null);
            args.putInt(ARG_MAX_CAPACITY, -1);
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

    public void setViewEdit() {
        edtBCname.setVisibility(View.INVISIBLE);
        txtBcName.setVisibility(View.VISIBLE);
        btnBcCancel.setText("CANCEL");

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
                    Server.setBuildingMaxCapacity(buildingId, maxCapacity, new Callback<Building>() {
                        @Override
                        public void onSuccess(Building result) {
                            if (result != null) {
                                Log.d(TAG, "onSuccess: Maximum Capacity of Building " + result.getName() + " set to " + result.getMaxCapacity());
                            } else {
                                Log.d(TAG, "onSuccess: Building is null");
                            }
                            Toast.makeText(getContext(), "Maximum Capacity of Building " + result.getName() + " set to " +  result.getMaxCapacity(), Toast.LENGTH_SHORT).show();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            fm.popBackStack();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            if (exception != null) {
                                Log.e(TAG, "onFailure: Building add error", exception);
                            } else {
                                Log.e(TAG, "onFailure: Building add error");
                            }
                            Toast.makeText(getContext(), "Building could not be added. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    public void setViewAdd() {
        txtBcName.setVisibility(View.INVISIBLE);
        edtBCname.setVisibility(View.VISIBLE);
        btnBcCancel.setText("CANCEL");

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
                    Server.addBuilding(buildingName, maxCapacity, new Callback<Building>() {
                        @Override
                        public void onSuccess(Building result) {
                            if (result != null) {
                                Log.d(TAG, "onSuccess: Building with ID " + result.getId() + " added");
                            } else {
                                Log.d(TAG, "onSuccess: Building is null");
                            }
                            Toast.makeText(getContext(), "Building " + result.getName() + " successfully added", Toast.LENGTH_SHORT).show();
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            fm.popBackStack();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            if (exception != null) {
                                Log.e(TAG, "onFailure: Building add error", exception);
                            } else {
                                Log.e(TAG, "onFailure: Building add error");
                            }
                            Toast.makeText(getContext(), "Building could not be added. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void setViewQR() {

    }
}