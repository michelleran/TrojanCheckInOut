package com.team10.trojancheckinout;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import static org.junit.Assert.*;
import com.team10.trojancheckinout.utils.Validator;

import java.util.ArrayList;

@RunWith(DataProviderRunner.class)
public class ValidatorTest {
    @DataProvider
    public static Object[][] mockInputs() {
        return new Object[][]{
                {"fname","lname","email","password",""},
                {"fname","lname","email","","ID"},
                {"fname","lname","","password","ID"},
                {"fname","","email","password","ID"},
                {"","lname","email","password","ID"},

                {"","","email","password","ID"},
                {"fname","","","password","ID"},
                {"fname","lname","","","ID"},
                {"fname","lname","email","",""},
                {"","","","password","ID"},
                {"fname","","","","ID"},
                {"fname","lname","","",""},
                {"","","","","ID"},
                {"fname","","","",""},
                {"","","","",""},

                {"fname","lname","email",""},
                {"fname","lname","","password"},
                {"fname","","email","password"},
                {"","lname","email","password"},
                {"","","email","password"},
                {"fname","","","password"},
                {"fname","lname","",""},
                {"fname","","",""},
                {"","","","password"},
                {"","","",""}
        };
    }
    @Test
    @UseDataProvider("mockInputs")
    public void strings_notEmpty(String... text){
        ArrayList<String> inputs = new ArrayList<String>();
        for (String t : text) {
            inputs.add(t);
        }

        if (inputs.size() == 5){
            assertFalse(Validator.validateNotEmpty(inputs.get(0),inputs.get(1),inputs.get(2),inputs.get(3),inputs.get(4)));
        }
        else{
            assertFalse(Validator.validateNotEmpty(inputs.get(0),inputs.get(1),inputs.get(2),inputs.get(3)));
        }
    }

    @DataProvider
    public static Object[][] validEmails() {
        return new Object[][]{
                {"winstoww@usc.edu"},
                {"winstoww7@usc.edu"},
                {"winstoww7w@usc.edu"},
        };
    }
    @Test
    @UseDataProvider("validEmails")
    public void check_validEmail(String email) {
        assertTrue(Validator.validateEmail(email));
    }

    @DataProvider
    public static Object[][] invalidEmails() {
        return new Object[][]{
                {"winstoww!@usc.edu"},
                {"winstoww!@@usc.edu"},
                {"winstoww!@#@usc.edu"},
                {"winstoww!@#$%@usc.edu"},
                {"@usc.edu"},
                {"user name@usc.edu"},
                {"1bigman@usc.edu"},
                {"b@usc.edu"},
                {"1@usc.edu"},
                {"1234567@usc.edu"},
                {"!winstoww@usc.edu"},
                {"bruin@ucla.edu"},
                {"winstoww@gmail.com"},
                {"winstoww7@gmail.com"},
                {"winstow7w@gmail.com"},
                {"micrsoftman@hotmail.com"},
                {"microsoftman2@outlook.com"}
        };
    }
    @Test
    @UseDataProvider("invalidEmails")
    public void check_invalidEmail(String email) {
        assertFalse(Validator.validateEmail(email));
    }

    @DataProvider
    public static Object[][] validPasswords() {
        return new Object[][]{
                {"overeight"},
                {"overeigh"},
        };
    }
    @Test
    @UseDataProvider("validPasswords")
    public void password_overEight(String password){
        assertTrue(Validator.validatePassword(password));
    }

    @DataProvider
    public static Object[][] invalidPasswords() {
        return new Object[][]{
                {"underei"},
                {""}
        };
    }
    @Test
    @UseDataProvider("invalidPasswords")
    public void password_underEight(String passwords){
        assertFalse(Validator.validatePassword(passwords));
    }

    @DataProvider
    public static Object[][] invalidIDs() {
        return new Object[][]{
                {""},
                {"12345"},
                {"123456"},
                {"1234567"},
                {"12345678"},
                {"123456789"},
                {"12345678901"},
                {"123456789012"},
                {"1234567890123"},
                {"12345678901234"},
                {"123456789012345"},
                {"12345678901234567890"}
        };
    }
    @Test
    @UseDataProvider("invalidIDs")
    public void ID_length_not_ten(String id){
        assertFalse(Validator.validateID(id));
    }

    @DataProvider
    public static Object[][] validIDs() {
        return new Object[][]{
                {"1234567890"},
                {"0987654321"},
                {"1111111111"}
        };
    }
    @Test
    @UseDataProvider("validIDs")
    public void ID_length_ten(String id){
        assertTrue(Validator.validateID(id));
    }

}