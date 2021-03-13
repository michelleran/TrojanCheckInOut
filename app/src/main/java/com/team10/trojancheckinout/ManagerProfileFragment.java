package com.team10.trojancheckinout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.team10.trojancheckinout.model.Manager;
import com.team10.trojancheckinout.model.Server;

public class ManagerProfileFragment extends Fragment {

    private TextView txtGivenName;
    private TextView txtSurname;
    private TextView txtEmail;
    private Manager currentManager;
    private Button btnEdit;
    private Button btnLogout;
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private ImageView imgPhoto;
    private int viewState;



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
                handleLeftButton();
            }
        });

        return rootView;
    }

    public void handleLeftButton() {
        /* Toggling the visibility of the First Name, Last Name, and Email fields.
           Making the TextView invisible and EditView visible for the edit component.
         */
        if (viewState == 0) {
            viewState = 1;

            txtGivenName.setVisibility(View.INVISIBLE);
            edtNewPassword.setVisibility(View.VISIBLE);

            txtSurname.setVisibility(View.INVISIBLE);
            edtConfirmPassword.setVisibility(View.VISIBLE);

            txtEmail.setVisibility(View.INVISIBLE);

            btnEdit.setText("CANCEL");
            btnLogout.setText("SAVE");

            imgPhoto.setColorFilter(Color.rgb(64, 64, 64));
            imgPhoto.setImageAlpha(200);
        }
        else if (viewState == 1) {
            viewState = 0;

            edtNewPassword.setVisibility(View.INVISIBLE);
            txtGivenName.setVisibility(View.VISIBLE);

            edtConfirmPassword.setVisibility(View.INVISIBLE);
            txtSurname.setVisibility(View.VISIBLE);

            txtEmail.setVisibility(View.VISIBLE);

            btnEdit.setText("EDIT");
            btnLogout.setText("LOGOUT");

            imgPhoto.clearColorFilter();
            imgPhoto.setImageAlpha(255);
        }


    }


}
