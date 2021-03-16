package com.team10.trojancheckinout.model;

public class Student implements User {
    private String uid;
    private String id;
    private String givenName;
    private String surname;
    private String email;
    private String photoUrl;
    private String major;
    private String currentBuilding;
    private boolean deleted;

    public Student(){}

    public String getId() { return id; }

    @Override
    public String getUid() { return uid; }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getMajor() {
        return major;
    }

    public String getCurrentBuilding() {
        return currentBuilding;
    }

    public boolean isDeleted() {
        return deleted;
    }

    void setPhotoUrl(String url) { this.photoUrl = url; }
    void setBuilding(String currentBuilding) {
        this.currentBuilding = currentBuilding;
    }
    void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}