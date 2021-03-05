package com.team10.trojancheckinout;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    protected ArrayList<Record> records;

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
        // TODO: sort by time (listeners may not be fired in the order data is written)
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
        holder.time.setText(record.getTimeString());

        holder.studentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: open profile of student
            }
        });

        Server.getStudent(record.getStudentId(), new Callback<Student>() {
            @Override
            public void onSuccess(Student result) {
                // set student photo
                Glide.with(holder.itemView)
                     .load(result.getPhotoUrl())
                     .into(holder.studentPhoto);
                // set student name
                holder.studentName.setText(
                    String.format("%s, %s", result.getSurname(), result.getGivenName()));
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO
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
                // TODO
            }
        });
    }

    @Override
    public int getItemCount() { return records.size(); }
}
