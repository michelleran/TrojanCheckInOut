package com.team10.trojancheckinout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.team10.trojancheckinout.model.Manager;
import com.team10.trojancheckinout.model.Server;

public class ManagerProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
            R.layout.fragment_manager_profile, container, false);

        // TODO

        TextView txtGivenName = (TextView) rootView.findViewById(R.id.txtGivenName);
        TextView txtSurname = (TextView) rootView.findViewById(R.id.txtSurname);
        TextView txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);

        Manager manager = (Manager) Server.getCurrentUser();

        txtGivenName.setText("First Name: " + manager.getGivenName());
        txtSurname.setText("Surname: " + manager.getSurname());
        txtEmail.setText("Email: " + manager.getEmail());

        return rootView;
    }

}
