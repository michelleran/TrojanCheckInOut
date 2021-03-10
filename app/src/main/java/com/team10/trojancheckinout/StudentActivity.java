package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;
import com.team10.trojancheckinout.utils.QRCodeHelper;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "StudentActivity";
    TextView givenName_tv, surname_tv, id_tv, major_tv, currentBuilding_tv;
    ImageView photoUrl;
    Button scanQRCode_btn;
    String fName, lName, usc_id, photo_url, major_, currBuilding;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Student student;
    private IntentIntegrator qrScan;
    private String newPass;

    //load user data into profile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        givenName_tv = findViewById(R.id.givenName);
        surname_tv = findViewById(R.id.surname);
        id_tv = findViewById(R.id.id);
        major_tv = findViewById(R.id.major);
        currentBuilding_tv = findViewById(R.id.currentBuilding);
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
        givenName_tv.setText(fName);
        surname_tv.setText(lName);
        id_tv.setText(usc_id);
        major_tv.setText(major_);
        if (currBuilding != null) currentBuilding_tv.setText(currBuilding);
        else currentBuilding_tv.setText(R.string.none);
        Glide.with(getApplicationContext()).load(photo_url).into(photoUrl);

        scanQRCode_btn = (Button) findViewById(R.id.scanQRCode);

        //initializing scan object
        qrScan = new IntentIntegrator(this);

        //attach onclick listener
        scanQRCode_btn.setOnClickListener(this);
    }

    public void deleteAccount(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    //TODO: insert Server.deleteAccount method
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.cancel();
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public void changePassword(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_password);
        //Set up input
        final EditText input = new EditText(this);
        //Set input type to password
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        //Set up buttons
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            newPass = input.getText().toString();
            //TODO: call Server.newPassword() function
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    //TODO: replace editImage() and uploadImagetoFirebase() with function calls from Register
    //upload and change profile image from gallery on click
    public void editImage(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    //upload image URI retrieved from Image Gallery to Firebase - update Student's photo URL field
    private void uploadImagetoFirebase(Uri imageUri){
        //upload profile picture to firebase
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setPhotoUri(imageUri).build();
        user.updateProfile(profileUpdate).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d(TAG, "User Profile image updated");
            }
        });
    }

    public void signOut(View view){
        //TODO: log out using Firebase auth with function from Server
        startActivity(new Intent(StudentActivity.this, LoginActivity.class));
        finish();
    }

    //manually check out of building
    public void checkOut(View view){
        currBuilding = "None";
        currentBuilding_tv.setText(R.string.none);
        //TODO: update current building in firebase using function from Server
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                photoUrl.setImageURI(imageUri);
                //uploadImagetoFirebase(imageUri);
            }
        }
        //QR SCAN - set UI
        String buildingID = QRCodeHelper.process(requestCode, resultCode, data, this);
        Server.getBuilding(buildingID, new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                //if user already checked into this building, check out
                if(currBuilding.equals(result.getName())){
                    currBuilding = "None";
                    currentBuilding_tv.setText(R.string.none);
                    //TODO: update current building in firebase using function from Server
                }
                //else check in
                else{
                    currBuilding = result.getName();
                    currentBuilding_tv.setText(currBuilding);
                    //TODO: update current building in firebase using function from Server
                }
            }
            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "onFailure: QR code getBuilding error");
            }
        });
    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }
}