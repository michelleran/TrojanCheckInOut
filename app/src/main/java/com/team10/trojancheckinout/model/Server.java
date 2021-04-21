package com.team10.trojancheckinout.model;

import android.content.Context;
import android.nfc.Tag;
import android.os.Environment;
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
import com.google.zxing.WriterException;
import com.team10.trojancheckinout.utils.QRCodeHelper;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;


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
        } //user already exists
        else {
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

    public static void registerManager(String givenName, String surname, String email,
                                       Uri file, String password, Callback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password) //also logs in the user
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //auth register
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("registerManager", "createUserWithEmail:success");
                            writeUserData("", givenName, surname, email, file, null, callback, false);
                        }
                        else {
                            Log.w("registerManager", "createUserWithEmail:failure", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public static void registerStudent(String id, String givenName, String surname, String email,
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
        String uid = auth.getCurrentUser().getUid();
        Map<String, Object> s = new HashMap<>();
        s.put("uid", uid);
        s.put("givenName", givenName);
        s.put("surname", surname);
        s.put("email", email);
        s.put("student", isStudent);
        if(isStudent) {
            s.put("id", id);
            s.put("deleted", false);
            s.put("major", major);
            s.put("currentBuilding", null);
        }

        StorageReference fileRef = storage.child("images/"+uid + file.getLastPathSegment());
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
                        FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(uid)
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

    public static void getCurrentUser(Callback<User> callback) {
        if(auth.getCurrentUser()==null){
            callback.onFailure(new Exception("No logged in user"));
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
                        if(document.getBoolean("student")) {
                            //can also check if the user is deleted by checking against "deleted"
                            Student student = document.toObject(Student.class);
                            callback.onSuccess(student);
                        }
                        else {
                            //make manager
                            callback.onSuccess(document.toObject(Manager.class));
                        }
                    } else {
                       Log.d("getUser", "No such document");
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
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();
        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("deleteStudent", "account deleted.");
                DocumentReference docRef = db.collection(USER_COLLECTION)
                    .document(uid); //get the current user document
                docRef.update("deleted", true) // TODO: this fails
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("deleteStudent", "profile set to deleted");
                            callback.onSuccess(null);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("deleteStudent", "profile not set", e);
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

    public static void getStudent(String uid, Callback<Student> callback) {
        DocumentReference docRef = db.collection(USER_COLLECTION)
                .document(uid);
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
                            callback.onSuccess(document.toObject(Student.class));
                        }
                        else { //manager
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

    public static void photoURLInput(String URL, Callback<String> callback) {
        if (auth.getCurrentUser() == null) {
            Log.d("changePhoto", "No Logged In User");
            callback.onFailure(null);
            return;
        }

        String uID = auth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection(USER_COLLECTION)
                .document(auth.getCurrentUser().getUid()); //get the current user document
        docRef.update("photoUrl", URL)
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

                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        DocumentReference docRef = db.collection(USER_COLLECTION)
                            .document(auth.getCurrentUser().getUid()); //get the current user document
                        docRef.update("photoUrl", url)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("changePhotoUrl", "DocumentSnapshot successfully updated!");
                                    callback.onSuccess(url);
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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

    public static void getBuildingIDByName (String buildingName, Callback<String> callback) {
        Query query = db.collection(BUILDING_COLLECTION)
                .whereEqualTo("name", buildingName);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot results = task.getResult();
                    if (results.getDocuments().size() == 1) {
                        callback.onSuccess(results.getDocuments().get(0).getId());
                    }
                    else {
                        callback.onSuccess(null);
                    }
                }
                else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }
                
    public static void getAllBuildingNames(Callback<String> callback) {
        db.collection(BUILDING_COLLECTION).get().addOnSuccessListener(result -> {
            for (DocumentSnapshot doc : result) {
                Building building = doc.toObject(Building.class);
                callback.onSuccess(building.getName());
            }
        }).addOnFailureListener(callback::onFailure);
    }

    public static void addBuilding(String name, int maxCapacity, Callback<Building> callback) {
        db.collection(BUILDING_COLLECTION)
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()) {
                                callback.onFailure(new Exception("Building with given name already exists"));
                            }else{
                                DocumentReference newBuildingRef = db.collection(BUILDING_COLLECTION).document();
                                String buildingID = newBuildingRef.getId();

                                byte[] qr = QRCodeHelper.generateQRCodeImage(buildingID, 250 , 250);
                                StorageReference fileRef = storage.child("qrcodes/" + buildingID);
                                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("contentType", "image/jpeg").build();
                                UploadTask uploadTask = fileRef.putBytes(qr, metadata);

                                uploadTask.addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        Log.d(TAG, "Upload is " + progress + "% done");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Log.d(TAG, "could not handle Uri");
                                        callback.onFailure(exception);

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(TaskSnapshot taskSnapshot) {
                                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Building building = new Building( newBuildingRef.getId(), name, uri.toString(),maxCapacity);
                                                db.collection(BUILDING_COLLECTION).document(buildingID).set(building)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot added with ID: ");
                                                                callback.onSuccess(building);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error adding document", e);
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

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
        final DocumentReference buildingDocRef = db.collection(BUILDING_COLLECTION).document(id);
        db.runTransaction(new Transaction.Function<Building>() {
            @Override
            public Building apply(Transaction transaction) throws FirebaseFirestoreException {
                Building building = transaction.get(buildingDocRef).toObject(Building.class);
                // If new max capacity is smaller than old max capacity
                if(building.getCurrentCapacity() > maxCapacity){
                    Log.d(TAG, "Error");
                    throw new FirebaseFirestoreException("New capacity is smaller than current capacity",
                        FirebaseFirestoreException.Code.ABORTED);
                }else{
                    transaction.update(buildingDocRef, "maxCapacity", maxCapacity);
                    building.setMaxCapacity(maxCapacity);
                }
                // Success
                return building;
            }
        }).addOnSuccessListener(new OnSuccessListener<Building>() {
            @Override
            public void onSuccess(Building building) {
                callback.onSuccess(building);
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    public static void listenForCheckedInStudents(String buildingId, Listener<Student> listener) {
        db.collection(USER_COLLECTION).whereEqualTo("currentBuilding", buildingId)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            listener.onAdd(dc.getDocument().toObject(Student.class));
                            Log.d(TAG, "Add: " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            listener.onUpdate(dc.getDocument().toObject(Student.class));
                            Log.d(TAG, "Update: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            listener.onRemove(dc.getDocument().toObject(Student.class));
                            Log.d(TAG, "Remove: " + dc.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }

    public static void checkIn(String buildingId, Callback<Building> callback) {
        checkInStudent(auth.getUid(), buildingId, callback);
    }

    @VisibleForTesting
    public static void checkInStudent(String studentId, String buildingId, Callback<Building> callback) {
        // Get building and student document references
        final DocumentReference newBuildingRef = db.collection(BUILDING_COLLECTION).document(buildingId);
        final DocumentReference studentRef = db.collection(USER_COLLECTION).document(studentId);
        db.runTransaction(new Transaction.Function<Building>() {
            @Override
            public Building apply(Transaction transaction) throws FirebaseFirestoreException {
                Building newBuilding = transaction.get(newBuildingRef).toObject(Building.class);
                Student student = transaction.get(studentRef).toObject(Student.class);

                if (newBuilding == null || student == null) {
                    throw new FirebaseFirestoreException("Failed to get building or student",
                        FirebaseFirestoreException.Code.ABORTED);
                }

                // Check if student is already checked in to building
                if(student.getCurrentBuilding() != null && student.getCurrentBuilding().equals(buildingId)) {
                    throw new FirebaseFirestoreException("Student is already checked into this building",
                        FirebaseFirestoreException.Code.ABORTED);
                }

                // Check if building is full
                if(newBuilding.getCurrentCapacity() == newBuilding.getMaxCapacity()) {
                    throw new FirebaseFirestoreException("Building is full",
                        FirebaseFirestoreException.Code.ABORTED);
                }

                // Check if student is already checked into a building
                if(student.getCurrentBuilding() == null) {
                    transaction.update(newBuildingRef, "currentCapacity", newBuilding.getCurrentCapacity() + 1);
                    transaction.update(studentRef, "currentBuilding", newBuilding.getId());
                    transaction.set(db.collection(RECORD_COLLECTION).document(), new Record(student, buildingId, newBuilding.getName(), true));
                }else{
                    final DocumentReference oldBuildingRef = db.collection(BUILDING_COLLECTION).document(student.getCurrentBuilding());
                    Building oldBuilding = transaction.get(oldBuildingRef).toObject(Building.class);
                    // Update each buildings capacity
                    transaction.update(newBuildingRef, "currentCapacity", newBuilding.getCurrentCapacity() + 1);
                    transaction.update(oldBuildingRef, "currentCapacity", oldBuilding.getCurrentCapacity() - 1);
                    // Update student's current building
                    transaction.update(studentRef, "currentBuilding", newBuilding.getId());

                    transaction.set(db.collection(RECORD_COLLECTION).document(), new Record(student, oldBuilding.getId(), oldBuilding.getName(), false));
                    transaction.set(db.collection(RECORD_COLLECTION).document(), new Record(student, buildingId, newBuilding.getName(), true));
                }
                newBuilding.setCurrentCapacity(newBuilding.getCurrentCapacity()+1);
                // Success
                return newBuilding;
            }
        }).addOnSuccessListener(new OnSuccessListener<Building>() {
            @Override
            public void onSuccess(Building building) {
                callback.onSuccess(building);
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
                callback.onFailure(e);
            }
        });
    }

    public static void checkOut(Callback<Building> callback) {
        checkOutStudent(auth.getUid(), callback);
    }

    @VisibleForTesting
    public static void checkOutStudent(String id, Callback<Building> callback) {
        final DocumentReference studentDocRef = db.collection(USER_COLLECTION).document(id);
        db.runTransaction(new Transaction.Function<Building>() {
            @Override
            public Building apply(@NotNull Transaction transaction) throws FirebaseFirestoreException {
                Student student = transaction.get(studentDocRef).toObject(Student.class);
                if (student == null) {
                    throw new FirebaseFirestoreException("Failed to get student",
                        FirebaseFirestoreException.Code.ABORTED);
                }

                if(student.getCurrentBuilding() == null){
                    throw new FirebaseFirestoreException("Student is not checked into a building",
                            FirebaseFirestoreException.Code.ABORTED);
                }

                final DocumentReference buildingDocRef = db.collection(BUILDING_COLLECTION).document(student.getCurrentBuilding());
                Building building = transaction.get(buildingDocRef).toObject(Building.class);
                if (building == null) {
                    throw new FirebaseFirestoreException("Failed to get building",
                        FirebaseFirestoreException.Code.ABORTED);
                }


                // Update database
                transaction.update(buildingDocRef, "currentCapacity", building.getCurrentCapacity() - 1);
                transaction.update(studentDocRef, "currentBuilding", null);
                transaction.set(db.collection(RECORD_COLLECTION).document(), new Record(student, building.getId(), building.getName(), false));

                building.setCurrentCapacity(building.getCurrentCapacity()-1);
                // Success
                return building;
            }
        }).addOnSuccessListener(new OnSuccessListener<Building>() {
            @Override
            public void onSuccess(Building building) {
                callback.onSuccess(building);
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }
    public static void listenToHistory(String id, Callback<Record> callback) {
        db.collection(RECORD_COLLECTION)
            .whereEqualTo("studentUid", id)
            .orderBy("epochTime", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    callback.onFailure(new Exception(error.getMessage()));
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Record record = dc.getDocument().toObject(Record.class);
                        callback.onSuccess(record);
                    }
                }
            });
    }


    private static Query queryRecords(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                      int endYear, int endMonth, int endDay, int endHour, int endMin,
                                      String buildingName, String studentId, String major)
    {
        Query query = db.collection(RECORD_COLLECTION);

        // Filter by building name
        if (!buildingName.isEmpty()) {
            query = query.whereEqualTo("buildingName", buildingName);
        }

        // Filter by student
        if(!studentId.isEmpty()){
            query = query.whereEqualTo("studentId", studentId);
        }

        // Filter by major
        if(!major.isEmpty()){
            query = query.whereEqualTo("major", major);
        }

        query = query.orderBy("epochTime", Query.Direction.DESCENDING);
        if (startYear != -1) {
            // startMonth . . . startMin must be valid too
            ZonedDateTime start = ZonedDateTime.of(startYear, startMonth, startDay, startHour, startMin, 0, 0, Record.pst);
            query = query.endAt(start.toEpochSecond()); // b/c query direction is descending, we "end" at the start date
        }

        if (endYear != -1) {
            // endMonth . . . endMin must be valid too
            ZonedDateTime end = ZonedDateTime.of(endYear, endMonth, endDay, endHour, endMin, 0, 0, Record.pst);
            query = query.startAt(end.toEpochSecond()); // b/c query direction is descending, we "start" at the end date
        }

        return query;
    }

    public static void filterRecords(int startYear, int startMonth, int startDay, int startHour, int startMin,
                                     int endYear, int endMonth, int endDay, int endHour, int endMin,
                                     String buildingName, String studentId, String major,
                                     Callback<Record> callback)
    {
        Query query = queryRecords(
            startYear, startMonth, startDay, startHour, startMin,
            endYear, endMonth, endDay, endHour, endMin,
            buildingName, studentId, major);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(new Exception(error.getMessage()));
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Record record = dc.getDocument().toObject(Record.class);
                        Log.d("filterRecords", "Found: " + record);
                        callback.onSuccess(record);
                    }
                }
            }
        });
    }

    public static void searchStudents(String name, String id, String major,
                                      String buildingName,
                                      int startYear, int startMonth, int startDay, int startHour, int startMin,
                                      int endYear, int endMonth, int endDay, int endHour, int endMin,
                                      Callback<Student> callback)
    {
        if (!buildingName.isEmpty()) {
            Query query = queryRecords(
                startYear, startMonth, startDay, startHour, startMin,
                endYear, endMonth, endDay, endHour, endMin,
                buildingName, "", major);
            query.get().addOnSuccessListener(result -> {
                // track which students we've already returned
                HashSet<String> students = new HashSet<>();
                for (DocumentSnapshot doc : result.getDocuments()) {
                    Record record = doc.toObject(Record.class);

                    if (!students.add(record.getStudentUid()))
                        // already returned this student
                        continue;

                    getStudent(record.getStudentUid(), new Callback<Student>() {
                        @Override
                        public void onSuccess(Student student) {
                            // search by name, if applicable
                            if ((name.isEmpty() ||
                                    student.getGivenName().toLowerCase().contains(name) ||
                                    student.getSurname().toLowerCase().contains(name)) &&
                                // search by id, if applicable
                                (id.isEmpty() || student.getId().equals(id)))
                                // student matches
                                callback.onSuccess(student);
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            callback.onFailure(exception);
                        }
                    });
                }
            }).addOnFailureListener(callback::onFailure);
        } else {
            Query query = db.collection(USER_COLLECTION)
                .whereEqualTo("student", true);
            if (!major.isEmpty())
                query = query.whereEqualTo("major", major);

            query.get().addOnSuccessListener(result -> {
                for (DocumentSnapshot doc : result.getDocuments()) {
                    Student student = doc.toObject(Student.class);
                    if ((name.isEmpty() ||
                        // search by name
                        student.getGivenName().toLowerCase().contains(name) ||
                        student.getSurname().toLowerCase().contains(name)) &&
                        // search by id, if applicable
                       (id.isEmpty() || student.getId().equals(id)))
                    {
                        // student matches
                        callback.onSuccess(student);
                    }
                }
            }).addOnFailureListener(callback::onFailure);
        }
    }
}
