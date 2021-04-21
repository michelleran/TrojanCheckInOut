package com.team10.trojancheckinout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;

public class StudentBasicActivity extends AppCompatActivity {
    private static final String TAG = "StudentBasicActivity";
    TextView givenName, surname, id, major, currentBuilding, deleted;
    Student student;
    ImageView photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_basic);

        Intent i = getIntent();
        String studentId = i.getExtras().getString("studentId");

        givenName = findViewById(R.id.givenName);
        surname = findViewById(R.id.surname);
        id = findViewById(R.id.id);
        major = findViewById(R.id.major);
        currentBuilding = findViewById(R.id.currentBuilding);
        photoUrl = findViewById(R.id.student_photo);
        deleted = findViewById(R.id.deletedAccount);

        Server.getStudent(studentId, new Callback<Student>() {
            @Override
            public void onSuccess(Student result) {
                student = (Student) result;
                givenName.setText(result.getGivenName());
                surname.setText(result.getSurname());
                id.setText(result.getId());
                major.setText(result.getMajor());

                Glide.with(getApplicationContext())
                        .load(result.getPhotoUrl())
                        .override(400, 400).centerCrop()
                        .into(photoUrl);

                if(result.isDeleted()){
                    deleted.setVisibility(TextView.VISIBLE);
                    currentBuilding.setText("N/A");
                }
                else{
                    //gets building name through Server.getBuilding()
                    if (result.getCurrentBuilding() != null) {
                        Server.getBuilding(result.getCurrentBuilding(), new Callback<Building>() {
                            @Override
                            public void onSuccess(Building result) {
                                currentBuilding.setText(result.getName());
                            }
                            @Override
                            public void onFailure(Exception exception) {
                                Log.e(TAG, "onFailure: getBuildingName failure");
                            }
                        });
                    } else {
                        currentBuilding.setText(R.string.none);
                    }
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, exception.getMessage() );
            }
        });
    }

    public void viewHistory(View view){
        Intent i = new Intent(StudentBasicActivity.this, StudentHistory.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", student.getUid());
        i.putExtras(bundle);
        startActivity(i);
    }

    public void forceKickout(View view){
        if(student.getCurrentBuilding() == null){
            Toast.makeText(getApplicationContext(), "Student is not currently checked in!", Toast.LENGTH_LONG).show();
        }
        else {
            Server.checkOutStudent(student.getUid(), new Callback<Building>() {
                @Override
                public void onSuccess(Building result) {
                    student.setBuilding(null);
                    currentBuilding.setText(R.string.none);
                    Toast.makeText(getApplicationContext(), "Force kick out successful!", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Exception exception) {
                    Log.e(TAG, "onFailure: force kickout failure");
                }
            });
        }
    }
}