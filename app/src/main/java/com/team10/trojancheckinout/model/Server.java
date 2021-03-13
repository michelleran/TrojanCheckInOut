package com.team10.trojancheckinout.model;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.net.Uri;
import android.telecom.Call;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import androidx.annotation.NonNull;


public class Server {
    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;

    private static final String TAG = "MyActivity";
    private static final String[] majors = { "Major 1", "Major 2" };

    // TODO: delete later
    private static final Student testStudent =
        new Student(0, "Test", "User", "test@usc.edu", "https://upload.wikimedia.org/wikipedia/commons/b/bb/Kittyply_edit1.jpg", "CSCI");

    public static void initialize() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // TODO: anything else
    }

    public static String[] getMajors() { return majors; }

    public static User getCurrentUser() {
        // TODO: replace this
        return testStudent;
    }

    public static void getStudent(String id, Callback<Student> callback) {
        // TODO: replace this
        callback.onSuccess(testStudent);
    }

    public static void logout(Callback<Void> callback){
        //TODO: replace this
    }

    public static void changePassword(String newPassword, Callback<Void> callback){
        //TODO: replace this
    }

    public static void changePhoto(Uri uri, Callback<String> callback){
        //TODO: replace this
    }

    public static void deleteAccount(Callback<Void> callback){
        //TODO: replace this
    }

    public static void getBuilding(String id, Callback<Building> callback) {
        initialize();
        // Get building from database
        DocumentReference docRef = db.collection("buildings").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        // Convert document to building object
                        Building building = document.toObject(Building.class);
                        callback.onSuccess(building);
                    } else {
                        callback.onFailure(new Exception("No such document"));
                        Log.d(TAG, "No such document");
                    }
                } else {
                    callback.onFailure(task.getException());
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public static void removeBuilding(String id, Callback<Building> callback){
        initialize();
        // Get building to be removed
        DocumentReference docRef = db.collection("buildings").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Building building = document.toObject(Building.class);
                        // Delete building
                        docRef.delete();
                        callback.onSuccess(building);
                    } else {
                        callback.onFailure(new Exception("No such document"));
                        Log.d(TAG, "No such document");
                    }
                } else {
                    callback.onFailure(task.getException());
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public static void checkIn(String id, Callback<Void> callback){
        //TODO: replace this
    }

    public static void checkOut(String id, Callback<Void> callback){
        //TODO: replace this
    }

    public static void listenForBuildings(Listener<Building> listener) {
        initialize();
        db.collection("buildings").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Building building = document.toObject(Building.class);
                                listener.onAdd(building);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void setBuildingMaxCapacity(String id, int maxCapacity, Callback<Building> callback){
        initialize();
        DocumentReference docRef = db.collection("buildings").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Building building = document.toObject(Building.class);
                        if(building.getMaxCapacity() > maxCapacity){
                            callback.onFailure(new Exception("New capacity is smaller than old capacity"));
                        }
                        // Update capacity
                        building.setMaxCapacity(maxCapacity);
                        // Update capacity on database
                        docRef.update("maxCapacity", maxCapacity);
                        callback.onSuccess(building);

                    } else {
                        Log.d(TAG, "No such document");
                        callback.onFailure(new Exception("No such document"));
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public static void checkIn(String id, Callback<Void> callback){
        initialize();
        DocumentReference docRef = db.collection("buildings").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Building building = document.toObject(Building.class);
                        Student student = (Student) getCurrentUser();
                        // Check if student is already checked in to building
                        if(student.getCurrentBuilding() == building.getName()) {
                            callback.onFailure(new Exception("Student already checked into building"));
                        }else{
                            // Check if building is full
                            if(building.getCurrentCapacity() == building.getMaxCapacity()){
                                callback.onFailure(new Exception("Building Full"));
                            }else{
                                // Check if user is already checked in to another building
                                if(student.getCurrentBuilding() != null){
                                    // Find other building student is checked into
                                    db.collection("building").whereEqualTo("name", student.getCurrentBuilding()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                                            DocumentReference oldDocRef = document.getReference();
                                                            // Decrease capacity of old building
                                                            int oldBuildingCapacity = (int)document.getData().get("currentCapacity");
                                                            oldDocRef.update("currentCapacity", oldBuildingCapacity-1);
                                                            Record record = new Record(oldDocRef.getId(), false);
                                                            addRecord(record);
                                                        }
                                                        // Add capacity to new building
                                                        Record record = new Record(id, true);
                                                        addRecord(record);
                                                        docRef.update("currentCapacity", building.getCurrentCapacity()+1);
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

                                }else{
                                    // Add capacity to new building
                                    docRef.update("currentCapacity", building.getCurrentCapacity()+1);
                                    // Add record
                                    Record record = new Record(id, true);
                                    addRecord(record);

                                }
                            }
                        }
                    } else {
                        callback.onFailure(new Exception("No such document"));
                        Log.d(TAG, "No such document");
                    }
                } else {
                    callback.onFailure(task.getException());
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public static void checkOut(String id, Callback<Void> callback){
        initialize();
        DocumentReference docRef = db.collection("buildings").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Building building = document.toObject(Building.class);
                        docRef.update("currentCapacity", building.getCurrentCapacity()-1);

                        Record record = new Record(id, false);
                        // Add checkout record
                        addRecord(record);
                    } else {
                        Log.d(TAG, "No such document");
                        callback.onFailure(new Exception("Error"));
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });

    }

    private static void addRecord(Record record){
        initialize();
        db.collection("records")
                .add(record)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    public static void listenForCheckedInStudents(String buildingId, Listener<Student> listener) {
        // TODO: listen to query "students where student's current building = building id"
        initialize();
        db.collection("Student")
                .whereEqualTo("currentBuilding", buildingId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Student student = document.toObject(Student.class);
                                listener.onAdd(student);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void searchHistory(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                     int endYear, int endMonth, int endDay, int endHour, int endMin,
                                     String buildingName, long studentId, String major,
                                     Callback<Record> callback) { // TODO: change to listener? technically a callback would suffice, though, b/c records are never removed/updated
        // TODO: replace this
        initialize();
        CollectionReference records = db.collection("record");
        records
                // TO DO: Add other queries
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Record record = document.toObject(Record.class);
                                callback.onSuccess(record);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
