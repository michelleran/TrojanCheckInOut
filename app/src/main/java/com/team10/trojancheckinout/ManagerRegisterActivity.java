package com.team10.trojancheckinout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManagerRegisterActivity extends AppCompatActivity {


    private EditText mfname;
    private EditText mlname;
    private EditText mEmail;
    private EditText mPassword;

    private Button mBack;
    private Button mRegister;
    private Button mPhoto;

    private boolean isValid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_register);

        mfname = findViewById(R.id.etMFname);
        mlname = findViewById(R.id.etMLname);
        mEmail = findViewById(R.id.etMEmail);
        mPassword = findViewById(R.id.etMPassword);

        mBack = findViewById(R.id.emBackButton);
        mRegister = findViewById(R.id.mRegBtn);
        mPhoto = findViewById(R.id.mAddPhoto);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String iFname = mfname.getText().toString();
                String iLname = mlname.getText().toString();
                String iEmail = mEmail.getText().toString();
                String iPassword = mPassword.getText().toString();

                isValid = validate(iFname,iLname,iEmail,iPassword);

                if(isValid){
                    Toast.makeText(ManagerRegisterActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(ManagerRegisterActivity.this, ManagerActivity.class);
                    startActivity(it);
                }

            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ManagerRegisterActivity.this, StartPage.class);
                startActivity(it);
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private boolean validate(String fname, String lname, String email, String password){
        if(fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please don't leave any field blank!" ,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!email.contains("usc.edu")){
            Toast.makeText(getApplicationContext(), "Please enter a usc email!" ,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.length() < 8){
            Toast.makeText(getApplicationContext(), "Please enter a password at least 8 characters long!" ,Toast.LENGTH_SHORT).show();
            return false;
        }

        return false;
    }

}