package com.team10.trojancheckinout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FilterResultsFragment extends Fragment {
    private final String TAG = "FilterResultsFragment";

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

    private static final String ARG_BUILDING_NAME = "buildingName";
    private static final String ARG_STUDENT_ID = "studentId";
    private static final String ARG_MAJOR = "major";

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

    private String buildingName;
    private String studentId;
    private String major;

    @VisibleForTesting
    RecyclerView resultsList;

    private RecordAdapter adapter;

    public FilterResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static FilterResultsFragment newInstance(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                                    int endYear, int endMonth, int endDay, int endHour, int endMin,
                                                    String buildingName, String studentId, String major) {
        FilterResultsFragment fragment = new FilterResultsFragment();
        Bundle args = new Bundle();

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

        args.putString(ARG_BUILDING_NAME, buildingName);
        args.putString(ARG_STUDENT_ID, studentId);
        args.putString(ARG_MAJOR, major);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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

            buildingName = getArguments().getString(ARG_BUILDING_NAME);
            studentId = getArguments().getString(ARG_STUDENT_ID);
            major = getArguments().getString(ARG_MAJOR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_filter_results, container, false);

        // set up RecyclerView
        resultsList = rootView.findViewById(R.id.results_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        resultsList.setLayoutManager(llm);

        adapter = new RecordAdapter();
        resultsList.setAdapter(adapter);

        Log.d(TAG, String.format("%d/%d/%d %02d:%02d - %d/%d/%d %02d:%02d, %s, %s, %s", startYear, startMonth, startDay, startHour, startMin, endYear, endMonth, endDay, endHour, endMin, buildingName, studentId, major));

        Server.filterRecords(startYear, startMonth, startDay, startHour, startMin,
            endYear, endMonth, endDay, endHour, endMin,
            buildingName, studentId, major, new Callback<Record>() {
                @Override
                public void onSuccess(Record result) {
                    adapter.addRecord(result);
                }

                @Override
                public void onFailure(Exception exception) {
                    Log.e(TAG, exception.getMessage());
                    // TODO: handle
                }
            });

        return rootView;
    }
}