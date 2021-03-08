package com.team10.trojancheckinout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StudentRegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //Student stud = new Student(1, "ho", "brah", "man@usc.edu", "https://photo.jpg", "CS");
    String[] majors = new String[]{"CSCI", "CECS", "CSBA", "BME", "AME", "BISC", "CHEM", "PHYS"};
    Spinner spin;

    private EditText fname;
    private EditText lname;
    private EditText sEmail;
    private EditText sPassword;
    private EditText sID;

    private String selectedMajor;

    private Button sBack;
    private Button sRegister;

    private boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        spin = findViewById(R.id.sMajors);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, majors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        fname = findViewById(R.id.etSFname);
        lname = findViewById(R.id.etSLname);
        sEmail = findViewById(R.id.etSEmail);
        sPassword = findViewById(R.id.etSPassword);
        sID = findViewById(R.id.etUSCid);

        sBack = findViewById(R.id.esBackButton);
        sRegister = findViewById(R.id.sRegBtn);

        sRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iFname = fname.getText().toString();
                String iLname = lname.getText().toString();
                String iEmail = sEmail.getText().toString();
                String iPassword = sPassword.getText().toString();
                String iID = sID.getText().toString();

                isValid = validate(iFname,iLname,iEmail,iPassword,iID);

                if(isValid){
                    Toast.makeText(StudentRegisterActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    //this should lead to studentActivity class which currently doesn't exist
                    Intent intent = new Intent(StudentRegisterActivity.this, ManagerActivity.class);
                    startActivity(intent);
                    //this should be the landing page after logging in regardless of user's class
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
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), "Selected Major: "+majors[position] ,Toast.LENGTH_SHORT).show();
        selectedMajor = majors[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // none
    }

    private boolean validate(String fname, String lname, String email, String password, String ID){
        return false;
    }

}