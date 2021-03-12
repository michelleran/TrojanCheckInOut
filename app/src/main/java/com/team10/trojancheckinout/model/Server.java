package com.team10.trojancheckinout.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;
import com.team10.trojancheckinout.LoginActivity;
import com.team10.trojancheckinout.StudentRegisterActivity;

import java.util.HashMap;
import java.util.Map;


public class Server {
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;

    // TODO: delete later
    private static final Student testStudent =
        new Student("0", "Test", "User", "test@usc.edu", "https://upload.wikimedia.org/wikipedia/commons/b/bb/Kittyply_edit1.jpg", "CSCI");

    public static void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // TODO: anything else
    }

    public static boolean isLoggedIn(){
        return FirebaseAuth.getInstance().getCurrentUser()!=null;
    }

    public static void loginUser(String email, String password, LoginActivity loginActivity, Object[] registerRun, Callback<User> callback){
       // initialize();
        LoginActivity la = new LoginActivity();
        //final boolean[] worked = {false};
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
             Log.d("login", "Already Signed In");
            //la.changeActivitySuccess(true);
            if(registerRun.length==0) { //simple login
                callback.onSuccess(getCurrentUser());
                //loginActivity.changeActivitySuccess(true);
            }
            else{//register
                addUser2(registerRun[0].toString(), registerRun[1].toString(), registerRun[2].toString(), registerRun[3].toString(),
                        registerRun[4].toString(), registerRun[5].toString(), (StudentRegisterActivity) registerRun[6], callback);
            }
            return;
           // return true;
            //FirebaseAuth.getInstance().signOut();
        } //user already exists
        
        else if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // log success
                                Log.d("login", "signInWithEmail:success");
                                if(registerRun.length==0) {
                                  //  loginActivity.changeActivitySuccess(true);
                                    callback.onSuccess(getCurrentUser());
                                }
                                else{
                                    addUser2(registerRun[0].toString(), registerRun[1].toString(), registerRun[2].toString(), registerRun[3].toString(),
                                            registerRun[4].toString(), registerRun[5].toString(), (StudentRegisterActivity) registerRun[6], callback);
                                }
                            }
                            else {
                                //log failure
                                Log.w("login", "signInWithEmail:failure", task.getException());
                                if(registerRun.length==0) {
                                   // loginActivity.changeActivitySuccess(false);
                                    callback.onFailure(task.getException());
                                }
                            }
                        }
                    });
        }
        //return FirebaseAuth.getInstance().getCurrentUser()!=null;
    }




    public static void logOutUser(){
        FirebaseAuth.getInstance().signOut();
        Log.w("log out", "log out attempt");
    }



    public static void studentRegister(String id, String givenName, String surname, String email,
                                   String photoUrl, String major, String password, StudentRegisterActivity sRActivity, Callback<User> callback) {
        //final boolean[] worked = {false};
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //auth register
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //worked[0] = true;
                            Log.d("registerStudent", "createUserWithEmail:success");
                            Object[] params = {id, givenName, surname, email, photoUrl, major, sRActivity};
                            loginUser(email, password, null, params, callback);
                            //addUser(id, givenName, surname, email, photoUrl, major, sRActivity);
                        }
                        else {
                            Log.w("registerStudent", "createUserWithEmail:failure", task.getException());
                            //worked[0] = false;
                        }
                    }
                });

        //return worked[0];
    }


    private static void addUser2(String id, String givenName, String surname, String email, String photoUrl, String major,
                          StudentRegisterActivity activity, Callback<User> callback) { //add user to the database
        Map<String, Object> s = new HashMap<>();
        s.put("id", id);
        s.put("givenName", givenName);
        s.put("surname", surname);
        s.put("email", email);
        s.put("photoUrl", photoUrl);
        s.put("major", major);
        s.put("deleted", false);
        s.put("student", true);
        // Student s = new Student(id, givenName, surname, email, photoUrl, major);
        FirebaseFirestore.getInstance().collection("StudentDetail").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(s)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("database", "DocumentSnapshot added with ID: ");
                       // activity.changeActivitySuccess(true);
                        callback.onSuccess(getCurrentUser());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("database", "Error adding document", e);
                        callback.onFailure(e);
                    }
                });

    }




    public static User getCurrentUser() {
        // TODO: replace this
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Log.d("getUser", "No Logged In User");
            return null;
        }
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("StudentDetail")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){ //success
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("getUser", "DocumentSnapshot data: " + document.getData());
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
                        }
                        else if(!student) { //manager
                            //make manager
                        }
                    } else {
                       Log.d("getUSer", "No such document");
                    }
                } else {
                    Log.d("getUser", "get failed with ", task.getException());
                }
            }
        });
        return testStudent;
    }

    public static void getStudent(String uID, Callback<Student> callback) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("StudentDetail")
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
                        }
                    } else {
                        Log.d("getStudent", "No such document");
                    }
                } else {
                    Log.d("getStudent", "get failed with ", task.getException());
                }
            }
        });
    }

    public static void changePassword(String newPassword){  //changes current user's password
        initialize();
        FirebaseUser user = mAuth.getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("changePassword", "User password updated.");
                            //do ui stuff
                        }
                    }
                });
    }

    public static void changePhotoUrl(String newURL){  //changes current user's password
        initialize();
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Log.d("changePhotoURL", "No Logged In User");
            return;
        }
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("StudentDetail")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()); //get the current user document
        docRef
                .update("PhotoUrl", newURL)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("changePhotoUrl", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("changePhotoUrl", "Error updating document", e);
                    }
                });
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
