package com.team10.trojancheckinout.model;

import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import org.apache.commons.lang3.ObjectUtils;

import androidx.annotation.NonNull;


public class Server {
    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;
    private static final String TAG = "MyActivity";
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
                        callback.onSuccess(building);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public static void removeBuilding(String id, Callback<Building> callback){
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

    public static void listenForBuildings(Listener<Building> listener) {
        // TODO: replace this
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
                        building.setMaxCapacity(maxCapacity);
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
        //TODO: replace this
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
                        if(student.getCurrentBuilding() == building.getName()) {
                            callback.onFailure(new Exception("Student already checked into building"));
                        }else{
                            if(building.getCurrentCapacity() == building.getMaxCapacity()){
                                callback.onFailure(new Exception("Building Full"));
                            }else{
                                if(student.getCurrentBuilding() != null){
                                    // If student is checked in at another building
                                    db.collection("building").whereEqualTo("name", student.getCurrentBuilding()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                                            DocumentReference oldDocRef = document.getReference();
                                                            int oldBuildingCapacity = (int)document.getData().get("currentCapacity");
                                                            oldDocRef.update("currentCapacity", oldBuildingCapacity-1);
                                                        }
                                                        docRef.update("currentCapacity", building.getCurrentCapacity()+1);
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

                                }else{
                                    docRef.update("currentCapacity", building.getCurrentCapacity()+1);

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
        //TODO: replace this
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


    public static void listenForCheckedInStudents(String buildingId, Listener<Student> listener) {
        // TODO: listen to query "students where student's current building = building id"
        db.collection("building")
                .whereEqualTo("buildingID", buildingId)
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
                                     String buildingName, int studentId, String major,
                                     Callback<Record> callback) { // TODO: change to listener? technically a callback would suffice, though, b/c records are never removed/updated
        // TODO: replace this
        callback.onSuccess(new Record(buildingName, true));
        callback.onSuccess(new Record(buildingName, false));
        callback.onSuccess(new Record(buildingName, true));
    }
}
