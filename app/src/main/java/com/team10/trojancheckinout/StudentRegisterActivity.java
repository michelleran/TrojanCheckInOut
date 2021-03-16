package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.User;

import static com.team10.trojancheckinout.model.Server.registerStudent;
import static com.team10.trojancheckinout.utils.Validator.validateEmail;
import static com.team10.trojancheckinout.utils.Validator.validateID;
import static com.team10.trojancheckinout.utils.Validator.validateNotEmpty;
import static com.team10.trojancheckinout.utils.Validator.validatePassword;

public class StudentRegisterActivity extends AppCompatActivity {
    Spinner spin;

    private EditText fname;
    private EditText lname;
    private EditText sEmail;
    private EditText sPassword;
    private EditText sID;

    private Button sBack;
    private Button sRegister;
    private Button sPhoto;

    private boolean isValid = false;

    private Uri imageUri;
    private boolean gotImage = false;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        spin = findViewById(R.id.sMajors);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.majors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin.setAdapter(adapter);

        fname = findViewById(R.id.etSFname);
        lname = findViewById(R.id.etSLname);
        sEmail = findViewById(R.id.etSEmail);
        sPassword = findViewById(R.id.etSPassword);
        sID = findViewById(R.id.etUSCid);

        sBack = findViewById(R.id.esBackButton);
        sRegister = findViewById(R.id.sRegBtn);
        sPhoto = findViewById(R.id.sAddPhoto);

        sRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iFname = fname.getText().toString().trim();
                String iLname = lname.getText().toString().trim();
                String iEmail = sEmail.getText().toString().trim();
                String iPassword = sPassword.getText().toString();
                String iID = sID.getText().toString().trim();
                String iMajor = spin.getSelectedItem().toString();

                if(!validateNotEmpty(iFname, iLname, iEmail, iPassword, iID)) {
                    Toast.makeText(getApplicationContext(), "Please don't leave any field blank!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!validateEmail(iEmail)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid usc email!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!validatePassword(iPassword)){
                    Toast.makeText(getApplicationContext(), "Please enter a password at least 8 characters long!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!validateID(iID)){
                    Toast.makeText(getApplicationContext(), "Please enter your TEN digit USC ID!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!gotImage){
                    Toast.makeText(getApplicationContext(), "Please add a photo!" ,Toast.LENGTH_SHORT).show();
                } else {
                    // all inputs are valid
                    registerStudent(iID, iFname, iLname, iEmail, imageUri, iMajor, iPassword, new Callback<User>() {
                        @Override
                        public void onSuccess(User result) {
                            Toast.makeText(StudentRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StudentRegisterActivity.this, StudentActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Toast.makeText(StudentRegisterActivity.this, "Registration failed: " + exception, Toast.LENGTH_SHORT).show();
                            Log.d("Frontend Student Register", "Failed to add student to server");
                        }
                    });
                }
            }
        });

        sBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(StudentRegisterActivity.this, StartPage.class);
                startActivity(it);
            }
        });

        sPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
    }

    private void choosePicture(){
        Intent itt = new Intent();
        itt.setType("image/*");
        itt.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(itt, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            gotImage = true;
            imageUri = data.getData();
        }
    }

}