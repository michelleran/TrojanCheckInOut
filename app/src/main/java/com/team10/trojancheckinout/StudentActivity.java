package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
        //gets building name through Server.getBuilding()
        currBuilding = getBuildingName(student.getCurrentBuilding());
        photo_url = student.getPhotoUrl();

        //set student data into TextView
        givenName_tv.setText(fName);
        surname_tv.setText(lName);
        id_tv.setText(usc_id);
        major_tv.setText(major_);
        if (student.getCurrentBuilding() != null) currentBuilding_tv.setText(currBuilding);
        else currentBuilding_tv.setText(R.string.none);
        Glide.with(getApplicationContext()).load(photo_url)
                .placeholder(R.drawable.default_profile_picture)
                .into(photoUrl);

        scanQRCode_btn = (Button) findViewById(R.id.scanQRCode);

        //initializing scan object
        qrScan = new IntentIntegrator(this);

        //attach onclick listener
        scanQRCode_btn.setOnClickListener(this);
    }

    public String getBuildingName(String id){
        Server.getBuilding(id, new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                currBuilding = result.getName();
            }
            @Override
            public void onFailure(Exception exception) {
                currBuilding = null;
                Log.e(TAG, "onFailure: getBuildingName failure");
            }
        });
        return currBuilding;
    }

    public void deleteAccount(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message)
                .setTitle(R.string.delete_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    Server.deleteAccount(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onFailure(Exception exception) {
                            Log.e(TAG, "onFailure: deleteAccount failure");
                        }
                    });
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
            Server.changePassword(newPass, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(StudentActivity.this, "Password changed!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(Exception exception) {
                    Log.e(TAG, "onFailure: changePassword failure");
                }
            });
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
        // TODO
    }

    public void signOut(View view){
        Server.logout(new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(StudentActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "onFailure: logout failure");
            }
        });
        startActivity(new Intent(StudentActivity.this, LoginActivity.class));
        finish();
    }

    //manually check out of building
    public void checkOut(View view){
        //show Toast if user not checked into a building
        if(currBuilding == null || currBuilding.equals("None")){
            Toast.makeText(getApplicationContext(), "Not currently checked into a building", Toast.LENGTH_LONG)
                    .show();
        }
        //set Current Building to None and Server.checkOut()
        currBuilding = "None";
        currentBuilding_tv.setText(R.string.none);
        Server.checkOut(student.getCurrentBuilding(), new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(), "Successfully checked out!", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "onFailure: manual checkout failure");
            }
        });
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
                if(currBuilding != null && currBuilding.equals(result.getName())){
                    Server.checkOut(buildingID, new Callback<Void>() {
                        @Override
                        public void onSuccess(Void res) {
                            currBuilding = "None";
                            currentBuilding_tv.setText(R.string.none);
                            Toast.makeText(getApplicationContext(),"Successfully checked out!", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onFailure(Exception exception) {
                            Log.e(TAG, "onFailure: checkOut failure");
                        }
                    });
                }
                //else check in
                else{
                    Server.checkIn(buildingID, new Callback<Void>() {
                        @Override
                        public void onSuccess(Void res) {
                            currBuilding = result.getName();
                            currentBuilding_tv.setText(currBuilding);
                            Toast.makeText(getApplicationContext(),"Successfully checked in!", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onFailure(Exception exception) {
                            Log.e(TAG, "onFailure: checkIn failure");
                        }
                    });
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