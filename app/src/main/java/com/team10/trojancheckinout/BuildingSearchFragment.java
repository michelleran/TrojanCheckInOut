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

    public static BuildingSearchFragment newInstance() {
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

        // TODO: more useful if we can search less/greater than capacity

        Button button = rootView.findViewById(R.id.search_button);
        button.setOnClickListener(view -> {
            int currentCapacity = -1;
            int maxCapacity = -1;
            try {
                currentCapacity = Integer.parseInt(currentCapacityField.getText().toString().trim());
            } catch (NumberFormatException e) {
                // ignore
            }

            try {
                maxCapacity = Integer.parseInt(maxCapacityField.getText().toString().trim());
            } catch (NumberFormatException e) {
                // ignore
            }

            // open search results (replace this fragment)
            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.building_list_frame,
                BuildingSearchResultsFragment.newInstance(
                    nameField.getText().toString().trim(),
                    currentCapacity, maxCapacity));
            ft.commit();
            ft.addToBackStack("building_search");
        });

        return rootView;
    }
}