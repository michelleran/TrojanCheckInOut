package com.team10.trojancheckinout.model;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

public class Server {
    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;

    public static void initialize() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // TODO: anything else
    }

    public static void listenForBuildings(Listener<Building> listener) {
        // TODO: replace this dummy implementation
        listener.onAdd(new Building("Building 1"));
        listener.onAdd(new Building("Building 2"));
    }
}
