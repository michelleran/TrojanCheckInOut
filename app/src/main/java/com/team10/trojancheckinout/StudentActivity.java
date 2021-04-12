package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "StudentActivity";
    private static final int PICK_PHOTO_REQUEST = 1000;

    TextView givenName, surname, id, major, currentBuilding;
    ImageView photo;
    Button scanBtn;
    Student student;
    private IntentIntegrator qrScan;
    private String newPass;
    private String imageLink;

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
        photo = findViewById(R.id.student_photo);

        //assume current user is student, gets student data
        Server.getCurrentUser(new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                student = (Student) result;

                //gets building name through Server.getBuilding()
                if (student.getCurrentBuilding() != null) {
                    Server.getBuilding(student.getCurrentBuilding(), new Callback<Building>() {
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

                //set student data into TextView
                givenName.setText(student.getGivenName());
                surname.setText(student.getSurname());
                id.setText(student.getId());
                major.setText(student.getMajor());

                Glide.with(getApplicationContext()).load(student.getPhotoUrl())
                    .placeholder(R.drawable.default_profile_picture)
                    .override(400, 400).centerCrop()
                    .into(photo);
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO: handle
            }
        });

        scanBtn = (Button) findViewById(R.id.scanQRCode);

        //initializing scan object
        qrScan = new IntentIntegrator(this);

        //attach onclick listener
        scanBtn.setOnClickListener(this);
    }

    public void viewHistory(View view){
        Intent i = new Intent(StudentActivity.this, StudentHistory.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", student.getUid());
        i.putExtras(bundle);
        startActivity(i);
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
        input.setId(R.id.edtNewPassword);
        //Set input type to password
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        //Set up buttons
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            newPass = input.getText().toString();
            if(newPass.isEmpty())
                Toast.makeText(StudentActivity.this, "Please type a password", Toast.LENGTH_LONG).show();
            else {
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
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    //upload and change profile image from gallery on click
    public void editImage(View view){
        String[] options = {"Choose from Gallery", "Use Web Link"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload New Profile Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("Choose from Gallery".equals(options[which])) {
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, PICK_PHOTO_REQUEST);
                } else if ("Use Web Link".equals(options[which])) {
                    //new alert dialog to accept user text input
                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
                    builder.setTitle("Insert Link for New Profile Photo");
                    final EditText input = new EditText(StudentActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imageLink = input.getText().toString();
                            Server.photoURLInput(imageLink, new Callback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    Toast.makeText(StudentActivity.this, "Updated Profile Picture", Toast.LENGTH_LONG).show();
                                    // replace photo in UI
                                    Glide.with(getApplicationContext()).load(result)
                                            .placeholder(R.drawable.default_profile_picture)
                                            .override(400, 400).centerCrop()
                                            .into(photo);
                                }
                                @Override
                                public void onFailure(Exception exception) {
                                    Log.e(TAG, "onFailure: upload prof pic failure");
                                }
                            });
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });
        builder.show();
    }

    public void signOut(View view){
        Server.logout();
        startActivity(new Intent(StudentActivity.this, LoginActivity.class));
        finish();
    }

    //manually check out of building
    public void checkOut(View view){
        //show Toast if user not checked into a building
        if(student.getCurrentBuilding() == null) {
            Toast.makeText(getApplicationContext(), "Not currently checked into a building", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //set Current Building to None and Server.checkOut()
        currentBuilding.setText(R.string.none);
        Server.checkOut(new Callback<Building>() {
            @Override
            public void onSuccess(Building building) {
                student.setBuilding(null);
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
                Log.d(TAG, imageUri.toString());
                Server.changePhoto(imageUri, new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(StudentActivity.this, "Updated Profile Picture", Toast.LENGTH_LONG).show();
                        // replace photo
                        Glide.with(getApplicationContext()).load(result)
                            .placeholder(R.drawable.default_profile_picture)
                            .override(400, 400).centerCrop()
                            .into(photo);
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
                didScanQR(result.getContents());
            } else {
                Toast.makeText(this, "Not a valid building QR code", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        qrScan.initiateScan();
    }

    @VisibleForTesting
    public void didScanQR(String buildingId) {
        Log.d("StudentActivity", buildingId);
        if (student.getCurrentBuilding() != null && student.getCurrentBuilding().equals(buildingId)) {
            Server.checkOut(new Callback<Building>() {
                @Override
                public void onSuccess(Building building) {
                    student.setBuilding(null);
                    currentBuilding.setText(R.string.none);
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
                    student.setBuilding(buildingId);
                    currentBuilding.setText(building.getName());
                    Toast.makeText(getApplicationContext(),"Successfully checked in!", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Exception exception) {
                    Log.e(TAG, "onFailure: checkIn failure");
                    exception.printStackTrace();
                }
            });
        }
    }
}