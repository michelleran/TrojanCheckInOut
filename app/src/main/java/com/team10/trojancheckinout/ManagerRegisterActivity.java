package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.User;
import com.team10.trojancheckinout.utils.Validator;

import static com.team10.trojancheckinout.model.Server.managerRegister;
import static com.team10.trojancheckinout.utils.Validator.validateEmail;
import static com.team10.trojancheckinout.utils.Validator.validateNotEmpty;
import static com.team10.trojancheckinout.utils.Validator.validatePassword;

public class ManagerRegisterActivity extends AppCompatActivity {


    private EditText mfname;
    private EditText mlname;
    private EditText mEmail;
    private EditText mPassword;

    private Button mBack;
    private Button mRegister;
    private Button mPhoto;

    private boolean isValid = false;

    private Uri imageUri;
    private boolean gotImage = false;


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
                isValid = validateNotEmpty(allEntries, allEntries.length) && validateEmail(iEmail) && validatePassword(iPassword) && gotImage;
                if(!validateNotEmpty(allEntries, allEntries.length)){
                    Toast.makeText(getApplicationContext(), "Please don't leave any field blank!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!validateEmail(iEmail)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid usc email!" ,Toast.LENGTH_SHORT).show();
                }
                else if(!validatePassword(iPassword)){
                    Toast.makeText(getApplicationContext(), "Please enter a password at least 8 characters long!" ,Toast.LENGTH_SHORT).show();
                }
                else if (!gotImage){
                    Toast.makeText(getApplicationContext(), "Please add a photo!" ,Toast.LENGTH_SHORT).show();
                }

                if(isValid){

                    managerRegister("", iFname, iLname, iEmail, imageUri, iPassword, new Callback<User>() {
                        @Override
                        public void onSuccess(User result) {
                            Toast.makeText(ManagerRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            Intent it = new Intent(ManagerRegisterActivity.this, ManagerActivity.class);
                            startActivity(it);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Toast.makeText(ManagerRegisterActivity.this, "Registration failed: " + exception, Toast.LENGTH_SHORT).show();
                            Log.d("Frontend Manager Register", "Failed to add manager to server");
                        }
                    });

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