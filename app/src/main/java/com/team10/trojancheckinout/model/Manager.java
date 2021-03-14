package com.team10.trojancheckinout.model;

public class Manager implements User {
    private String id;
    private String givenName;
    private String surname;
    private String email;
    private String photoUrl;

    public Manager() {}

    public Manager(String id, String givenName, String surname, String email, String photoUrl) {
        this.id = id;
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
        this.photoUrl = photoUrl;

        //TODO: get rid of this (for testing purposes only)
        this.photoUrl = "https://www.clipartkey.com/mpngs/m/238-2383342_transparent-pizza-clip-art-transparent-cartoons-whole-pizza.png";
    }

    @Override
    public String getIdString() {
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

    void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
