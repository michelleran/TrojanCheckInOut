package com.team10.trojancheckinout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;
    private Button eBack;
    private TextView eAttemptsInfo;

    private boolean isValid = false;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);
        eAttemptsInfo = findViewById(R.id.etAttemptsInfo);
        eBack = findViewById(R.id.emBackButton);

        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputName = eEmail.getText().toString();
                String inputPassword = ePassword.getText().toString();

                if(inputName.isEmpty() || inputPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please do not leave any fields empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    isValid = validate(inputName, inputPassword);
                    if(!isValid){
                        counter--;
                        Toast.makeText(LoginActivity.this, "Incorrect credentials entered!", Toast.LENGTH_SHORT).show();

                        eAttemptsInfo.setText("No. of attempts remaining: " + counter);

                        if(counter == 0){
                            eLogin.setEnabled(false);
                        }
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        //add the code to go to the new activity
                        Intent intent = new Intent(LoginActivity.this, ManagerActivity.class);
                        startActivity(intent);
                        //this should be the landing page after logging in regardless of user's class
                        // TODO: no, need to check type of the user and direct to either StudentActivity or ManagerActivity
                    }
                }

            }
        });

        eBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, StartPage.class);
                startActivity(it);
            }
        });

    }
    private boolean validate(String name, String password){
        //will call the server function that allows to check if email/password combo is legitimate
        return true;
    }
}