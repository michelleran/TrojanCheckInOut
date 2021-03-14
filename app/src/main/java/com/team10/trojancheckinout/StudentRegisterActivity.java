package com.team10.trojancheckinout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.utils.Validator;

import static com.team10.trojancheckinout.utils.Validator.validateEmail;
import static com.team10.trojancheckinout.utils.Validator.validateID;
import static com.team10.trojancheckinout.utils.Validator.validateNotEmpty;
import static com.team10.trojancheckinout.utils.Validator.validatePassword;

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
        sPhoto = findViewById(R.id.sAddPhoto);

        sRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iFname = fname.getText().toString().trim();
                String iLname = lname.getText().toString().trim();
                String iEmail = sEmail.getText().toString().trim();
                String iPassword = sPassword.getText().toString();
                String iID = sID.getText().toString().trim();

                String [] allEntries = new String[] {iFname, iLname, iEmail, iPassword, iID};
                isValid = validateNotEmpty(allEntries, allEntries.length) && validateEmail(iEmail) && validatePassword(iPassword) && validateID(iID);
                if(!validateNotEmpty(allEntries, allEntries.length)){
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

                if(isValid){
                    Toast.makeText(StudentRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    //server.registerStudent(iFname, iLname, iID, iEmail, iPassword, Callback<Student> callback)
                    //if(gotImage){server.changePhoto(imageURI, Callback<Student> callback);}

                    Intent intent = new Intent(StudentRegisterActivity.this, StudentActivity.class);
                    startActivity(intent);
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

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), "Selected Major: "+majors[position] ,Toast.LENGTH_SHORT).show();
        selectedMajor = majors[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // none
    }

    /*private boolean validate(String fname, String lname, String email, String password, String ID){
        //Toast.makeText(getApplicationContext(), "Email: " + email ,Toast.LENGTH_SHORT).show();
        if(fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty() || ID.isEmpty()){
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
        else if(ID.length() != 10){
            Toast.makeText(getApplicationContext(), "Please enter your TEN digit USC ID!" ,Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(getApplicationContext(), "Selected Major: "+ selectedMajor ,Toast.LENGTH_SHORT).show();
        return true;
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