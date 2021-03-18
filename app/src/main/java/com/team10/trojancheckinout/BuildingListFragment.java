package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Listener;
import com.team10.trojancheckinout.model.Manager;
import com.team10.trojancheckinout.model.Server;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BuildingListFragment extends Fragment {
    private BuildingAdapter adapter;
    private Button btnAddBuilding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_building_list, container, false);



        // set up RecyclerView
        RecyclerView buildingList = rootView.findViewById(R.id.building_list);
        btnAddBuilding = rootView.findViewById(R.id.btnAddBuilding);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        buildingList.setLayoutManager(llm);

        adapter = new BuildingAdapter(getParentFragmentManager());
        buildingList.setAdapter(adapter);

        // get extant buildings, then listen for add/remove/update
        Server.listenForBuildings(adapter);

        btnAddBuilding.setVisibility(View.VISIBLE);

        btnAddBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.building_tab_content, BuildingChanges.newInstance(true, null));
                ft.commit();
                ft.addToBackStack(null);

//                ManagerProfileFragment nextFrag= new ManagerProfileFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.building_tab_content, nextFrag, "findThisFragment")
//                        .addToBackStack(null)
//                        .commit();
            }
        });

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
        public final TextView buildingCurrentCapacity;
        public final TextView buildingMaximumCapacity;
//        public final Button btnAddBuilding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.building_name);
            // TODO
            buildingCurrentCapacity = itemView.findViewById(R.id.txtBuildingCurrentCapacity);
            buildingMaximumCapacity = itemView.findViewById(R.id.txtBuildingMaximumCapacity);
//            btnAddBuilding = itemView.findViewById(R.id.btnAddBuilding);

//            btnAddBuilding.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("Hey", "onClick: things");
//                }
//            });
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
        //TODO: remove these lines (for testing purposes)
        Building building = new Building("2", "KAP", "blah", 12);
        building.setMaxCapacity(12);

        buildingNames.add(item.getName());
        nameToBuilding.put(item.getName(), item);

        buildingNames.add(building.getName());
        nameToBuilding.put(building.getName(), building);

        Building building2 = new Building("2", "KAP", "blah", 12);
        building2.setMaxCapacity(12);
        buildingNames.add(building2.getName());
        nameToBuilding.put(building2.getName(), building2);

        Building building3 = new Building("2", "KAP", "blah", 12);
        building3.setMaxCapacity(12);
        buildingNames.add(building3.getName());
        nameToBuilding.put(building3.getName(), building3);

        Building building4 = new Building("2", "KAP", "blah", 12);
        building4.setMaxCapacity(12);
        buildingNames.add(building4.getName());
        nameToBuilding.put(building4.getName(), building4);

        Building building5 = new Building("2", "KAP", "blah", 12);
        building5.setMaxCapacity(12);
        buildingNames.add(building5.getName());
        nameToBuilding.put(building5.getName(), building5);

        Building building6 = new Building("2", "KAP", "blah", 12);
        building6.setMaxCapacity(12);
        buildingNames.add(building6.getName());
        nameToBuilding.put(building6.getName(), building6);

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
        holder.buildingCurrentCapacity.setText("Current Capacity: " + String.valueOf(building.getCurrentCapacity()));
        holder.buildingMaximumCapacity.setText("Maximum Capacity: " + String.valueOf(building.getMaxCapacity()));

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