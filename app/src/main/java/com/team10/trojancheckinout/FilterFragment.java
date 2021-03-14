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
    private long studentId;
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

        Spinner spinner = rootView.findViewById(R.id.filter_major_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.majors, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        Button filter = rootView.findViewById(R.id.filter_button);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get remaining inputs
                buildingName = buildingNameField.getText().toString();
                try {
                    studentId = Long.parseLong(studentIdField.getText().toString());
                } catch (Exception e) {
                    Log.e("FilterFragment", e.getMessage());
                    // TODO: handle
                }
                major = spinner.getSelectedItem().toString();

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