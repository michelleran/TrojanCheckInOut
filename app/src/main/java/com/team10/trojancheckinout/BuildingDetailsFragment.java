package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;

import java.util.Collections;

/**
 * Use the {@link BuildingDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildingDetailsFragment extends Fragment {
    private static final String ARG_BUILDING_ID = "buildingId";
    private static final String ARG_BUILDING_NAME = "buildingName";

    private String buildingId;
    private String buildingName;
    private CheckedInStudentAdapter adapter;

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
    public static BuildingDetailsFragment newInstance(Building building) {
        BuildingDetailsFragment fragment = new BuildingDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUILDING_ID, building.getId());
        args.putString(ARG_BUILDING_NAME, building.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getString(ARG_BUILDING_ID);
            buildingName = getArguments().getString(ARG_BUILDING_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_building_details, container, false);

        // set building name
        TextView buildingNameView = rootView.findViewById(R.id.building_details_name);
        buildingNameView.setText(buildingName);

        // TODO: show current/max capacity

        // set up RecyclerView
        RecyclerView recordList = rootView.findViewById(R.id.building_details_students);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recordList.setLayoutManager(llm);

        adapter = new CheckedInStudentAdapter();
        recordList.setAdapter(adapter);

        Server.searchHistory(
            -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1,
            buildingName, -1, "", new Callback<Record>() {
            @Override
            public void onSuccess(Record result) {
                adapter.addRecord(result);
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO
            }
        });

        return rootView;
    }
}

class CheckedInStudentAdapter extends RecordAdapter {
    @Override
    public void addRecord(Record record) {
        if (record.getCheckIn()) {
            super.addRecord(record);
            return;
        }
        // a student checked out
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getStudentId().equals(record.getStudentId())) {
                // remove corresponding check-in record
                records.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }
}