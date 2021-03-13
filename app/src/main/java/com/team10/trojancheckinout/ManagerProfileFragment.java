package com.team10.trojancheckinout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Manager;
import com.team10.trojancheckinout.model.Server;

import static android.app.Activity.RESULT_OK;

public class ManagerProfileFragment extends Fragment {

    private TextView txtGivenName;
    private TextView txtSurname;
    private TextView txtEmail;
    private Manager currentManager;
    private Button btnEdit;
    private Button btnChangePicture;
    private Button btnLogout;
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private ImageView imgPhoto;
    private int viewState;
    private static final int SELECT_PHOTO_REQUEST = 1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_manager_profile, container, false);

        // TODO
        viewState = 0;
        txtGivenName = (TextView) rootView.findViewById(R.id.txtGivenName);
        txtSurname = (TextView) rootView.findViewById(R.id.txtSurname);
        txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
        btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
        btnChangePicture = (Button) rootView.findViewById(R.id.btnChangePicture);
        btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        edtNewPassword = (EditText) rootView.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = (EditText) rootView.findViewById(R.id.edtConfirmPassword);
        imgPhoto = (ImageView) rootView.findViewById(R.id.imgPhoto);
        Glide.with(this).load("https://www.clipartkey.com/mpngs/m/238-2383342_transparent-pizza-clip-art-transparent-cartoons-whole-pizza.png").override(400, 400).into(imgPhoto);

        currentManager = (Manager) Server.getCurrentUser_manager(); // TODO: replace w/ getCurrentUser once implemented

        txtGivenName.setText("First Name: " + currentManager.getGivenName());
        txtSurname.setText("Surname: " + currentManager.getSurname());
        txtEmail.setText("Email: " + currentManager.getEmail());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEditPasswordButton();
            }
        });

        btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleChangePasswordButton();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogout();
            }
        });


        return rootView;
    }

    public void handleLogout() {
        Server.logOutUser();
        Toast.makeText(getActivity(), "Logout Successful", Toast.LENGTH_SHORT).show();
        Intent loginPage = new Intent(getActivity(), StartPage.class);
        startActivity(loginPage);
    }

    public void handleEditPasswordButton() {
        if (viewState == 0) {
            viewState = 1; //Change Password View

            txtGivenName.setVisibility(View.INVISIBLE);
            edtNewPassword.setVisibility(View.VISIBLE);
            txtSurname.setVisibility(View.INVISIBLE);
            edtConfirmPassword.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.INVISIBLE);

            btnEdit.setText("CANCEL");
            btnChangePicture.setText("SAVE");
        }
        else if (viewState == 1) {
            viewState = 0;

            edtNewPassword.setVisibility(View.INVISIBLE);
            txtGivenName.setVisibility(View.VISIBLE);
            edtConfirmPassword.setVisibility(View.INVISIBLE);
            txtSurname.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);

            btnEdit.setText("EDIT PASSWORD");
            btnChangePicture.setText("CHANGE PHOTO");
        }
    }

    public void handleChangePasswordButton() {
        if (viewState == 1) {
            String newPassword = edtNewPassword.getText().toString();
            String confirmPassword = edtConfirmPassword.getText().toString();
            int passwordValidation = validatePassword(newPassword, confirmPassword);
            if (passwordValidation == 2) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (passwordValidation == 1) {
                Toast.makeText(getActivity(), "Password(s) are empty", Toast.LENGTH_SHORT).show();
            }
            else {
                viewState = 0;
                Toast.makeText(getActivity(), "Password successfully updated", Toast.LENGTH_LONG).show();
                Server.changePassword(newPassword);

                edtNewPassword.setVisibility(View.INVISIBLE);
                txtGivenName.setVisibility(View.VISIBLE);
                edtConfirmPassword.setVisibility(View.INVISIBLE);
                txtSurname.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);

                btnEdit.setText("EDIT PASSWORD");
                btnChangePicture.setText("CHANGE PHOTO");
            }

        }
        if (viewState == 0) {
            Intent choosePic = new Intent();
            choosePic.setType("image/*");
            choosePic.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(choosePic,"Select Photo"), SELECT_PHOTO_REQUEST);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data == null || data.getData() == null) {
                Toast.makeText(getActivity(), "Upload error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    public static int validatePassword(String newPassword, String confirmPassword) {
        if (newPassword.length() == 0 || confirmPassword.length() == 0) {
            return 1;
        }
         else {
            if (!newPassword.equals(confirmPassword)) {
                return 2;
            }
            else return 0;
        }
    }
}
