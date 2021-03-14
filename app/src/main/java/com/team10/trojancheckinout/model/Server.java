package com.team10.trojancheckinout.model;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.net.Uri;
import androidx.annotation.NonNull;


import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


import androidx.annotation.Nullable;


public class Server {
    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static StorageReference storage;

    private static final String USER_COLLECTION = "users";
    private static final String BUILDING_COLLECTION = "buildings";
    private static final String RECORD_COLLECTION = "records";

    private static final String TAG = "Server";

    public static void initialize() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public static void login(String email, String password, Callback<User> callback){
        if(auth.getCurrentUser()!=null) {
            Log.d("login", "Already Signed In");
            getCurrentUser(callback);
            return;
        } //user already exists
        else {
            auth.getCurrentUser();
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // log success
                            Log.d("login", "signInWithEmail:success");
                            getCurrentUser(callback);
                        }
                        else {
                            //log failure
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
        }
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
        Log.w("log out", "log out attempt");
    }

    public static void managerRegister(String givenName, String surname, String email,
                                       Uri file, String password, Callback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password) //also logs in the user
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //auth register
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("registerStudent", "createUserWithEmail:success");
                            //loginUser(email, password, null, params, callback); //redundant
                            writeUserData("", givenName, surname, email, file, null, callback, false);
                        }
                        else {
                            Log.w("registerStudent", "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public static void studentRegister(String id, String givenName, String surname, String email,
                                   Uri file, String major, String password, Callback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password) //also logs in the user
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //auth register
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("registerStudent", "createUserWithEmail:success");
                            writeUserData(id, givenName, surname, email, file, major, callback, true);
                        }
                        else {
                            Log.w("registerStudent", "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    private static void writeUserData(String id, String givenName, String surname, String email,
                                      Uri file, String major, Callback<User> callback, boolean isStudent) {
        Map<String, Object> s = new HashMap<>();
        s.put("givenName", givenName);
        s.put("surname", surname);
        s.put("email", email);
        s.put("student", isStudent);
        if(isStudent) {
            s.put("id", id);
            s.put("deleted", false);
            s.put("major", major);
        }

        String uID = auth.getCurrentUser().getUid();
        StorageReference fileRef = storage.child("images/"+uID + file.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(file);
        uploadTask.addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
            @Override
            public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("Photo Uri", "Upload is " + progress + "% done");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("Photo Uri", "could not handle Uri");
                callback.onFailure(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        s.put("photoUrl", uri.toString());
                        Log.d("Photo Uri", "could not handle Uri");
                        FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .set(s)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("add user", "DocumentSnapshot added with ID: ");
                                    getCurrentUser(callback);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("add user", "Error adding document", e);
                                    callback.onFailure(e);
                                }
                            });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
            }
        });
    }

    public static String getCurrentUserId() {
        if (auth.getCurrentUser() == null)
            return null;
        return auth.getCurrentUser().getUid();
    }

    public static void getCurrentUser(Callback<User> callback) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Log.d("getUser", "No Logged In User");
            callback.onFailure(null);
            return;
        }
        DocumentReference docRef = db.collection(USER_COLLECTION)
                .document(auth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){ //success
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("getUser", "DocumentSnapshot data: " + document.getData());
                        //return document.getData()
                        boolean student = (boolean) document.getData().get("student");
                        String givenName = (String) document.getData().get("givenName");
                        String surname = (String) document.getData().get("surname");
                        String id = (String) document.getData().get("id");
                        String email = (String) document.getData().get("email");
                        String photoUrl = (String) document.getData().get("photoUrl");
                        if(student) { //if a student
                            //can also check if the user is deleted by checking against "deleted"
                            boolean deleted = (boolean) document.getData().get("deleted");
                            String major = (String) document.getData().get("major");
                            Student profile = new Student(id, givenName, surname, email, photoUrl, major);
                            //Student profile = document.toObject(Student.class);  //doesn't work because i am checking if they are a student
                            //handling that we succeeded
                            callback.onSuccess(profile);
                        }
                        else if(!student) { //manager
                            //make manager
                            Manager manager = new Manager(id, givenName, surname, email, photoUrl);
                            callback.onSuccess(manager);
                        }
                    } else {
                       Log.d("getUSer", "No such document");
                       callback.onFailure(task.getException());

                    }
                } else {
                    Log.d("getUser", "get failed with ", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public static void deleteManager(Callback<Void> callback) {
        if(auth.getCurrentUser()==null){
            Log.d("userDelete", "no user logged in");
            callback.onFailure(null);
            return;
        }
        DocumentReference docRef = db.collection(USER_COLLECTION)
                .document(auth.getCurrentUser().getUid()); //get the current user document
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("profile", "full deleted");
                        FirebaseUser user = auth.getCurrentUser();
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callback.onSuccess(null);
                                } else {
                                    callback.onFailure(task.getException());
                                }

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("profile", "profile not deleted", e);
                        callback.onFailure(e);
                    }
                });

    }

    public static void deleteStudent(Callback<Void> callback){
        if(auth.getCurrentUser()==null){
            Log.d("userDelete", "no user logged in");
            callback.onFailure(null);
            return;
        }
        String uID = auth.getCurrentUser().getUid();
        FirebaseUser user = auth.getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("userDelete", "User account deleted.");
                            DocumentReference docRef = db.collection(USER_COLLECTION)
                                    .document(auth.getCurrentUser().getUid()); //get the current user document
                            docRef.update("deleted", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("profile", "profile set to deleted");
                                            callback.onSuccess(null);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("profile", "Profile not set", e);
                                            callback.onFailure(e);
                                        }
                                    });
                        }
                        else{
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public static void getStudent(String uID, Callback<Student> callback) {
        DocumentReference docRef = db.collection(USER_COLLECTION)
                .document(uID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){ //success
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("getStudent", "DocumentSnapshot data: " + document.getData());
                        //return document.getData()
                        boolean deleted = (boolean) document.getData().get("deleted");
                        boolean student = (boolean) document.getData().get("student");
                        if(student) { //if a student
                            //can also check if the user is deleted by checking against "deleted"

                            String givenName = (String) document.getData().get("givenName");
                            String surname = (String) document.getData().get("surname");
                            String id = (String) document.getData().get("id");
                            String major = (String) document.getData().get("major");
                            String email = (String) document.getData().get("email");
                            String photoUrl = (String) document.getData().get("photoUrl");
                            Student profile = new Student(id, givenName, surname, email, photoUrl, major);
                            //handling that we succeeded
//                            callback.notify();
                            callback.onSuccess(profile);
                        }
                        else if(!student) { //manager
                            Log.d("getStudent", "Trying to access a manager");
                            callback.onFailure(null);
                        }
                    } else {
                        Log.d("getStudent", "No such document");
                        callback.onFailure(task.getException());
                    }
                } else {
                    Log.d("getStudent", "get failed with ", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public static void changePassword(String newPassword, Callback<Void> callback){  //changes current user's password
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("changePassword", "User password updated.");
                            //do ui stuff
                            callback.onSuccess(null);
                        }
                        else{
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public static void changePhoto(Uri file, Callback<String> callback){
        if(auth.getCurrentUser()==null){
            Log.d("changePhoto", "No Logged In User");
            callback.onFailure(null);
            return;
        }

        String uID = auth.getCurrentUser().getUid();
        StorageReference fileRef = storage.child("images/"+uID + file.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(file);
        uploadTask.addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("Photo Uri", "Upload is " + progress + "% done");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("Photo Uri", "could not handle Uri");
                callback.onFailure(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(TaskSnapshot taskSnapshot) {
                Log.d("Photo Uri", "handled Uri succesfully");
                String URL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                DocumentReference docRef = db.collection(USER_COLLECTION)
                        .document(auth.getCurrentUser().getUid()); //get the current user document
                docRef.update("PhotoUrl", URL)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("changePhotoUrl", "DocumentSnapshot successfully updated!");
                            callback.onSuccess(URL);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("changePhotoUrl", "Error updating document", e);
                            callback.onFailure(e);
                        }
                    });
            }
        });
    }

    public static void getBuilding(String id, Callback<Building> callback) {
        // Get building from database
        DocumentReference docRef = db.collection(BUILDING_COLLECTION).document(id);
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

    public static void addBuilding() {
        // TODO
    }

    public static void removeBuilding(String id, Callback<Void> callback){
        db.collection(BUILDING_COLLECTION).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSuccess(aVoid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static void listenForBuildings(Listener<Building> listener) {
        db.collection(BUILDING_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    listener.onAdd(dc.getDocument().toObject(Building.class));
                                    Log.d(TAG, "New building: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    listener.onUpdate(dc.getDocument().toObject(Building.class));
                                    Log.d(TAG, "Modified building: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    listener.onRemove(dc.getDocument().toObject(Building.class));
                                    Log.d(TAG, "Removed building: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    public static void setBuildingMaxCapacity(String id, int maxCapacity, Callback<Building> callback){
        initialize();
        final DocumentReference buildingDocRef = db.collection(BUILDING_COLLECTION).document(id);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Building building = transaction.get(buildingDocRef).toObject(Building.class);
                // If new max capacity is smaller than old max capacity
                if(building.getMaxCapacity() > maxCapacity){
                    callback.onFailure(new Exception("New capacity is smaller than old capacity"));
                }else{
                    transaction.update(buildingDocRef, "maxCapacity", maxCapacity);
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
        // Get building and student document references
        final DocumentReference newBuildingRef = db.collection(BUILDING_COLLECTION).document("id");
        final DocumentReference studentRef = db.collection(USER_COLLECTION).document(auth.getUid());
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Building newBuilding = transaction.get(newBuildingRef).toObject(Building.class);
                Student student = transaction.get(studentRef).toObject(Student.class);

                if (newBuilding == null || student == null) {
                    callback.onFailure(new Exception("Failed to get building or student"));
                    return null;
                }

                // Check if student is already checked in to building
                if(student.getCurrentBuilding() != null && student.getCurrentBuilding().equals(id)) {
                    callback.onFailure(new Exception("Student is already checked into building"));
                    return null;
                }

                // Check if building is full
                if(newBuilding.getCurrentCapacity() == newBuilding.getMaxCapacity()) {
                    callback.onFailure(new Exception("Building Full"));
                    return null;
                }

                // Check if student is already checked into a building
                if(student.getCurrentBuilding() == null) {
                    transaction.update(newBuildingRef, "currentCapacity", newBuilding.getCurrentCapacity() + 1);
                    transaction.update(studentRef, "currentBuilding", newBuilding.getName());
                    Record record = new Record(id, newBuilding.getName(), student.getMajor(), true);
                    addRecord(record);
                }else{
                    final DocumentReference oldBuildingRef = db.collection(BUILDING_COLLECTION).document(student.getCurrentBuilding());
                    Building oldBuilding = transaction.get(oldBuildingRef).toObject(Building.class);
                    // Update each buildings capacity
                    transaction.update(newBuildingRef, "currentCapacity", newBuilding.getCurrentCapacity() + 1);
                    transaction.update(oldBuildingRef, "currentCapacity", oldBuilding.getCurrentCapacity() - 1);
                    // Update student's current building
                    transaction.update(studentRef, "currentBuilding", newBuilding.getName());

                    // Generate records
                    Record record1 = new Record(id, newBuilding.getName(), student.getMajor(), true);
                    addRecord(record1);
                    Record record2 = new Record(oldBuilding.getId(), oldBuilding.getName(), student.getMajor(), false);
                    addRecord(record2);
                }

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onSuccess(aVoid);
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

        final DocumentReference buildingDocRef = db.collection(BUILDING_COLLECTION).document(id);
        final DocumentReference studentDocRef = db.collection(USER_COLLECTION).document(auth.getUid());
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NotNull Transaction transaction) throws FirebaseFirestoreException {
                Building building = transaction.get(buildingDocRef).toObject(Building.class);
                Student student = transaction.get(studentDocRef).toObject(Student.class);

                if (building == null || student == null) {
                    callback.onFailure(new Exception("Failed to get student or building"));
                    return null;
                }

                if(student.getCurrentBuilding() == null){
                    callback.onFailure(new Exception("Student is not checked into a building"));
                    return null;
                }

                // Update database
                transaction.update(buildingDocRef, "currentCapacity", building.getCurrentCapacity() - 1);
                transaction.update(studentDocRef, "currentBuilding", null);

                // Generate records
                Record record = new Record(id, building.getName(), student.getMajor(), false);
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
        db.collection(RECORD_COLLECTION)
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
        db.collection(USER_COLLECTION)
                .whereEqualTo("currentBuilding", buildingId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    listener.onAdd(dc.getDocument().toObject(Student.class));
                                    Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    listener.onUpdate(dc.getDocument().toObject(Student.class));
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    listener.onRemove(dc.getDocument().toObject(Student.class));
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    public static void filterRecords(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                     int endYear, int endMonth, int endDay, int endHour, int endMin,
                                     String buildingName, long studentId, String major,
                                     Callback<Record> callback) {
        Query query = db.collection(RECORD_COLLECTION);
        // Set start parameters
        if(startYear != -1){
            query = query.whereGreaterThanOrEqualTo("year", startYear);
        }
        if(startMonth != -1){
            query = query.whereGreaterThanOrEqualTo("month", startMonth);
        }
        if(startDay != -1){
            query = query.whereGreaterThanOrEqualTo("day", startDay);
        }
        if(startHour != -1){
            query = query.whereGreaterThanOrEqualTo("hour", startDay);
        }
        if(startMin != -1){
            query = query.whereGreaterThanOrEqualTo("minute", startMin);
        }

        // Set end parameters
        if(endYear != -1){
            query = query.whereLessThanOrEqualTo("year", endYear);
        }
        if(endMonth != -1){
            query = query.whereLessThanOrEqualTo("month", endYear);
        }
        if(endDay != -1){
            query = query.whereLessThanOrEqualTo("day", endDay);
        }
        if(endHour != -1){
            query = query.whereLessThanOrEqualTo("hour", endDay);
        }
        if(endMin != -1){
            query = query.whereLessThanOrEqualTo("minute", endMin);
        }

        // Filter by building name
        if (!buildingName.isEmpty()) {
            query = query.whereEqualTo("buildingName", buildingName);
        }

        // Filter by student
        if(studentId != -1){
            query = query.whereEqualTo("studentId", studentId);
        }

        // Filter by major
        if(!major.isEmpty()){
            query = query.whereEqualTo("major", major);
        }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(new Exception(error.getMessage()));
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        callback.onSuccess(dc.getDocument().toObject(Record.class));
                    }
                }
            }
        });

        /*query
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
            });*/
    }
}
