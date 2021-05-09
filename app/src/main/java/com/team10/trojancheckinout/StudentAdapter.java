package com.team10.trojancheckinout;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Listener;
import com.team10.trojancheckinout.model.Student;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentAdapter
    extends RecyclerView.Adapter<StudentAdapter.ViewHolder>
    implements Listener<Student>, Callback<Student>
{
    protected ArrayList<Student> students;

    private View empty;

    private final String TAG = "StudentAdapter";
    private boolean sortBySurname = false;

    @Override
    public void onSuccess(Student item) {
        if (students.isEmpty() && empty != null)
            empty.setVisibility(View.GONE);
        students.add(item);
        // sort alphabetically by surname
        students.sort((Student s1, Student s2) -> s1.getSurname().compareTo(s2.getSurname()));
        notifyDataSetChanged();
    }

    @Override
    public void onAdd(Student item) {
        if (students.isEmpty() && empty != null)
            empty.setVisibility(View.GONE);
        students.add(item);
        notifyDataSetChanged();
    }

    @Override
    public void onRemove(Student item) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getUid().equals(item.getUid())) {
                students.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
        if (students.isEmpty() && empty != null)
            empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdate(Student item) { }

    @Override
    public void onFailure(Exception exception) {
        exception.printStackTrace();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView studentPhoto;
        public final TextView studentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // get refs to views
            studentPhoto = itemView.findViewById(R.id.record_student_photo);
            studentName = itemView.findViewById(R.id.record_student_name);
        }
    }

    public StudentAdapter() {
        students = new ArrayList<>();
    }

    public StudentAdapter(boolean sortBySurname, View empty) {
        students = new ArrayList<>();
        this.sortBySurname = sortBySurname;
        this.empty = empty;
    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_student, parent, false);
        return new StudentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {
        Student student = students.get(position);
        // set student photo
        Glide.with(holder.itemView)
            .load(student.getPhotoUrl())
            .override(400, 400).centerCrop()
            .into(holder.studentPhoto);
        // set student name
        holder.studentName.setText(
            String.format("%s, %s", student.getSurname(), student.getGivenName()));

        holder.studentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open profile of student
                Intent intent = new Intent(holder.itemView.getContext(), StudentBasicActivity.class);
                intent.putExtra("studentId", student.getUid());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return students.size(); }
}
