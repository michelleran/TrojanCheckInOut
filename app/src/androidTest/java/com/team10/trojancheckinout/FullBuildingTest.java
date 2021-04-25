package com.team10.trojancheckinout;

import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_LONG_OP;
import static com.team10.trojancheckinout.TestUtils.getCurrentActivity;
import static com.team10.trojancheckinout.TestUtils.sleep;

@RunWith(AndroidJUnit4.class)
public class FullBuildingTest {
    private final String EMAIL = "meh@usc.edu";
    private final String PASSWORD = "1234567890";
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);
    @Test
    public void checkInFullBuilding() {
        // Login

        onView(withId(R.id.etEmail)).perform(typeText(EMAIL));
        onView(withId(R.id.etPassword)).perform(typeText(PASSWORD));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());
        sleep(WAIT_DATA);
        Log.d("FullTest","here");

        final String BUILDING = "j5s0x3WnH75qhbblaFu4";
        StudentActivity activity = (StudentActivity) getCurrentActivity();
        // mock scan to check in
        activity.didScanQR(BUILDING);
        sleep(WAIT_LONG_OP*3);
        onView(withText("Are you sure you want to check in?")).check(matches(isDisplayed()));
        onView(withId(R.id.btnLogin)).perform(click());
        //onView(withId(R.id.currentBuilding)).check(matches(withText("test")));

        // mock scan to check out
        //activity.didScanQR(BUILDING);
        //sleep(WAIT_LONG_OP);
        //onView(withId(R.id.currentBuilding)).check(matches(withText(R.string.none)));


        // Logout
        //onView(withId(R.id.signOutbtn)).perform(click());
    }
}
