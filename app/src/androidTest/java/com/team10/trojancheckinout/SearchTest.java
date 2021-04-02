package com.team10.trojancheckinout;

import android.util.Log;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.getCurrentActivity;
import static com.team10.trojancheckinout.TestUtils.withRecyclerView;
import static com.team10.trojancheckinout.TestUtils.selectTabAtPosition;
import static com.team10.trojancheckinout.TestUtils.selectInSpinner;
import static com.team10.trojancheckinout.TestUtils.sleep;

@RunWith(AndroidJUnit4.class)
public class SearchTest {
    private final String MAJOR = "CSCI";
    private final String BUILDING = "Mudd Hall";

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

    @After
    public void logout() {
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);
        onView(withId(R.id.btnLogout)).perform(click());
    }

    @Test
    public void searchByNamePartialMatch() {
        final String INPUT = "t";
        onView(withId(R.id.search_name)).perform(typeText(INPUT));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.search_button)).perform(click());

        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        Log.d("SearchTest", String.valueOf(list.getAdapter().getItemCount()));
        for (int i = 0; i < Math.min(7, list.getAdapter().getItemCount()); i++) {
            // scroll to student
            onView(withId(R.id.results_list))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // assert that student's name contains input
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_name))
                .check(matches(withText(containsString(INPUT))));
            // open profile
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert again that student's name contains input
            try {
                onView(withId(R.id.givenName)).check(matches(withText(containsString(INPUT))));
            } catch (AssertionFailedError e) {
                onView(withId(R.id.surname)).check(matches(withText(containsString(INPUT))));
            }
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByNameFullMatch() {
        final String INPUT = "Student";
        onView(withId(R.id.search_name)).perform(typeText(INPUT));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.search_button)).perform(click());

        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        for (int i = 0; i < Math.min(7, list.getAdapter().getItemCount()); i++) {
            // scroll to student
            onView(withId(R.id.results_list))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // assert that student's name contains input
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_name))
                .check(matches(withText(containsString(INPUT))));
            // open profile
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert again that student's name contains input
            try {
                onView(withId(R.id.givenName)).check(matches(withText(containsString(INPUT))));
            } catch (AssertionFailedError e) {
                onView(withId(R.id.surname)).check(matches(withText(containsString(INPUT))));
            }
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByMajor() {
        selectInSpinner(R.id.search_major_spinner, MAJOR);
        onView(withId(R.id.search_button)).perform(click());

        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        for (int i = 0; i < Math.min(7, list.getAdapter().getItemCount()); i++) {
            // scroll to record
            onView(withId(R.id.results_list))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // open profile
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert that major matches
            onView(withId(R.id.major)).check(matches(withText(MAJOR)));
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByBuilding() {
        selectInSpinner(R.id.search_building_spinner, BUILDING);
        onView(withId(R.id.search_button)).perform(click());

        // TODO: blocked by view history
    }

    @Test
    public void searchByBuildingStartDate() {
        // TODO: blocked by view history
    }

    @Test
    public void searchByBuildingStartEndDate() {
        // TODO: blocked by view history
    }

    @Test
    public void searchByNameMajor() {
        final String INPUT = "Student";
        onView(withId(R.id.search_name)).perform(typeText(INPUT));
        Espresso.closeSoftKeyboard();
        selectInSpinner(R.id.search_major_spinner, MAJOR);
        onView(withId(R.id.search_button)).perform(click());

        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        for (int i = 0; i < Math.min(7, list.getAdapter().getItemCount()); i++) {
            // scroll to record
            onView(withId(R.id.results_list))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // assert that student's name contains input
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_name))
                .check(matches(withText(containsString(INPUT))));
            // open profile
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert again that student's name contains input
            try {
                onView(withId(R.id.givenName)).check(matches(withText(containsString(INPUT))));
            } catch (AssertionFailedError e) {
                onView(withId(R.id.surname)).check(matches(withText(containsString(INPUT))));
            }
            // assert that major matches
            onView(withId(R.id.major)).check(matches(withText(MAJOR)));
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByNameMajorBuilding() {
        // TODO: blocked by view history
    }

    @Test
    public void searchByNameMajorBuildingStartEndDate() {
        // TODO: blocked by view history
    }
}
