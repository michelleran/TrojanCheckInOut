package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Use the {@link BuildingDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildingDetailsFragment extends Fragment {
    private static final String ARG_BUILDING_ID = "buildingId";
    private String buildingId;

    /**
     * Don't use! Use {@link BuildingDetailsFragment#newInstance} instead.
     */
    public BuildingDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static BuildingDetailsFragment newInstance(String buildingId) {
        BuildingDetailsFragment fragment = new BuildingDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUILDING_ID, buildingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getString(ARG_BUILDING_ID);
            Log.d("BuildingFragment", buildingId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_building_details, container, false);
    }
}