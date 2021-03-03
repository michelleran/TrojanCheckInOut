package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.databinding.FragmentBuildingListBinding;
import com.team10.trojancheckinout.model.Listener;
import com.team10.trojancheckinout.model.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BuildingListFragment extends Fragment {
    //private FragmentBuildingListBinding binding;
    private BuildingListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate & bind
        View rootView = inflater.inflate(
            R.layout.fragment_building_list, container, false);

        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_building_list, container, false);

        RecyclerView buildingList = rootView.findViewById(R.id.building_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        buildingList.setLayoutManager(llm);

        adapter = new BuildingListAdapter();
        buildingList.setAdapter(adapter);

        // get extant buildings, then listen for add/remove/update
        Server.listenForBuildings(new Listener<Building>() {
            @Override
            public void onAdd(Building item) { adapter.addBuilding(item); }
            @Override
            public void onRemove(Building item) { adapter.removeBuilding(item); }
            @Override
            public void onUpdate(Building item) { adapter.updateBuilding(item); }

            @Override
            public void onFailure(Exception exception) {
                // TODO: print
            }
        });

        return rootView;
    }
}

class BuildingListAdapter extends RecyclerView.Adapter<BuildingListAdapter.ViewHolder> {
    private ArrayList<String> buildingNames;
    private HashMap<String, Building> nameToBuilding;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.building_name);
            // TODO
        }
    }

    public BuildingListAdapter() {
        // initialize cache
        buildingNames = new ArrayList<>();
        buildingNames.add("Test");
        nameToBuilding = new HashMap<>();
    }

    public void addBuilding(Building building) {
        if (buildingNames.contains(building.getName())) {
            // replace building in cache
            updateBuilding(building);
            return;
        }
        buildingNames.add(building.getName());
        nameToBuilding.put(building.getName(), building);
        // sort alphabetically
        Collections.sort(buildingNames);
        // refresh
        notifyDataSetChanged();
    }

    public void removeBuilding(Building building) {
        buildingNames.remove(building.getName());
        nameToBuilding.remove(building.getName());
        // refresh
        notifyDataSetChanged();
    }

    public void updateBuilding(Building building) {
        if (!buildingNames.contains(building.getName())) {
            // add new building
            addBuilding(building);
            return;
        }
        nameToBuilding.put(building.getName(), building);
        // refresh
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_building, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("BuildingListAdapter", "Element " + position + " set.");
        holder.name.setText(buildingNames.get(position));
        // TODO
    }

    @Override
    public int getItemCount() { return buildingNames.size(); }
}