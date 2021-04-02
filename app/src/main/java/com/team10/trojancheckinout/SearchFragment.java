package com.team10.trojancheckinout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.utils.Validator;

import java.util.Calendar;
import java.util.Locale;


public class SearchFragment extends Fragment {
    private EditText start;
    private EditText end;

    private final String DATE_TIME_FORMAT = "%02d/%02d/%04d %02d:%02d PDT";

    private String studentName;
    private String major;

    private String buildingName;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_search, container, false);

        // get fields
        start = rootView.findViewById(R.id.search_start_date);
        start.setOnClickListener(view -> {
            // show date picker dialog
            final Calendar cal = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(rootView.getContext(), (datePicker, year, month, day) -> {
                // save values
                startYear = year;
                startMonth = month + 1; // month is 0-indexed
                startDay = day;

                // pick time
                TimePickerDialog timePicker = new TimePickerDialog(rootView.getContext(), (tp, hour, min) -> {
                    // save values
                    startHour = hour;
                    startMin = min;
                    // update field
                    start.setText(String.format(Locale.US, DATE_TIME_FORMAT, startMonth, startDay, startYear, startHour, startMin));
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
                timePicker.show();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            picker.show();
        });

        end = rootView.findViewById(R.id.search_end_date);
        end.setOnClickListener(view -> {
            // show date picker dialog
            final Calendar cal = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(rootView.getContext(), (datePicker, year, month, day) -> {
                // save values
                endYear = year;
                endMonth = month + 1; // month is 0-indexed
                endDay = day;
                // pick time
                TimePickerDialog timePicker = new TimePickerDialog(rootView.getContext(), (tp, hour, min) -> {
                    // save values
                    endHour = hour;
                    endMin = min;
                    // update field
                    end.setText(String.format(Locale.US, DATE_TIME_FORMAT, endMonth, endDay, endYear, endHour, endMin));
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
                timePicker.show();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_WEEK));
            picker.show();
        });

        EditText nameField = rootView.findViewById(R.id.search_name);

        Spinner majors = rootView.findViewById(R.id.search_major_spinner);
        majors.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.majors_with_any, android.R.layout.simple_spinner_item));

        // get list of buildings
        Spinner buildings = rootView.findViewById(R.id.search_building_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.add("Any");
        buildings.setAdapter(adapter);
        Server.getAllBuildingNames(new Callback<String>() {
            @Override
            public void onSuccess(String building) {
                adapter.add(building);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
            }
        });

        Button button = rootView.findViewById(R.id.search_button);
        button.setOnClickListener(view -> {
            // validate that start <= end date
            if (startYear != -1 && endYear != -1 &&
                (startYear > endYear ||
                    (startYear == endYear && startMonth > endMonth) ||
                    (startYear == endYear && startMonth == endMonth && startDay > endDay) ||
                    (startYear == endYear && startMonth == endMonth && startDay == endDay && startHour > endHour) ||
                    (startYear == endYear && startMonth == endMonth && startDay == endDay && startHour == endHour && startMin > endMin)))
            {
                Toast.makeText(getContext(), R.string.filter_invalid_dates, Toast.LENGTH_LONG).show();
                return;
            }

            // open search results (replace this fragment)
            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.search_tab_content,
                SearchResultsFragment.newInstance(
                    nameField.getText().toString().trim(),
                    majors.getSelectedItemPosition() == 0 ? "" : majors.getSelectedItem().toString(),
                    buildings.getSelectedItemPosition() == 0 ? "" : buildings.getSelectedItem().toString(),
                    startYear, startMonth, startDay, startHour, startMin,
                    endYear, endMonth, endDay, endHour, endMin));
            ft.commit();
            ft.addToBackStack("search");
        });

        return rootView;
    }
}