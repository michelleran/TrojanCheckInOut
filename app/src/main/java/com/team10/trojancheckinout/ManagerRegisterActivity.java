package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.utils.Validator;

public class ManagerRegisterActivity extends AppCompatActivity {


    private EditText mfname;
    private EditText mlname;
    private EditText mEmail;
    private EditText mPassword;

    private Button mBack;
    private Button mRegister;
    private Button mPhoto;

    private Validator val;
    private boolean isValid = false;

    private Uri imageUri;
    private boolean gotImage = false;
    private Server server;


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

                String iFname = mfname.getText().toString().trim();
                String iLname = mlname.getText().toString().trim();
                String iEmail = mEmail.getText().toString().trim();
                String iPassword = mPassword.getText().toString();

                String [] allEntries = new String[] {iFname, iLname, iEmail, iPassword};
                isValid = Validator.validateNotEmpty(allEntries, allEntries.length) && Validator.validateEmail(iEmail) && Validator.validatePassword(iPassword);
                if(!Validator.validateNotEmpty(allEntries, allEntries.length)){
                    Toast.makeText(getApplicationContext(), "Please don't leave any field blank!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!Validator.validateEmail(iEmail)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid usc email!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!Validator.validatePassword(iPassword)){
                    Toast.makeText(getApplicationContext(), "Please enter a password at least 8 characters long!" ,Toast.LENGTH_SHORT).show();
                }

                if(isValid){

                    //server.registerManager(iFname, iLname, iEmail, iPassword, Callback<manager> callback);
                    //if(gotImage){server.changePhoto(imageUri, Callback<Manager> callback);}

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
                choosePicture();
            }
        });

    }

    /*private boolean validate(String fname, String lname, String email, String password){
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
    }*/

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