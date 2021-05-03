package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BuildingSearchFragment extends Fragment {

    public BuildingSearchFragment() {
        // Required empty public constructor
    }

    public static BuildingSearchFragment newInstance(String param1, String param2) {
        BuildingSearchFragment fragment = new BuildingSearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_building_search, container, false);

        EditText nameField = rootView.findViewById(R.id.search_name);
        EditText currentCapacityField = rootView.findViewById(R.id.search_current);
        EditText maxCapacityField = rootView.findViewById(R.id.search_max);

        Button button = rootView.findViewById(R.id.search_button);
        button.setOnClickListener(view -> {
            int currentCapacity, maxCapacity;
            try {
                currentCapacity = Integer.parseInt(currentCapacityField.getText().toString().trim());
                maxCapacity = Integer.parseInt(maxCapacityField.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_LONG).show();
                return;
            }

            // open search results (replace this fragment)
            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.building_tab_content,
                BuildingSearchResultsFragment.newInstance(
                    nameField.getText().toString().trim().toLowerCase(),
                    currentCapacity, maxCapacity));
            ft.commit();
            ft.addToBackStack("building_search");
        });

        return rootView;
    }
}