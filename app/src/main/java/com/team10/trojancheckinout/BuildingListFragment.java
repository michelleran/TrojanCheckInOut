package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

        adapter = new BuildingAdapter();
        buildingList.setAdapter(adapter);

        // get extant buildings, then listen for add/remove/update
        Server.listenForBuildings(adapter);

        // MARK: for testing
        /*RecordAdapter a = new RecordAdapter();
        buildingList.setAdapter(a);
        Server.searchHistory(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, "", new Callback<Record>() {
            @Override
            public void onSuccess(Record result) {
                a.addRecord(result);
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });*/

        return rootView;
    }
}

class BuildingAdapter
    extends RecyclerView.Adapter<BuildingAdapter.ViewHolder>
    implements Listener<Building>
{
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

    public BuildingAdapter() {
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
        buildingNames.add(item.getName());
        nameToBuilding.put(item.getName(), item);
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
        Log.d(TAG, "Element " + position + " set.");
        holder.name.setText(buildingNames.get(position));
        // TODO
    }

    @Override
    public int getItemCount() { return buildingNames.size(); }
}