package com.team10.trojancheckinout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;

import javax.annotation.Nullable;

public class StudentHistoryFragment extends Fragment {
    private static final String TAG = "Student History Fragment";

    private static final String ARG_STUDENT_ID = "studentId";

    private long studentId;

    private RecordAdapter adapter;

    public StudentHistoryFragment() {
        // Required empty public constructor
    }

    public static StudentHistoryFragment newInstance(long studentId) {
        StudentHistoryFragment fragment = new StudentHistoryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID);
        }
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(
                R.layout.fragment_student_history, container, false);

        RecyclerView resultsList = rootView.findViewById(R.id.student_history_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        resultsList.setLayoutManager(llm);

        adapter = new RecordAdapter();
        resultsList.setAdapter(adapter);

        Log.d(TAG, String.valueOf(studentId));

        Server.filterRecords(-1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1,
                "", studentId, "", new Callback<Record>() {
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