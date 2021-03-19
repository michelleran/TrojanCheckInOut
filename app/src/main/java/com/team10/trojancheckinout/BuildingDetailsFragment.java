package com.team10.trojancheckinout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Listener;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Use the {@link BuildingDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildingDetailsFragment extends Fragment {
    private static final String ARG_BUILDING_ID = "buildingId";
    private static final String ARG_BUILDING_NAME = "buildingName";
    private static final String ARG_MAX_CAPACITY = "maxCapacity";

    private String buildingId;
    private String buildingName;
    private int maxCapacity;

    private CheckedInStudentAdapter adapter;
    private final String capacityFormat = "Capacity: %d/%d";

    private final String TAG = "BuildingDetailsFragment";

    public BuildingDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static BuildingDetailsFragment newInstance(Building building) {
        BuildingDetailsFragment fragment = new BuildingDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUILDING_ID, building.getId());
        args.putString(ARG_BUILDING_NAME, building.getName());
        args.putInt(ARG_MAX_CAPACITY, building.getMaxCapacity());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getString(ARG_BUILDING_ID);
            buildingName = getArguments().getString(ARG_BUILDING_NAME);
            maxCapacity = getArguments().getInt(ARG_MAX_CAPACITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_building_details, container, false);

        // set building name
        TextView buildingNameView = rootView.findViewById(R.id.building_details_name);
        buildingNameView.setText(buildingName);

        // set building current/max capacity
        TextView capacity = rootView.findViewById(R.id.building_details_capacity);

        // set up RecyclerView
        RecyclerView studentList = rootView.findViewById(R.id.building_details_students);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        studentList.setLayoutManager(llm);

        adapter = new CheckedInStudentAdapter();
        studentList.setAdapter(adapter);
        Server.listenForCheckedInStudents(buildingId, new Listener<Student>() {
            @Override
            public void onAdd(Student item) {
                adapter.onAdd(item);
                capacity.setText(String.format(Locale.US, capacityFormat, adapter.getItemCount(), maxCapacity));
            }

            @Override
            public void onRemove(Student item) {
                adapter.onRemove(item);
                capacity.setText(String.format(Locale.US, capacityFormat, adapter.getItemCount(), maxCapacity));
            }

            @Override
            public void onUpdate(Student item) {
                // nothing
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, exception.getMessage());
            }
        });

        return rootView;
    }
}

class CheckedInStudentAdapter
    extends RecyclerView.Adapter<CheckedInStudentAdapter.ViewHolder>
    implements Listener<Student>
{
    protected ArrayList<Student> students;

    private final String TAG = "CheckedInStudentAdapter";

    @Override
    public void onAdd(Student item) {
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
    }

    @Override
    public void onUpdate(Student item) { }
    @Override
    public void onFailure(Exception exception) { }

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

    public CheckedInStudentAdapter() {
        students = new ArrayList<>();
    }

    @NonNull
    @Override
    public CheckedInStudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.row_student, parent, false);
        return new CheckedInStudentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckedInStudentAdapter.ViewHolder holder, int position) {
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