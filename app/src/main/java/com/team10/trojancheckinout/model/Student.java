package com.team10.trojancheckinout.model;

public class Student {
    private int id;
    private String givenName;
    private String surname;
    private String email;
    private String photoUrl;
    private String major;
    private String currentBuilding;
    private boolean deleted;

    public Student(){}

    public Student(int id, String givenName, String surname, String email,
                   String photoUrl, String major) {
        this.id = id;
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.photoUrl = photoUrl;
        this.major = major;
    }

    public int getId() {
        return id;
    }

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

    public void setBuilding(String currentBuilding) {
        this.currentBuilding = currentBuilding;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}