package com.team10.trojancheckinout;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.databinding.FragmentBuildingListBinding;

public class BuildingListFragment extends Fragment {
    private FragmentBuildingListBinding binding;
    private BuildingListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_building_list, container, false);

        adapter = new BuildingListAdapter(getContext());
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_building_list, container, false);
        binding.buildingList.setAdapter(adapter);

        // TODO: listen for buildings

        return rootView;
    }
}

class BuildingListAdapter extends BaseAdapter {
    private Context context;
    // TODO: list/map of buildings, sorted alphabetically

    public BuildingListAdapter(Context context) {
        this.context = context;
        // TODO: initialize list/map of buildings
    }

    // TODO

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View buildingRow = inflater.inflate(R.layout.row_building, viewGroup, false);

        // TODO: populate data, attach listeners

        return buildingRow;
    }
}