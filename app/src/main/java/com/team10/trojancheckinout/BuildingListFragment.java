package com.team10.trojancheckinout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Listener;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.utils.BuildingSorter;
import com.team10.trojancheckinout.utils.CSVParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class BuildingListFragment extends Fragment {
    private BuildingAdapter adapter;
    private Button btnAddBuilding;
    private Button btnImportCSV;

    private static final String TAG = "BuildingListFragment";
    private static final int REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_building_list, container, false);


        // set up RecyclerView
        RecyclerView buildingList = rootView.findViewById(R.id.building_list);
        btnAddBuilding = rootView.findViewById(R.id.btnAddBuilding);
        btnImportCSV = rootView.findViewById(R.id.btnImportCSV);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        buildingList.setLayoutManager(llm);

        adapter = new BuildingAdapter(getParentFragmentManager(), null);
        buildingList.setAdapter(adapter);

        // get extant buildings, then listen for add/remove/update
        Server.listenForBuildings(adapter);

        btnAddBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.building_list_frame, BuildingChanges.newInstance("ADD", null));
                ft.commit();
                ft.addToBackStack(null);
            }
        });

        btnImportCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("text/comma-separated-values");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Import CSV"), REQUEST_CODE);

            }
        });

        Button btnSearch = rootView.findViewById(R.id.buildings_search_button);
        btnSearch.setOnClickListener(view -> {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.building_list_frame, BuildingSearchFragment.newInstance());
            ft.commit();
            ft.addToBackStack("start_building_search");
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK || data == null || data.getData() == null) {
            Log.e("VAR", "onActivityResult: Error");
        }
        else {
            try {
                CSVReader dataRead = new CSVReader(new InputStreamReader(getContext().getContentResolver().openInputStream(data.getData())));
                ArrayList<String[]> information = CSVParser.parseCSV(dataRead);
                if (information == null) {
                    //ERROR
                    new AlertDialog.Builder(getContext())
                            .setTitle("ERROR: Incorrect Format")
                            .setMessage("The CSV file provided doesn't have enough columns")
                            .setNeutralButton(android.R.string.ok, null)
                            .show();
                }
                else if (information.size() == 1 && information.get(0) != null && information.get(0)[0].equals("FE")) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ERROR: Incorrect Format")
                            .setMessage("The CSV file provided doesn't have enough columns.\n" +
                                    "Update and Add Entries must have a minimum of 3 arguments")
                            .setNeutralButton(android.R.string.ok, null)
                            .show();
                }
                else {
                    ArrayList<String> capacityErrors = new ArrayList<String>(0);
                    ArrayList<String> operationErrors = new ArrayList<String>(0);
                    for (String[] info : information) {
                        if (info[0].equals("U")) {
                            Server.getBuildingIDByName(info[1], new Callback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    String buildingIDResult = result;
                                    if (buildingIDResult != null) {
                                        if (info.length >= 3 && info[2] != null && info[2].equals("-")) {
                                            if (info.length >= 4 && info[3] != null && info[3].length() > 0) {
                                                Server.setBuildingName(buildingIDResult, info[3], new Callback<Void>() {
                                                    @Override
                                                    public void onSuccess(Void result) {
                                                        Log.d(TAG, "onSuccess: building name set");

                                                    }

                                                    @Override
                                                    public void onFailure(Exception exception) {
                                                        Log.e(TAG, "onFailure: building name set",exception );
                                                    }
                                                });
                                            }

                                        }
                                        else {
                                            if (info.length >= 3 && info[2] != null) {
                                                int maxCapacity = Integer.parseInt(info[2]);
                                                Server.setBuildingMaxCapacity(result, maxCapacity, new Callback<Building>() {
                                                    @Override
                                                    public void onSuccess(Building result) {
                                                        if (info.length >= 4 && info[3] != null && info[3].length() > 0) {
                                                            Server.setBuildingName(buildingIDResult, info[3], new Callback<Void>() {
                                                                @Override
                                                                public void onSuccess(Void result) {
                                                                    Log.d(TAG, "onSuccess: building name set");

                                                                }

                                                                @Override
                                                                public void onFailure(Exception exception) {
                                                                    Log.e(TAG, "onFailure: building name set",exception );

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Exception exception) {
                                                        Log.e(TAG, "onFailure: building update failed ", exception);
                                                    }
                                                });
                                            }

                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    Log.e(TAG, "getBuildingID failed for " + info[1], exception);
                                }
                            });
                        }
                        else if (info[0].equals("A")) {
                            if (info.length >= 3 && info[2] != null){
                                int maxCapacity = Integer.parseInt(
                                        info[2]);
                                Server.addBuilding(info[1], maxCapacity, new Callback<Building>() {
                                    @Override
                                    public void onSuccess(Building result) {
                                        Log.d(TAG, "onSuccess: " + result.getName() + " added");
                                    }

                                    @Override
                                    public void onFailure(Exception exception) {
                                        Log.e(TAG, "onFailure: building addition failed", exception);
                                    }
                                });
                            }

                        }
                        else if (info[0].equals("D")) {
                            Log.d(TAG, "onActivityResult: Going to delete!");
                            Server.getBuildingIDByName(info[1], new Callback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    if (result != null) {
                                        Server.removeBuilding(result, new Callback<Void>() {
                                            @Override
                                            public void onSuccess(Void result) { }

                                            @Override
                                            public void onFailure(Exception exception) {
                                                Log.e(TAG, "onFailure: building delete failed ", exception);
                                                new AlertDialog.Builder(getContext())
                                                    .setTitle("ERROR: Failed to delete " + info[1])
                                                    .setMessage(exception.getMessage())
                                                    .setNeutralButton(android.R.string.ok, null)
                                                    .show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    Log.e(TAG, "getBuildingID failed for " + info[1], exception);
                                }
                            });
                        }
                        else if (info[0].equals("W")) {
                            capacityErrors.add(info[1]);
                        }
                        else if (info[0].equals("OPE")) {
                            operationErrors.add(info[1]);
                        }
                    }
                    if (capacityErrors.size() > 0 || operationErrors.size() > 0) {
                        String titleError = "WARNING: ";
                        if (capacityErrors.size() > 0) {
                            titleError += "Invalid Capacities, ";
                        }
                        if (operationErrors.size() > 0) {
                            titleError += "Invalid Operations, ";
                        }
                        titleError = titleError.substring(0, titleError.length() - 2);

                        String operationsMessage = "";
                        if (operationErrors.size() > 0) {
                            operationsMessage += "The following lines have invalid operations:\n";
                            for (String operationError : operationErrors) {
                                operationsMessage += operationError + ", ";
                            }
                            operationsMessage = operationsMessage.substring(0, operationsMessage.length() - 2);
                            operationsMessage += "\n";
                        }

                        String warningMessage = "";
                        if (capacityErrors.size() > 0) {
                            int errorCount = 0;
                            for (String error : capacityErrors) {
                                if (errorCount > 30) {
                                    warningMessage += ", ...";
                                    break;
                                }
                                else {
                                    warningMessage += ", " + error;
                                    errorCount++;
                                }

                            }
                            warningMessage = warningMessage.substring(2);
                            warningMessage = "The following " + String.valueOf(capacityErrors.size())
                                    + " buildings have invalid capacities:\n"
                                    + warningMessage
                                    + "\n\nAll building capacities must be non-negative integers";
                        }


                        if (operationsMessage.length() > 0) {
                            warningMessage = operationsMessage + warningMessage;
                        }


                        new AlertDialog.Builder(getContext())
                                .setTitle(titleError)
                                .setMessage(warningMessage)
                                .setNeutralButton(android.R.string.ok, null)
                                .show();
                    }
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}

class BuildingAdapter
    extends RecyclerView.Adapter<BuildingAdapter.ViewHolder>
    implements Listener<Building>
{
    private FragmentManager fragmentManager;
    private ArrayList<String> buildingIds;
    private HashMap<String, Building> idToBuilding;
    private View empty;

    private final String TAG = "BuildingAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView buildingCurrentCapacity;
        public final TextView buildingMaximumCapacity;
        public final TextView btnBuildingEdit;
        public final TextView menuDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.building_name);
            buildingCurrentCapacity = itemView.findViewById(R.id.txtBuildingCurrentCapacity);
            buildingMaximumCapacity = itemView.findViewById(R.id.txtBuildingMaximumCapacity);
            btnBuildingEdit = itemView.findViewById(R.id.btnBuildingEdit);
            menuDelete = (TextView) itemView.findViewById(R.id.menuDelete);
        }
    }

    public BuildingAdapter(FragmentManager fragmentManager, View empty) {
        this.fragmentManager = fragmentManager;
        this.empty = empty;
        // initialize cache
        buildingIds = new ArrayList<>();
        idToBuilding = new HashMap<>();
    }

    @Override
    public void onAdd(Building item) {
        if (buildingIds.contains(item.getId())) {
            // replace building in cache
            onUpdate(item);
            return;
        }
        if (buildingIds.isEmpty() && empty != null)
            empty.setVisibility(View.GONE);
        buildingIds.add(item.getId());
        idToBuilding.put(item.getId(), item);

        // sort alphabetically
        buildingIds = BuildingSorter.sortBuilding(buildingIds, idToBuilding);
//        Collections.sort(buildingIds);
        // refresh
        notifyDataSetChanged();
    }

    @Override
    public void onRemove(Building item) {
        buildingIds.remove(item.getId());
        idToBuilding.remove(item.getId());

        //sort
        buildingIds = BuildingSorter.sortBuilding(buildingIds, idToBuilding);

        // refresh
        notifyDataSetChanged();
        if (buildingIds.isEmpty() && empty != null)
            empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdate(Building item) {
        if (!buildingIds.contains(item.getId())) {
            // add new building
            onAdd(item);
            return;
        }
        idToBuilding.put(item.getId(), item);

        //sort
        buildingIds = BuildingSorter.sortBuilding(buildingIds, idToBuilding);

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
        Building building = idToBuilding.get(buildingIds.get(position));
        holder.name.setText(building.getName());

        holder.buildingCurrentCapacity.setText("Current Capacity: " + String.valueOf(building.getCurrentCapacity()));
        holder.buildingMaximumCapacity.setText("Maximum Capacity: " + String.valueOf(building.getMaxCapacity()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open building details (replace this fragment)
                final FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.building_list_frame,
                            BuildingDetailsFragment.newInstance(building));

                ft.commit();
                ft.addToBackStack(building.getId());
            }
        });
        
        holder.btnBuildingEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 3 DOT BUTTON");
                PopupMenu popup = new PopupMenu(holder.name.getContext(), holder.btnBuildingEdit);
                popup.inflate(R.menu.options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuEdit:
                                Log.d(TAG, "onMenuItemClick: EDIT");
                                FragmentTransaction ft = fragmentManager.beginTransaction();
                                ft.replace(R.id.building_list_frame, BuildingChanges.newInstance("EDIT", building));
                                ft.commit();
                                ft.addToBackStack(building.getId());
                                return true;
                            case R.id.menuViewQR:
                                Log.d(TAG, "onMenuItemClick: VIEW QR");
                                FragmentTransaction ft1 = fragmentManager.beginTransaction();
                                ft1.replace(R.id.building_list_frame, BuildingChanges.newInstance("QR", building));
                                ft1.commit();
                                ft1.addToBackStack(building.getId());
                                return true;
                            case R.id.menuDelete:
                                Log.d(TAG, "onMenuItemClick: DELETE");
                                new AlertDialog.Builder(holder.name.getContext())
                                        .setTitle("Delete Building")
                                        .setMessage("Are you sure you want to delete this building?\nNOTE: This action is permanent")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Server.removeBuilding(building.getId(), new Callback<Void>() {
                                                    @Override
                                                    public void onSuccess(Void result) {
                                                        Log.d(TAG, "Successfully Removed Building!");
                                                        Toast.makeText(holder.name.getContext(), "Building " + building.getName() + " Successfully Deleted", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(Exception exception) {
                                                        Log.e(TAG, "Building Delete Error", exception);
                                                        Toast.makeText(holder.name.getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() { return buildingIds.size(); }
}