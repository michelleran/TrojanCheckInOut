package com.team10.trojancheckinout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;
import com.team10.trojancheckinout.model.User;

public class StudentHistory extends AppCompatActivity {
    private static final String TAG = "Student History";
    private RecordAdapter adapter;
    String usc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_history);

        Bundle bundle = getIntent().getExtras();
        usc_id = bundle.getString("usc_id");

        RecyclerView resultsList = findViewById(R.id.student_history_list);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        resultsList.setLayoutManager(llm);

        adapter = new RecordAdapter();
        resultsList.setAdapter(adapter);

        Server.filterRecords(-1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1,
                "", usc_id, "", new Callback<Record>() {
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
    }
}