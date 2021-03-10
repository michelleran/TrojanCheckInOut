package com.team10.trojancheckinout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.team10.trojancheckinout.model.Manager;
import com.team10.trojancheckinout.model.Server;

public class ManagerProfileFragment extends Fragment {

    private TextView txtGivenName;
    private TextView txtSurname;
    private TextView txtEmail;
    private Manager currentManager;
    private Button btnEdit;
    private EditText edtGivenName;
    private EditText edtSurname;
    private EditText edtEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_manager_profile, container, false);

        // TODO

        txtGivenName = (TextView) rootView.findViewById(R.id.txtGivenName);
        txtSurname = (TextView) rootView.findViewById(R.id.txtSurname);
        txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
        btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
        edtGivenName = (EditText) rootView.findViewById(R.id.edtGivenName);
        edtSurname = (EditText) rootView.findViewById(R.id.edtSurname);
        edtEmail = (EditText) rootView.findViewById(R.id.edtEmail);

        currentManager = (Manager) Server.getCurrentUser();

        txtGivenName.setText("First Name: " + currentManager.getGivenName());
        txtSurname.setText("Surname: " + currentManager.getSurname());
        txtEmail.setText("Email: " + currentManager.getEmail());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEdit();
            }
        });

        return rootView;
    }

    public void onEdit() {
        /* Toggling the visibility of the First Name, Last Name, and Email fields.
           Making the TextView invisible and EditView visible for the edit component.
         */
        txtGivenName.setVisibility(View.INVISIBLE);
        edtGivenName.setVisibility(View.VISIBLE);

        txtSurname.setVisibility(View.INVISIBLE);
        edtSurname.setVisibility(View.VISIBLE);

        txtEmail.setVisibility(View.INVISIBLE);
        edtEmail.setVisibility(View.VISIBLE);
    }


}
