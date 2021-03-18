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

    private static final String ARG_IS_ADD = "isAdd";
//    private static final String ARG_PARAM2 = "param2";

    private boolean isAdd;
//    private String mParam2;

    private EditText edtBCname;
    private EditText edtBcMaxCap;
    private Button btnBcConfirm;
    private Button btnBcCancel;

    public BuildingChanges() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static BuildingChanges newInstance(boolean isAdd, Building building) {
        BuildingChanges fragment = new BuildingChanges();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_ADD, isAdd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAdd = getArguments().getBoolean(ARG_IS_ADD);
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

        btnBcCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });


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
                    if (isAdd) {
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
            }
        });

        return rootView;
    }

}