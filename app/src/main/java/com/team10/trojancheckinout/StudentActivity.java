package com.team10.trojancheckinout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team10.trojancheckinout.model.Student;

public class StudentActivity extends AppCompatActivity {
    TextView givenName, surname, id, major, currentBuilding;
    ImageView photoUrl;
    String fName, lName, usc_id, photo_url, major_, currBuilding;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

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

        //get data corresponding to logged in user
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                fName = task.getResult().getString("givenName");
                lName = task.getResult().getString("surname");
                usc_id = task.getResult().getString("id");
                major_ = task.getResult().getString("major");
                currBuilding = task.getResult().getString("currentBuilding");
                photo_url = task.getResult().getString("photoUrl");

                givenName.setText(fName);
                surname.setText(lName);
                id.setText(usc_id);
                major.setText(major_);
                currentBuilding.setText(currBuilding);
                if (photo_url != null) {  //require profile photo on registration?
                    Glide.with(getApplicationContext()).load(photo_url).into(photoUrl);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Student data load error", Toast.LENGTH_LONG).show();
            }
        });
    }
}