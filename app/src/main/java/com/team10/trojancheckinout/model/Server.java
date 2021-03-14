package com.team10.trojancheckinout.model;

import android.net.Uri;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class Server {
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;
    private static StorageReference storageRef;

    private static final String[] majors = { "Major 1", "Major 2" };

    // TODO: delete later
    private static final Student testStudent =
        new Student("0", "Test", "User", "test@usc.edu", "https://upload.wikimedia.org/wikipedia/commons/b/bb/Kittyply_edit1.jpg", "CSCI");

    public static void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String[] getMajors() { return majors; }

    public static void login(String email, String password, Callback<User> callback){
        if(mAuth.getCurrentUser()!=null) {
            Log.d("login", "Already Signed In");
            getCurrentUser(callback);
            return;
        } //user already exists
        
        else if(mAuth.getCurrentUser()==null) {
            mAuth.signInWithEmailAndPassword(email, password)
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

    public static void managerRegister(String id, String givenName, String surname, String email,
                                       Uri file, String password, Callback<User> callback) {
        mAuth.createUserWithEmailAndPassword(email, password) //also logs in the user
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //auth register
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("registerStudent", "createUserWithEmail:success");
                            //loginUser(email, password, null, params, callback); //redundant
                            addUser2(id, givenName, surname, email, file, null, callback, false);
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
        mAuth.createUserWithEmailAndPassword(email, password) //also logs in the user
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //auth register
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("registerStudent", "createUserWithEmail:success");
                            addUser2(id, givenName, surname, email, file, major, callback, true);
                        }
                        else {
                            Log.w("registerStudent", "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }


    private static void addUser2(String id, String givenName, String surname, String email, Uri file, String major
            ,Callback<User> callback, boolean isStudent) { //add user to the database
        Map<String, Object> s = new HashMap<>();
        //s.put("id", id);
        s.put("givenName", givenName);
        s.put("surname", surname);
        s.put("email", email);
        s.put("student", isStudent);
        if(isStudent) {
            s.put("deleted", false);
            s.put("major", major);
        }

        String uID = mAuth.getCurrentUser().getUid();
        StorageReference fileRef = storageRef.child("images/"+uID + file.getLastPathSegment());
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
                String URL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                s.put("photoUrl", URL);
                Log.d("Photo Uri", "could not handle Uri");
                FirebaseFirestore.getInstance().collection("StudentDetail").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
        });
    }

    public static String getCurrentUserId() {
        if (mAuth.getCurrentUser() == null)
            return null;
        return mAuth.getCurrentUser().getUid();
    }

    public static void getCurrentUser(Callback<User> callback) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Log.d("getUser", "No Logged In User");
            callback.onFailure(null);
            return;
        }
        DocumentReference docRef = db.collection("StudentDetail")
                .document(mAuth.getCurrentUser().getUid());
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
        if(mAuth.getCurrentUser()==null){
            Log.d("userDelete", "no user logged in");
            callback.onFailure(null);
            return;
        }
        DocumentReference docRef = db.collection("StudentDetail")
                .document(mAuth.getCurrentUser().getUid()); //get the current user document
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("profile", "full deleted");
                        FirebaseUser user = mAuth.getCurrentUser();
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
        if(mAuth.getCurrentUser()==null){
            Log.d("userDelete", "no user logged in");
            callback.onFailure(null);
            return;
        }
        String uID = mAuth.getCurrentUser().getUid();
        FirebaseUser user = mAuth.getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("userDelete", "User account deleted.");
                            DocumentReference docRef = db.collection("StudentDetail")
                                    .document(mAuth.getCurrentUser().getUid()); //get the current user document
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
        DocumentReference docRef = db.collection("StudentDetail")
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
        FirebaseUser user = mAuth.getCurrentUser();
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
        if(mAuth.getCurrentUser()==null){
            Log.d("changePhoto", "No Logged In User");
            callback.onFailure(null);
            return;
        }

        String uID = mAuth.getCurrentUser().getUid();
        StorageReference fileRef = storageRef.child("images/"+uID + file.getLastPathSegment());
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
                DocumentReference docRef = db.collection("StudentDetail")
                        .document(mAuth.getCurrentUser().getUid()); //get the current user document
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
        // TODO: replace this
        callback.onSuccess(new Building(id, id, "", 30));
    }

    public static void checkIn(String id, Callback<Void> callback){
        //TODO: replace this
    }

    public static void checkOut(String id, Callback<Void> callback){
        //TODO: replace this
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
                                     String buildingName, long studentId, String major,
                                     Callback<Record> callback) { // TODO: change to listener? technically a callback would suffice, though, b/c records are never removed/updated
        // TODO: replace this
        callback.onSuccess(new Record(buildingName, true));
        callback.onSuccess(new Record(buildingName, false));
        callback.onSuccess(new Record(buildingName, true));
    }
}
