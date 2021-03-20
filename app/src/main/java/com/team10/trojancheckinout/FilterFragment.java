package com.team10.trojancheckinout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.Toast;

import com.team10.trojancheckinout.utils.Validator;

import java.util.Calendar;
import java.util.Locale;

public class FilterFragment extends Fragment {
    private TextView start;
    private TextView end;

    private final String DATE_TIME_FORMAT = "%02d/%02d/%04d %02d:%02d PDT";

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_filter, container, false);

        // get fields
        start = rootView.findViewById(R.id.start_date);
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

        end = rootView.findViewById(R.id.end_date);
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

        EditText buildingNameField = rootView.findViewById(R.id.filter_building_field);
        EditText studentIdField = rootView.findViewById(R.id.filter_student_id_field);

        Spinner spinner = rootView.findViewById(R.id.filter_major_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.majors_with_any, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        Button filter = rootView.findViewById(R.id.filter_button);
        filter.setOnClickListener(view -> {
            // get remaining inputs (may be empty)
            buildingName = buildingNameField.getText().toString().trim();
            studentId = studentIdField.getText().toString().trim();
            if (!studentId.isEmpty() && !Validator.validateID(studentId)) {
                // invalid student id
                Toast.makeText(getContext(), R.string.filter_invalid_usc_id, Toast.LENGTH_LONG).show();
                return;
            }
            if (spinner.getSelectedItemPosition() == 0) {
                major = "";
            } else {
                major = spinner.getSelectedItem().toString();
            }

            // open filter results (replace this fragment)
            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.filter_tab_content,
                FilterResultsFragment.newInstance(
                    startYear, startMonth, startDay, startHour, startMin,
                    endYear, endMonth, endDay, endHour, endMin,
                    buildingName, studentId, major));
            ft.commit();
            ft.addToBackStack("filter");
        });

        return rootView;
    }
}