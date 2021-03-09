package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;

public class StudentActivity extends AppCompatActivity {
    private static final String TAG = "StudentActivity";
    TextView givenName, surname, id, major, currentBuilding;
    ImageView photoUrl;
    String fName, lName, usc_id, photo_url, major_, currBuilding;
    Student student;

    //load user data into profile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        givenName = findViewById(R.id.givenName);
        surname = findViewById(R.id.surname);
        id = findViewById(R.id.id);
        major = findViewById(R.id.major);
        currentBuilding = findViewById(R.id.currentBuilding);
        photoUrl = findViewById(R.id.student_photo);

        //assume current user is student, gets student data
        student = (Student) Server.getCurrentUser();
        fName = student.getGivenName();
        lName = student.getSurname();
        usc_id = student.getIdString();
        major_ = student.getMajor();
        currBuilding = student.getCurrentBuilding();
        photo_url = student.getPhotoUrl();

        //set student data into TextView
        givenName.setText(fName);
        surname.setText(lName);
        id.setText(usc_id);
        major.setText(major_);
        if (currBuilding != null)
            currentBuilding.setText(currBuilding);
        else
            currentBuilding.setText(R.string.none);
        Glide.with(getApplicationContext()).load(photo_url).into(photoUrl);
    }

    //upload and change profile image from gallery on click
    public void editImage(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                photoUrl.setImageURI(imageUri);
                // TODO: upload image to Firebase through Server
            }
        }
    }
}