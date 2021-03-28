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
import org.junit.runners.MethodSorters;

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
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.sleep;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
        new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void login() {
        // login
        onView(withId(R.id.etEmail)).perform(typeText("student@usc.edu"));
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        sleep(WAIT_DATA);
    }

    @After
    public void logout() {
        onView(withId(R.id.signOutbtn)).perform(click());
    }

    @Test
    public void t1_OpenStudentHistory() {
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
    public void t2_EditProfileImage() {
        Matcher<Intent> expectedIntent = AllOf.allOf(IntentMatchers.hasAction(Intent.ACTION_PICK),
                IntentMatchers.hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        Intents.init();
        Intents.intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, getGalleryIntent()));
        sleep(WAIT_UI);
        onView(withId(R.id.editImagebtn)).perform(click());
        sleep(WAIT_UI);
        Intents.intended(expectedIntent);
        Intents.release();
    }

    private Intent getGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setData(Uri.parse("android.resource://com.team10.trojancheckinout/" + R.drawable.default_profile_picture));
        return intent;
    }

    @Test
    public void t3_CheckOut() {
        sleep(WAIT_UI);
        onView(withId(R.id.checkOutbtn)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.currentBuilding)).check(matches(withText(R.string.none)));
    }

    @Test
    public void t4_CancelDeleteAccountPopUp(){
        sleep(3000);
        onView(withId(R.id.floatingActionButton2)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //cancel delete
        onView(withText(R.string.cancel)).perform(click());
    }

    @Ignore("Execute this test individually due to its destructive nature. Log in as a student beforehand.")
    public void t5_ConfirmDeleteAccountPopUp(){
        sleep(WAIT_UI);
        onView(withId(R.id.floatingActionButton2)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //confirm delete
        onView(withText(R.string.confirm)).perform(click());
        sleep(WAIT_UI);
        //check if returned to start page
        onView(withId(R.id.studentRegisterBtn)).check(matches(isDisplayed()));
    }
}