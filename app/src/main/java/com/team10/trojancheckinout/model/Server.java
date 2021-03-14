package com.team10.trojancheckinout.model;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Server {
    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;
    private static final String TAG = "Server";
    // TODO: delete later
    private static final Student testStudent =
        new Student(0, "Test", "User", "test@usc.edu", "https://upload.wikimedia.org/wikipedia/commons/b/bb/Kittyply_edit1.jpg", "CSCI");

    private static final HashMap<String,String> buildingNameIDMap = new HashMap<>();
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

    public static void listenForBuildings(Listener<Building> listener) {
        initialize();
        db.collection("building")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            listener.onAdd(dc.getDocument().toObject(Building.class));
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New building: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified building: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed building: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    public static void setBuildingMaxCapacity(String id, int maxCapacity, Callback<Building> callback){
        initialize();
        final DocumentReference buildingDocRef = db.collection("buildings").document(id);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(buildingDocRef);
                // If new max capacity is smaller than old max capacity
                if((int)snapshot.get("maxCapacity") > maxCapacity){
                    callback.onFailure(new Exception("New capacity is smaller than old capacity"));
                }else{
                    transaction.update(buildingDocRef, "maxCapacity", maxCapacity);
                    Building building = snapshot.toObject(Building.class);
                    building.setMaxCapacity(maxCapacity);
                    callback.onSuccess(building);
                }

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    public static void checkIn(String id, Callback<Void> callback){
        initialize();
        // Get building and student document references
        final DocumentReference newBuildingRef = db.collection("buildings").document("id");
        final DocumentReference studentRef = db.collection("students").document(auth.getUid());
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot newBuildingSnapshot = transaction.get(newBuildingRef);
                DocumentSnapshot studentSnapshot = transaction.get(studentRef);
                // Check if student is already checked in to building
                if(studentSnapshot.get("currentBuilding") == newBuildingSnapshot.get("name")) {
                    callback.onFailure(new Exception("Student is already checked into building"));
                    return null;
                }

                // Check if building is full
                if(newBuildingSnapshot.get("currentCapacity") == newBuildingSnapshot.get("maxCapacity")) {
                    callback.onFailure(new Exception("Building Full"));
                    return null;
                }

                // Check if student is already checked into a building
                if(studentSnapshot.get("currentBuilding") == null) {
                    transaction.update(newBuildingRef, "currentCapacity", (int)newBuildingSnapshot.get("currentCapacity")+1);
                    transaction.update(studentRef, "currentBuilding", newBuildingSnapshot.get("name"));
                    Record record = new Record(newBuildingRef.getId(), (String)studentSnapshot.get("major"), true);
                    addRecord(record);
                }else{
                    // Get student's old building
                    db.collection("building")
                            .whereEqualTo("name", studentSnapshot.get("currentBuilding"))
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            DocumentReference oldBuildingRef = document.getReference();
                                            // Update each buildings capacity
                                            transaction.update(newBuildingRef, "currentCapacity", (int)newBuildingSnapshot.get("currentCapacity")+1);
                                            transaction.update(oldBuildingRef, "currentCapacity", (int)document.get("currentCapacity")-1);
                                            // Update student's current building
                                            transaction.update(studentRef, "currentBuilding", newBuildingSnapshot.get("name"));

                                            // Generate records
                                            Record record1 = new Record(newBuildingRef.getId(),(String)studentSnapshot.get("major"), true);
                                            addRecord(record1);
                                            Record record2 = new Record(oldBuildingRef.getId(),(String)studentSnapshot.get("major"), false);
                                            addRecord(record2);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    public static void checkOut(String id, Callback<Void> callback){
        initialize();

        final DocumentReference buildingDocRef = db.collection("buildings").document(id);
        final DocumentReference studentDocRef = db.collection("students").document(auth.getUid());
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot buildingSnapshot = transaction.get(buildingDocRef);
                DocumentSnapshot studentSnapshot = transaction.get(studentDocRef);
                if(studentSnapshot.get("currentBuilding") == null){
                    callback.onFailure(new Exception("Student is not checked into a building"));
                    return null;
                }

                // Update database
                transaction.update(buildingDocRef, "currentCapacity", (int)buildingSnapshot.get("currentCapacity")-1);
                transaction.update(studentDocRef, "currentBuilding", null);

                // Generate records
                Record record = new Record(buildingDocRef.getId(),(String)studentSnapshot.get("major"), false);
                addRecord(record);
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
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

        db.collection("cities")
                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            listener.onAdd(dc.getDocument().toObject(Student.class));
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    public static void searchHistory(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                     int endYear, int endMonth, int endDay, int endHour, int endMin,
                                     String buildingName, int studentId, String major,
                                     Callback<Record> callback) { // TODO: change to listener? technically a callback would suffice, though, b/c records are never removed/updated
        initialize();
        CollectionReference records = db.collection("records");

        // Set start parameters
        if(startYear != -1){
            records.whereGreaterThanOrEqualTo("year", startYear);
        }
        if(startMonth != -1){
            records.whereGreaterThanOrEqualTo("month", startMonth);
        }
        if(startDay != -1){
            records.whereGreaterThanOrEqualTo("day", startDay);
        }
        if(startHour != -1){
            records.whereGreaterThanOrEqualTo("hour", startDay);
        }
        if(startMin != -1){
            records.whereGreaterThanOrEqualTo("minute", startMin);
        }

        // Set end parameters
        if(endYear != -1){
            records.whereLessThanOrEqualTo("year", endYear);
        }
        if(endMonth != -1){
            records.whereLessThanOrEqualTo("month", endYear);
        }
        if(endDay != -1){
            records.whereLessThanOrEqualTo("day", endDay);
        }
        if(endHour != -1){
            records.whereLessThanOrEqualTo("hour", endDay);
        }
        if(endMin != -1){
            records.whereLessThanOrEqualTo("minute", endMin);
        }

        // Filter by building name
        if(buildingName != ""){
            records.whereEqualTo("buildingID", buildingNameIDMap.get(buildingName));
        }

        // Filter by student
        if(studentId != -1){
            records.whereEqualTo("studentID", studentId);
        }

        // Filter by major
        if(major != ""){
            records.whereEqualTo("major", major);
        }

        records
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
