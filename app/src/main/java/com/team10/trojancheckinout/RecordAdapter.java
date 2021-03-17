package com.team10.trojancheckinout;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    protected ArrayList<Record> records;

    private final String TAG = "RecordAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView studentPhoto;
        public final TextView studentName;
        public final TextView recordType;
        public final TextView buildingName;
        public final TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // get refs to views
            studentPhoto = itemView.findViewById(R.id.record_student_photo);
            studentName = itemView.findViewById(R.id.record_student_name);
            recordType = itemView.findViewById(R.id.record_type);
            buildingName = itemView.findViewById(R.id.record_building_name);
            time = itemView.findViewById(R.id.record_time);
        }
    }

    public RecordAdapter() {
        records = new ArrayList<>();
    }

    public void addRecord(Record record) {
        records.add(record);
        // sort by time (listeners may not be fired in the order data is written)
        Collections.sort(records);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.recordType.setText(record.getCheckIn() ? R.string.checked_into : R.string.checked_out_of);
        holder.time.setText(record.getTime());

        holder.studentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open profile of student
                Intent intent = new Intent(holder.itemView.getContext(), StudentBasicActivity.class);
                intent.putExtra("studentId", record.getStudentUid());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        Server.getStudent(record.getStudentUid(), new Callback<Student>() {
            @Override
            public void onSuccess(Student result) {
                // set student photo
                Glide.with(holder.itemView)
                     .load(result.getPhotoUrl())
                     .override(400, 400).centerCrop()
                     .into(holder.studentPhoto);
                // set student name
                holder.studentName.setText(
                    String.format("%s, %s", result.getSurname(), result.getGivenName()));
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, exception.getMessage());
                // TODO: handle
            }
        });

        Server.getBuilding(record.getBuildingId(), new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                // set building name
                holder.buildingName.setText(result.getName());
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, exception.getMessage());
                // TODO: handle
            }
        });
    }

    @Override
    public int getItemCount() { return records.size(); }
}
