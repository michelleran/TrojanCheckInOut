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
        callback.onSuccess(new Building(id, id, "", 30));
    }

    public static void listenForBuildings(Listener<Building> listener) {
        // TODO: replace this
        listener.onAdd(new Building("Building 1", "Building 1", "", 10));
        listener.onAdd(new Building("Building 2", "Building 2", "", 20));
    }
    
    public static void listenForCheckedInStudents(String buildingId, Listener<Student> listener) {
        // TODO: listen to query "students where student's current building = building id"
        listener.onAdd(testStudent);
    }

    public static void searchHistory(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                     int endYear, int endMonth, int endDay, int endHour, int endMin,
                                     String buildingName, int studentId, String major,
                                     Callback<Record> callback) { // TODO: change to listener? technically a callback would suffice, though, b/c records are never removed/updated
        // TODO: replace this
        callback.onSuccess(new Record(buildingName, true));
        callback.onSuccess(new Record(buildingName, false));
        callback.onSuccess(new Record(buildingName, true));
    }
}
