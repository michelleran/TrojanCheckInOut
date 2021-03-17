package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Listener;
import com.team10.trojancheckinout.model.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BuildingListFragment extends Fragment {
    private BuildingAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_building_list, container, false);

        // set up RecyclerView
        RecyclerView buildingList = rootView.findViewById(R.id.building_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        buildingList.setLayoutManager(llm);

        adapter = new BuildingAdapter(getFragmentManager());
        buildingList.setAdapter(adapter);

        // get extant buildings, then listen for add/remove/update
        Server.listenForBuildings(adapter);

        return rootView;
    }
}

class BuildingAdapter
    extends RecyclerView.Adapter<BuildingAdapter.ViewHolder>
    implements Listener<Building>
{
    private FragmentManager fragmentManager;
    private ArrayList<String> buildingNames;
    private HashMap<String, Building> nameToBuilding;

    private final String TAG = "BuildingAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.building_name);
            // TODO
        }
    }

    public BuildingAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        // initialize cache
        buildingNames = new ArrayList<>();
        nameToBuilding = new HashMap<>();
    }

    @Override
    public void onAdd(Building item) {
        if (buildingNames.contains(item.getName())) {
            // replace building in cache
            onUpdate(item);
            return;
        }
        Building building = new Building("2", "KAP", "blah", 12);

        buildingNames.add(item.getName());
        buildingNames.add(building.getName());
        nameToBuilding.put(item.getName(), item);
        nameToBuilding.put(building.getName(), building);
        // sort alphabetically
        Collections.sort(buildingNames);
        // refresh
        notifyDataSetChanged();
    }

    @Override
    public void onRemove(Building item) {
        buildingNames.remove(item.getName());
        nameToBuilding.remove(item.getName());
        // refresh
        notifyDataSetChanged();
    }

    @Override
    public void onUpdate(Building item) {
        if (!buildingNames.contains(item.getName())) {
            // add new building
            onAdd(item);
            return;
        }
        nameToBuilding.put(item.getName(), item);
        // refresh
        notifyDataSetChanged();
    }

    @Override
    public void onFailure(Exception exception) {
        Log.e(TAG, exception.getMessage());
        // don't need to do anything
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
        Building building = nameToBuilding.get(buildingNames.get(position));
        holder.name.setText(building.getName());
        // TODO

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open building details (replace this fragment)
                final FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.building_tab_content,
                            BuildingDetailsFragment.newInstance(building));
                ft.commit();
                ft.addToBackStack(building.getId());
            }
        });
    }

    @Override
    public int getItemCount() { return buildingNames.size(); }
}