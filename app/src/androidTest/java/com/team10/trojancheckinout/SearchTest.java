package com.team10.trojancheckinout;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.selectTabAtPosition;
import static com.team10.trojancheckinout.TestUtils.sleep;

@RunWith(AndroidJUnit4.class)
public class SearchTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
        new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void login() {
        onView(withId(R.id.etEmail)).perform(typeText("ranmiche@usc.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));
        sleep(WAIT_UI);
    }

    // TODO

    @After
    public void logout() {
        onView(withId(R.id.btnLogout)).perform(click());
    }
}
