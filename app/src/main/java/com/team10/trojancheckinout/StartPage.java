package com.team10.trojancheckinout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPage extends AppCompatActivity {

    private Button elogin;
    private Button estudentRegister;
    private Button emanagerRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        elogin = findViewById(R.id.startLoginbtn);
        estudentRegister = findViewById(R.id.studentRegisterBtn);
        emanagerRegister = findViewById(R.id.managerRegisterBtn);

        elogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartPage.this, LoginActivity.class);
                startActivity(i);
            }
        });

        estudentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(StartPage.this, StudentRegisterActivity.class);
                startActivity(ii);
            }
        });

        emanagerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iii = new Intent(StartPage.this, ManagerRegisterActivity.class);
                startActivity(iii);
            }
        });


    }
}