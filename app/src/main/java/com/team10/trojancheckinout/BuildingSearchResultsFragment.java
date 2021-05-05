package com.team10.trojancheckinout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BuildingSearchResultsFragment extends Fragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_CURRENT = "current";
    private static final String ARG_MAX = "max";

    private BuildingAdapter adapter;

    private String name;
    private int current;
    private int max;

    public BuildingSearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static BuildingSearchResultsFragment newInstance(String name, int currentCapacity, int maxCapacity) {
        BuildingSearchResultsFragment fragment = new BuildingSearchResultsFragment();
        Bundle args = new Bundle();

        args.putString(ARG_NAME, name);
        args.putInt(ARG_CURRENT, currentCapacity);
        args.putInt(ARG_MAX, maxCapacity);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            current = getArguments().getInt(ARG_CURRENT);
            max = getArguments().getInt(ARG_MAX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        // set up RecyclerView
        RecyclerView resultsList = rootView.findViewById(R.id.results_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        resultsList.setLayoutManager(llm);

        adapter = new BuildingAdapter(getParentFragmentManager());
        resultsList.setAdapter(adapter);
        Server.filterBuildings(name, current, max, new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                adapter.onAdd(result);
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
            }
        });

        return rootView;
    }
}