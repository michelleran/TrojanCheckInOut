package com.team10.trojancheckinout;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_LONG_OP;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.getCurrentActivity;
import static com.team10.trojancheckinout.TestUtils.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class StudentActivityTest {
    private final String EMAIL = "student@usc.edu";
    private final String PASSWORD = "12345678";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
        new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void login() {
        // login
        onView(withId(R.id.etEmail)).perform(typeText(EMAIL));
        onView(withId(R.id.etPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        sleep(WAIT_DATA);
    }

    @After
    public void logout() {
        onView(withId(R.id.signOutbtn)).perform(click());
    }

    @Test
    public void openStudentHistory() {
        Intents.init();
        sleep(WAIT_UI);
        onView(withId(R.id.viewHistorybtn)).perform(click());
        intended(hasComponent(StudentHistory.class.getName()));
        sleep(WAIT_UI);
        //check if appropriate recycler view is displayed on following page
        onView(withId(R.id.student_history_list)).check(matches(isDisplayed()));
        Intents.release();
        Espresso.pressBack();
    }

    @Test
    public void editProfileImage() {
        Matcher<Intent> expectedIntent = AllOf.allOf(IntentMatchers.hasAction(Intent.ACTION_PICK),
                IntentMatchers.hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Intents.init();
        Intents.intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, getGalleryIntent()));
        sleep(WAIT_UI);
        onView(withId(R.id.editImagebtn)).perform(click());
        sleep(WAIT_UI);
        Intents.intended(expectedIntent);
        Intents.release();
        // wait for upload to complete
        sleep(WAIT_LONG_OP);
    }

    private Intent getGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setData(Uri.parse("android.resource://com.team10.trojancheckinout/" + R.drawable.default_profile_picture));
        return intent;
    }

    @Test
    public void mockScanQR_checkInThenOut() {
        final String BUILDING = "yiFJ8xf2VT4ge6aWXxCJ";
        StudentActivity activity = (StudentActivity) getCurrentActivity();
        // mock scan to check in
        activity.didScanQR(BUILDING);
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText("Mudd Hall")));
        // mock scan to check out
        activity.didScanQR(BUILDING);
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText(R.string.none)));
    }

    @Test
    public void mockScanQR_checkIn_checkOutViaButton() {
        StudentActivity activity = (StudentActivity) getCurrentActivity();
        // mock scan to check in
        activity.didScanQR("yiFJ8xf2VT4ge6aWXxCJ");
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText("Mudd Hall")));
        // check out via button
        onView(withId(R.id.checkOutbtn)).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText(R.string.none)));
    }

    @Test
    public void deleteAccount_cancelPopUp(){
        onView(withId(R.id.floatingActionButton2)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //cancel delete
        onView(withText(R.string.cancel)).perform(click());
    }

    @Test
    public void deleteAccount_confirmPopUp_reregister() {
        sleep(WAIT_UI);
        onView(withId(R.id.floatingActionButton2)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //confirm delete
        onView(withText(R.string.confirm)).perform(click());
        sleep(WAIT_UI);

        // re-register
        onView(withId(R.id.studentRegisterBtn)).perform(click());
        onView(withId(R.id.etSFname)).perform(typeText("Student"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etSLname)).perform(typeText("Testing"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etSEmail)).perform(typeText(EMAIL));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etSPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etUSCid)).perform(typeText("6998590265"));
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
    }

    //must be logged into account using EMAIL and PASSWORD
    @Test
    public void confirmRegistration(){

        //check contents of accounts matches previous test's registration
        onView(withId(R.id.givenName)).check(matches(withText("Student")));
        onView(withId(R.id.surname)).check(matches(withText("Testing")));
        onView(withId(R.id.id)).check(matches(withText("6998590265")));
        onView(withId(R.id.major)).check(matches(withText("CSCI")));

        //the below is to doubly make sure all registration fields match
        /*//logout
        onView(withId(R.id.signOutbtn)).perform(click());

        //login
        onView(withId(R.id.etEmail)).perform(typeText(EMAIL));
        onView(withId(R.id.etPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        sleep(WAIT_DATA);

        //check contents again
        onView(withId(R.id.givenName)).check(matches(withText("Student")));
        onView(withId(R.id.surname)).check(matches(withText("Testing")));
        onView(withId(R.id.id)).check(matches(withText("6998590265")));
        onView(withId(R.id.major)).check(matches(withText("CSCI")));*/

    }



    //Tests to put in Rithwik's section
    /*@Test
    public void deleteAccount(){
        onView(withId(R.id.btnDeleteProfile)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //cancel delete
        onView(withText(R.string.cancel)).perform(click());
    }

    @Test
    public void register_manager(){
        sleep(WAIT_UI);
        onView(withId(R.id.btnDeleteProfile)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //confirm delete
        onView(withText(R.string.confirm)).perform(click());
        sleep(WAIT_UI);

        //register the account again
        onView(withId(R.id.managerRegisterBtn)).perform(click());
        onView(withId(R.id.etMFname)).perform(typeText(userGivenName));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etMLname)).perform(typeText(userSurname));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etMEmail)).perform(typeText(userEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etMPassword)).perform(typeText(userPassword));
        Espresso.closeSoftKeyboard();

        // add photo
        Matcher<Intent> expectedIntent = IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT);
        Intents.init();
        Intents.intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, getGalleryIntent()));
        sleep(WAIT_UI);
        onView(withId(R.id.sAddPhoto)).perform(click());
        sleep(WAIT_UI);
        Intents.intended(expectedIntent);
        Intents.release();

        //register
        onView(withId(R.id.sRegBtn)).perform(click());
        sleep(WAIT_LONG_OP);
    }*/
}