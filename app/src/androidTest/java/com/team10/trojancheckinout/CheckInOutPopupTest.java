package com.team10.trojancheckinout;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
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
import org.junit.runner.RunWith;
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

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_LONG_OP;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.getCurrentActivity;
import static com.team10.trojancheckinout.TestUtils.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class CheckInOutPopupTest {
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
    public void mockScanQR_checkInThenOut() throws Throwable {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        final String BUILDING = "yiFJ8xf2VT4ge6aWXxCJ";
        StudentActivity activity = (StudentActivity) getCurrentActivity();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                // mock scan to check in
                activity.didScanQR(BUILDING);
                sleep(WAIT_LONG_OP);
            }
        });

        onView(withText("Are you sure you want to check in?")).check(matches(isDisplayed()));
        onView(withText("Confirm")).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText("Mudd Hall")));

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                // mock scan to check out
                activity.didScanQR(BUILDING);
                sleep(WAIT_LONG_OP);
            }
        });

        sleep(WAIT_LONG_OP);
        onView(withText("Are you sure you want to check out?")).check(matches(isDisplayed()));
        onView(withText("Confirm")).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText("None")));
    }

    @Test
    public void mockScanQR_checkIn_checkOutViaButton() throws Throwable {
        StudentActivity activity = (StudentActivity) getCurrentActivity();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // mock scan to check in
                activity.didScanQR("yiFJ8xf2VT4ge6aWXxCJ");
                sleep(WAIT_LONG_OP);
            }
        });

        onView(withText("Are you sure you want to check in?")).check(matches(isDisplayed()));
        onView(withText("Confirm")).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText("Mudd Hall")));

        // check out via button
        onView(withId(R.id.checkOutbtn)).perform(click());
        sleep(WAIT_UI);
        onView(withText("Are you sure you want to check out?")).check(matches(isDisplayed()));
        onView(withText("Confirm")).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText(R.string.none)));
    }

    @Test
    public void mockScanQR_checkIn_while_checkIn() throws Throwable {
        StudentActivity activity = (StudentActivity) getCurrentActivity();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // mock scan to check in
                activity.didScanQR("yiFJ8xf2VT4ge6aWXxCJ");
                sleep(WAIT_LONG_OP);

            }
        });

        onView(withText("Are you sure you want to check in?")).check(matches(isDisplayed()));
        onView(withText("Confirm")).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText("Mudd Hall")));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // mock scan to check into a different building
                activity.didScanQR("FGhcVP2KLHA6VgA1ceFr");
                sleep(WAIT_LONG_OP);
            }
        });

        // check that check in failed
        sleep(WAIT_LONG_OP);
        onView(withText("You are currently checked into a different building. Please check out before checking into a new building.")).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText("Mudd Hall")));

        // check out via button
        onView(withId(R.id.checkOutbtn)).perform(click());
        sleep(WAIT_UI);
        onView(withText("Are you sure you want to check out?")).check(matches(isDisplayed()));
        onView(withText("Confirm")).perform(click());
        sleep(WAIT_LONG_OP);
        onView(withId(R.id.currentBuilding)).check(matches(withText(R.string.none)));
    }


}
