package com.team10.trojancheckinout.model;

public class Manager implements User {
    private String uid;
    private String givenName;
    private String surname;
    private String email;
    private String photoUrl;

    public Manager() {}

    @Override
    public String getUid() {
        return uid;
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

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}