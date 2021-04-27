package com.team10.trojancheckinout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team10.trojancheckinout.model.Server;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BuildingSearchResultsFragment extends Fragment {

    private BuildingAdapter adapter;

    public BuildingSearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static BuildingSearchResultsFragment newInstance() {
        BuildingSearchResultsFragment fragment = new BuildingSearchResultsFragment();
        Bundle args = new Bundle();

        // TODO

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO
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
        // TODO

        return rootView;
    }
}