package com.team10.trojancheckinout.model;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

public class Server {
    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;

    // TODO: delete later
    private static final Student testStudent =
        new Student(0, "Test", "User", "test@usc.edu", "https://upload.wikimedia.org/wikipedia/commons/b/bb/Kittyply_edit1.jpg", "CSCI");

    public static void initialize() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // TODO: anything else
    }

    public static User getCurrentUser() {
        // TODO: replace this
        return testStudent;
    }

    public static void getStudent(String id, Callback<Student> callback) {
        // TODO: replace this
        callback.onSuccess(testStudent);
    }

    public static void getBuilding(String id, Callback<Building> callback) {
        // TODO: replace this
        callback.onSuccess(new Building("Test Building"));
    }

    public static void listenForBuildings(Listener<Building> listener) {
        // TODO: replace this
        listener.onAdd(new Building("Building 1"));
        listener.onAdd(new Building("Building 2"));
        listener.onAdd(new Building("Building 3"));
        listener.onRemove(new Building("Building 1"));
        // final dataset should be Building 2, Building 3
    }
}
