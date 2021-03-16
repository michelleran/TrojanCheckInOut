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
import com.google.zxing.integration.android.IntentResult;
import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;
import com.team10.trojancheckinout.model.User;
import com.team10.trojancheckinout.utils.QRCodeHelper;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "StudentActivity";
    private static final int PICK_PHOTO_REQUEST = 1000;

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
        Server.getCurrentUser(new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                student = (Student) result;
                fName = student.getGivenName();
                lName = student.getSurname();
                usc_id = student.getId();
                major_ = student.getMajor();

                //gets building name through Server.getBuilding()
                if (student.getCurrentBuilding() != null) {
                    Server.getBuilding(student.getCurrentBuilding(), new Callback<Building>() {
                        @Override
                        public void onSuccess(Building result) {
                            currBuilding = result.getName();
                            currentBuilding_tv.setText(currBuilding);
                        }
                        @Override
                        public void onFailure(Exception exception) {
                            currBuilding = null;
                            Log.e(TAG, "onFailure: getBuildingName failure");
                        }
                    });
                } else {
                    currentBuilding_tv.setText(R.string.none);
                }

                photo_url = student.getPhotoUrl();

                //set student data into TextView
                givenName_tv.setText(fName);
                surname_tv.setText(lName);
                id_tv.setText(usc_id);
                major_tv.setText(major_);

                Glide.with(getApplicationContext()).load(photo_url)
                    .placeholder(R.drawable.default_profile_picture)
                    .override(400, 400).centerCrop()
                    .into(photoUrl);
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO: handle
            }
        });

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
                    Server.deleteStudent(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();
                            // return to start page
                            Intent intent = new Intent(StudentActivity.this, StartPage.class);
                            startActivity(intent);
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
        startActivityForResult(openGalleryIntent, PICK_PHOTO_REQUEST);
    }

    public void signOut(View view){
        Server.logout();
        startActivity(new Intent(StudentActivity.this, LoginActivity.class));
        finish();
    }

    //manually check out of building
    public void checkOut(View view){
        //show Toast if user not checked into a building
        if(currBuilding == null || currBuilding.equals("None")){
            Toast.makeText(getApplicationContext(), "Not currently checked into a building", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //set Current Building to None and Server.checkOut()
        currBuilding = "None";
        currentBuilding_tv.setText(R.string.none);
        Server.checkOut(new Callback<Building>() {
            @Override
            public void onSuccess(Building building) {
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
        if(requestCode == PICK_PHOTO_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                Server.changePhoto(imageUri, new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(StudentActivity.this, "Updated Profile Picture", Toast.LENGTH_LONG).show();
                        // replace photo
                        photo_url = result;
                        Glide.with(getApplicationContext()).load(photo_url)
                            .placeholder(R.drawable.default_profile_picture)
                            .override(400, 400).centerCrop()
                            .into(photoUrl);
                    }
                    @Override
                    public void onFailure(Exception exception) {
                        Log.e(TAG, "onFailure: upload prof pic failure");
                    }
                });
            }
        } else {
            //QR SCAN - set UI
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                String buildingId = result.getContents();
                // TODO: below doesn't work if called right after checking in b/c local student obj is not updated
                if (student.getCurrentBuilding() != null && student.getCurrentBuilding().equals(buildingId)) {
                    Server.checkOut(new Callback<Building>() {
                        @Override
                        public void onSuccess(Building building) {
                            currBuilding = "None";
                            currentBuilding_tv.setText(R.string.none);
                            Toast.makeText(getApplicationContext(),"Successfully checked out!", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onFailure(Exception exception) {
                            Log.e(TAG, "onFailure: checkOut failure");
                        }
                    });
                } else {
                    Server.checkIn(buildingId, new Callback<Building>() {
                        @Override
                        public void onSuccess(Building building) {
                            currBuilding = building.getName();
                            currentBuilding_tv.setText(currBuilding);
                            Toast.makeText(getApplicationContext(),"Successfully checked in!", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onFailure(Exception exception) {
                            Log.e(TAG, "onFailure: checkIn failure");
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Not a valid building QR code", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }
}