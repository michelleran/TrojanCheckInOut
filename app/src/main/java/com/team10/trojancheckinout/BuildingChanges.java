package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuildingChanges#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildingChanges extends Fragment {

    private static final String ARG_PARAM1 = "param1 applied";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView txtBCparam1;

    public BuildingChanges() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuildingChanges.
     */
    // TODO: Rename and change types and number of parameters
    public static BuildingChanges newInstance(String param1, String param2) {
        BuildingChanges fragment = new BuildingChanges();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_building_changes, container, false);
        txtBCparam1 = (TextView) rootView.findViewById(R.id.txtBCparam1);
        txtBCparam1.setText(mParam1);

        return rootView;
    }
}