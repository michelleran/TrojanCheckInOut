package com.team10.trojancheckinout;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_LONG_OP;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class RegistrationLoginTest {
    private final String SEMAIL = "studentR@usc.edu";
    private final String MEMAIL = "managerR@usc.edu";
    private final String PASSWORD = "12345678";
    private final String SFNAME = "Student";
    private final String MFNAME = "Manager";
    private final String LNAME = "Testman";
    private final String ID = "1234567890";

    @Rule
    public ActivityScenarioRule<StartPage> activityRule =
            new ActivityScenarioRule<>(StartPage.class);
    @Test
    public void registerStudent_confirmInput(){

        //register student
        onView(withId(R.id.studentRegisterBtn)).perform(click());
        onView(withId(R.id.etSFname)).perform(typeText(SFNAME));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etSLname)).perform(typeText(LNAME));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etSEmail)).perform(typeText(SEMAIL));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etSPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etUSCid)).perform(typeText(ID));
        Espresso.closeSoftKeyboard();

        // select major
        onView(withId(R.id.sMajors)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CSCI"))).perform(click());

        // add photo
        Matcher<Intent> expectedIntent = IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT);
        Intents.init();
        Intents.intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, getGalleryIntent()));
        sleep(WAIT_UI);
        onView(withId(R.id.sAddPhoto)).perform(click());
        sleep(WAIT_UI);
        Intents.intended(expectedIntent);
        Intents.release();

        onView(withId(R.id.sRegBtn)).perform(click());
        sleep(WAIT_LONG_OP);

        //confirm inputs
        onView(withId(R.id.givenName)).check(matches(withText(SFNAME)));
        onView(withId(R.id.surname)).check(matches(withText(LNAME)));
        onView(withId(R.id.id)).check(matches(withText(ID)));
        onView(withId(R.id.major)).check(matches(withText("CSCI")));

        //logout
        onView(withId(R.id.signOutbtn)).perform(click());

        //login
        onView(withId(R.id.etEmail)).perform(typeText(SEMAIL));
        onView(withId(R.id.etPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        sleep(WAIT_DATA);

        //delete account
        onView(withId(R.id.floatingActionButton2)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));
        onView(withText(R.string.confirm)).perform(click());
        sleep(WAIT_UI);
    }

    private Intent getGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setData(Uri.parse("android.resource://com.team10.trojancheckinout/" + R.drawable.default_profile_picture));
        return intent;
    }


    @Test
    public void registerManager_confirmInputs(){
        //register manager
        onView(withId(R.id.managerRegisterBtn)).perform(click());
        onView(withId(R.id.etMFname)).perform(typeText(MFNAME));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etMLname)).perform(typeText(LNAME));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etMEmail)).perform(typeText(MEMAIL));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etMPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();

        // add photo
        Matcher<Intent> expectedIntent = IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT);
        Intents.init();
        Intents.intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, getGalleryIntent()));
        sleep(WAIT_UI);
        onView(withId(R.id.mAddPhoto)).perform(click());
        sleep(WAIT_UI);
        Intents.intended(expectedIntent);
        Intents.release();

        onView(withId(R.id.mRegBtn)).perform(click());
        sleep(WAIT_LONG_OP);

        //confirm inputs
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.txtGivenName)).check(matches(withText("First Name: " + MFNAME)));
        onView(withId(R.id.txtSurname)).check(matches(withText("Surname: " + LNAME)));
        onView(withId(R.id.txtEmail)).check(matches(withText("Email: " + MEMAIL)));

        //logout
        onView(withId(R.id.btnLogout)).perform(click());

        //login
        onView(withId(R.id.startLoginbtn)).perform(click());
        onView(withId(R.id.etEmail)).perform(typeText(MEMAIL));
        onView(withId(R.id.etPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        sleep(WAIT_DATA);

        //delete account
        onView(withId(R.id.btnDeleteProfile)).perform(click());
        sleep(WAIT_UI);
        onView(withText("Are you sure you want to delete your account?\nNOTE: This action is permanent")).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
        sleep(WAIT_UI);

    }

}