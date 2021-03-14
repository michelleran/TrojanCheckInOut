package com.team10.trojancheckinout.utils;

public class Validator {
    public static boolean validateNotEmpty(String... text){
        for (String t : text) {
            if (t.trim().isEmpty())
                return false;
        }
        return true;
    }

    public static boolean validateEmail(String email){
        String emailPattern = "^[a-zA-Z]+[a-zA-Z0-9]+@usc\\.edu$"; // TODO: allow numeric characters, as long as it's not the first
        if(email.matches(emailPattern) && email.length() > 0){
            return true;
        }
        return false;
    }

    public static boolean validatePassword(String password){
        if(password.length() < 8){
            return false;
        }
        return true;
    }

    public static boolean validateID(String ID){
        if(ID.length() != 10){
            return false;
        }
        return true;
    }
}
