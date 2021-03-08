package com.team10.trojancheckinout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;

import java.util.Calendar;
import java.util.Locale;

public class FilterFragment extends Fragment {
    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;

    private final String DATE_FORMAT = "%02d/%02d/%d";
    private final String TIME_FORMAT = "%02d:%02d";

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
    private int studentId;
    private String major;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_filter, container, false);

        // get fields
        startDate = rootView.findViewById(R.id.start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show date picker dialog
                final Calendar cal = Calendar.getInstance();
                DatePickerDialog picker = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // save values
                        startYear = year;
                        startMonth = month;
                        startDay = day;
                        // update field
                        startDate.setText(String.format(Locale.US, DATE_FORMAT, year, month, day));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_WEEK));
                picker.show();
            }
        });

        startTime = rootView.findViewById(R.id.start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                TimePickerDialog picker = new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        // save values
                        startHour = hour;
                        startMin = min;
                        // update field
                        startTime.setText(String.format(Locale.US, TIME_FORMAT, hour, min));
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
                picker.show();
            }
        });

        endDate = rootView.findViewById(R.id.end_date);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show date picker dialog
                final Calendar cal = Calendar.getInstance();
                DatePickerDialog picker = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // save values
                        endYear = year;
                        endMonth = month;
                        endDay = day;
                        // update field
                        endDate.setText(String.format(Locale.US, DATE_FORMAT, year, month, day));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_WEEK));
                picker.show();
            }
        });

        endTime = rootView.findViewById(R.id.end_time);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal = Calendar.getInstance();
                TimePickerDialog picker = new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        // save values
                        endHour = hour;
                        endMin = min;
                        // update field
                        endTime.setText(String.format(Locale.US, TIME_FORMAT, hour, min));
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
                picker.show();
            }
        });

        EditText buildingNameField = rootView.findViewById(R.id.filter_building_field);
        EditText studentIdField = rootView.findViewById(R.id.filter_student_id_field);

        String[] majors = Server.getMajors();
        Spinner spinner = rootView.findViewById(R.id.filter_major_spinner);
        spinner.setAdapter(
            new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, majors));
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                major = majors[pos];
            }
        });

        Button filter = rootView.findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get remaining inputs
                buildingName = buildingNameField.getText().toString();
                studentId = Integer.parseInt(studentIdField.getText().toString());

                // TODO: validate

                // open filter results (replace this fragment)
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.filter_tab_content,
                    FilterResultsFragment.newInstance(
                        startYear, startMonth, startDay, startHour, startMin,
                        endYear, endMonth, endDay, endHour, endMin,
                        buildingName, studentId, major));
                ft.commit();
                ft.addToBackStack("filter");
            }
        });

        return rootView;
    }
}

class FilterResultsFragment extends Fragment {
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
    private int studentId;
    private String major;

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
                                                    String buildingName, int studentId, String major) {
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
        args.putInt(ARG_STUDENT_ID, studentId);
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
            studentId = getArguments().getInt(ARG_STUDENT_ID);
            major = getArguments().getString(ARG_MAJOR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_filter_results, container, false);

        // set up RecyclerView
        RecyclerView resultsList = rootView.findViewById(R.id.results_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        resultsList.setLayoutManager(llm);

        adapter = new RecordAdapter();
        resultsList.setAdapter(adapter);

        Log.d(TAG, String.format("%d/%d/%d %02d:%02d - %d/%d/%d %02d:%02d, %s, %d, %s", startYear, startMonth, startDay, startHour, startMin, endYear, endMonth, endDay, endHour, endMin, buildingName, studentId, major));

        Server.searchHistory(startYear, startMonth, startDay, startHour, startMin,
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