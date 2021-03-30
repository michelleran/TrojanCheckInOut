package com.team10.trojancheckinout;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Manager;
import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;
import com.team10.trojancheckinout.model.Student;
import com.team10.trojancheckinout.model.User;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;


import static com.team10.trojancheckinout.TestUtils.*;


@RunWith(AndroidJUnit4.class)
public class UsersServerTest {
    private final String SEMAIL = "student@usc.edu";
    private final String MEMAIL = "ranmiche@usc.edu";
    private final String BADEMAIL = "bademail911@usc.edu";
    private final String PASSWORD = "12345678";
    private final String BADPASSWORD = "12345678910";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void logout() {
        Server.logout();
    }

    /*@Test // redundant with ManagerProfileTest.checkProfileInformation()
    public void loginManager() {
        // login
        onView(withId(R.id.etEmail)).perform(typeText(MEMAIL));
        onView(withId(R.id.etEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(PASSWORD));
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);

        onView(withId(R.id.txtGivenName)).check(matches(withText("First Name: Michelle")));
        onView(withId(R.id.txtSurname)).check(matches(withText("Surname: Ran")));
        onView(withId(R.id.txtEmail)).check(matches(withText("Email: " + MEMAIL)));

        onView(withId(R.id.btnLogout)).perform(click());
    }*/

    @Test
    public void loginStudent() {
        onView(withId(R.id.etEmail)).perform(typeText(SEMAIL));
        onView(withId(R.id.etEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);

        onView(withId(R.id.givenName)).check(matches(withText("Student")));
        onView(withId(R.id.surname)).check(matches(withText("Testing")));
        onView(withId(R.id.id)).check(matches(withText("6998590265")));
        onView(withId(R.id.major)).check(matches(withText("CSCI")));
        onView(withId(R.id.currentBuilding)).check(matches(withText("None")));

        onView(withId(R.id.signOutbtn)).perform(click());
    }

    @Test
    public void BadLoginPassword() {
        onView(withId(R.id.etEmail)).perform(typeText(SEMAIL));
        onView(withId(R.id.etEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(BADPASSWORD));
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);

        Callback<User> callback = new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                assert false;
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception != null) {
                    Log.e("StartPage", exception.getMessage());
                    assert true;
                   // assert (exception instanceof FirebaseAuthInvalidCredentialsException); //invalid password error
                }
                else {
                    assert false;
                }
            }
        };
        Server.getCurrentUser(callback);
    }

    @Test
    public void BadLoginEmail() {
        Server.logout();
        sleep(WAIT_DATA);

        onView(withId(R.id.etEmail)).perform(typeText(BADEMAIL));
        onView(withId(R.id.etEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(PASSWORD));
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);

        Callback<User> callback = new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                assert false;
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception != null) {
                    Log.e("StartPage", exception.getMessage());
                    //assert (exception instanceof FirebaseAuthInvalidUserException); //invalid email error
                    assert true;
                }
                else {
                    assert false;
                }
            }
        };
        Server.getCurrentUser(callback);
    }

    @Test
    public void LogOutTest() {
        Server.logout(); //just in case

        Callback<User> callback = new Callback<User>() {
            @Override
            public void onSuccess(User result) {
                assert false;
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception != null) {
                    Log.e("StartPage", exception.getMessage());
                    assert (exception.getMessage() == "No logged in user"); //invalid email error
                }
                else {
                    assert false;
                }
            }
        };

        Server.getCurrentUser(callback);
    }


    @Test
    public void getStudentTest() {
        Callback<Student> callback = new Callback<Student>() {
            @Override
            public void onSuccess(Student result) {
                assert true;
            }

            @Override
            public void onFailure(Exception exception) {

                Log.e("StartPage", exception.getMessage());
                assert false; //no student
            }
        };
        Server.getStudent("OPnUgR1qHKPO7VKtgEipOFIkS5H3", callback);

    }

    //change Password
    @Test
    public void changePasswordTest() {
        onView(withId(R.id.etEmail)).perform(typeText(SEMAIL));
        onView(withId(R.id.etEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);

        onView(withId(R.id.changePasswordbtn)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.edtNewPassword)).perform(typeText("123456789")); //edit
        onView(withText(R.string.confirm)).inRoot(isDialog()).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.signOutbtn)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.etEmail)).perform(typeText(SEMAIL));
        onView(withId(R.id.etEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("123456789")); //edit
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        sleep(WAIT_DATA);


        onView(withId(R.id.changePasswordbtn)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.edtNewPassword)).perform(typeText("12345678")); //edit
        onView(withText(R.string.confirm)).inRoot(isDialog()).perform(click());


        onView(withId(R.id.givenName)).check(matches(withText("Student")));
        onView(withId(R.id.surname)).check(matches(withText("Testing")));
        onView(withId(R.id.id)).check(matches(withText("6998590265")));
        onView(withId(R.id.major)).check(matches(withText("CSCI")));
        onView(withId(R.id.currentBuilding)).check(matches(withText("None")));

        onView(withId(R.id.signOutbtn)).perform(click());
    }
}
