package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_MAJOR = "major";
    private static final String ARG_BUILDING = "building";

    private static final String ARG_START_YEAR = "startYear";
    private static final String ARG_START_MONTH = "startMonth";
    private static final String ARG_START_DAY = "startDay";
    private static final String ARG_START_HOUR = "startHour";
    private static final String ARG_START_MIN = "startMin";

    private static final String ARG_END_YEAR = "endYear";
    private static final String ARG_END_MONTH = "endMonth";
    private static final String ARG_END_DAY = "endDay";
    private static final String ARG_END_HOUR = "endHour";
    private static final String ARG_END_MIN = "endMin";

    private String name;
    private String major;
    private String building;

    private int startYear = -1;
    private int startMonth = -1;
    private int startDay = -1;
    private int startHour = -1;
    private int startMin = -1;

    private int endYear = -1;
    private int endMonth = -1;
    private int endDay = -1;
    private int endHour = -1;
    private int endMin = -1;

    private StudentAdapter adapter;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static SearchResultsFragment newInstance(String name, String major, String building,
                                                    int startYear, int startMonth, int startDay, int startHour, int startMin,
                                                    int endYear, int endMonth, int endDay, int endHour, int endMin) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();

        args.putString(ARG_NAME, name);
        args.putString(ARG_MAJOR, major);
        args.putString(ARG_BUILDING, building);

        args.putInt(ARG_START_YEAR, startYear);
        args.putInt(ARG_START_MONTH, startMonth);
        args.putInt(ARG_START_DAY, startDay);
        args.putInt(ARG_START_HOUR, startHour);
        args.putInt(ARG_START_MIN, startMin);

        args.putInt(ARG_END_YEAR, endYear);
        args.putInt(ARG_END_MONTH, endMonth);
        args.putInt(ARG_END_DAY, endDay);
        args.putInt(ARG_END_HOUR, endHour);
        args.putInt(ARG_END_MIN, endMin);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            major = getArguments().getString(ARG_MAJOR);
            building = getArguments().getString(ARG_BUILDING);

            startYear = getArguments().getInt(ARG_START_YEAR);
            startMonth = getArguments().getInt(ARG_START_MONTH);
            startDay = getArguments().getInt(ARG_START_DAY);
            startHour = getArguments().getInt(ARG_START_HOUR);
            startMin = getArguments().getInt(ARG_START_MIN);

            endYear = getArguments().getInt(ARG_END_YEAR);
            endMonth = getArguments().getInt(ARG_END_MONTH);
            endDay = getArguments().getInt(ARG_END_DAY);
            endHour = getArguments().getInt(ARG_END_HOUR);
            endMin = getArguments().getInt(ARG_END_MIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        // set up RecyclerView
        RecyclerView resultsList = rootView.findViewById(R.id.results_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        resultsList.setLayoutManager(llm);

        adapter = new StudentAdapter(true);
        resultsList.setAdapter(adapter);
        Server.searchStudents(name, major, building,
            startYear, startMonth, startDay, startHour, startMin,
            endYear, endMonth, endDay, endHour, endMin, adapter);

        return rootView;
    }
}