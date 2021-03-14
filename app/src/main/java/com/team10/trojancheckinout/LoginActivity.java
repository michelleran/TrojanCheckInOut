package com.team10.trojancheckinout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;
import com.team10.trojancheckinout.model.User;

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
                } else {
                    Server.login(inputName, inputPassword, new Callback<User>() {
                        @Override
                        public void onSuccess(User result) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // open user's profile
                            Intent intent;
                            if (result instanceof Student) {
                                intent = new Intent(LoginActivity.this, StudentActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, ManagerActivity.class);
                            }
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            // TODO: not necessarily b/c of incorrect credentials
                            counter--;
                            Toast.makeText(LoginActivity.this, "Incorrect credentials entered!", Toast.LENGTH_SHORT).show();

                            eAttemptsInfo.setText("No. of attempts remaining: " + counter);

                            if(counter == 0){
                                eLogin.setEnabled(false);
                            }
                        }
                    });
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
}