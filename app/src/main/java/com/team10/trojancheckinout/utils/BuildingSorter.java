package com.team10.trojancheckinout.utils;

import android.util.Log;

import com.team10.trojancheckinout.model.Building;

import java.util.ArrayList;
import java.util.HashMap;

public class BuildingSorter {
    private static final String TAG = "BuildingSorter";

    public static ArrayList<String> sortBuilding(ArrayList<String> buildingIds, HashMap<String, Building> idToBuilding) {
        int numElements = buildingIds.size();
        for (int i = 0; i < numElements; i++) {
            int min_index = findMin(i, buildingIds, idToBuilding);
            buildingIds = swap_elements(i, min_index, buildingIds);
        }

        return buildingIds;
    }

    private static int findMin(int start_index, ArrayList<String> buildingIds, HashMap<String, Building> idToBuilding) {
        int numElements = buildingIds.size();

        int min_id = start_index;
        String min_name = "";
        Building minBuilding = idToBuilding.get(buildingIds.get(min_id));
        if (minBuilding != null) {
            min_name = minBuilding.getName();
        }

        if (start_index + 1 < numElements) {

            for (int i = start_index + 1; i < numElements; i++) {
                Building building = idToBuilding.get(buildingIds.get(i));
                if (building != null) {
                    String buildingName = building.getName();
                    if (buildingName.compareTo(min_name) < 0) {
                        min_id = i;
                        min_name = buildingName;
                    }
                }

            }

        }

        return min_id;

    }

    public static ArrayList<String> swap_elements (int ind1, int ind2, ArrayList<String> buildingIds) {
      ArrayList<String> result = buildingIds;
      String elem1 = result.get(ind1);
      result.set(ind1, result.get(ind2));
      result.set(ind2, elem1);
      return result;
    }
}
