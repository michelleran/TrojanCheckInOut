package com.team10.trojancheckinout;

import com.team10.trojancheckinout.model.Building;
import com.team10.trojancheckinout.model.Callback;
import com.team10.trojancheckinout.model.Server;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import static org.hamcrest.Matchers.allOf;

import static com.team10.trojancheckinout.TestUtils.*;

@RunWith(AndroidJUnit4.class)
public class BuildingsTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
        new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void login_navigateToBuildingsTab() {
        // login
        onView(withId(R.id.etEmail)).perform(typeText("ranmiche@usc.edu"));
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);

        // navigate to buildings tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_UI);
    }

    @After
    public void logout() {
        // navigate to profile tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);
        // log out
        onView(withId(R.id.btnLogout)).perform(click());
    }

    @Test
    public void openBuilding_verifyDetails() {
        final String BUILDING = "Mudd Hall";
        // select building
        onView(withId(R.id.building_list))
            .perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText(BUILDING)), click()));

        sleep(WAIT_UI);

        // check building name
        onView(withId(R.id.building_details_name))
            .check(matches(allOf(withText(BUILDING), isDisplayed())));

        RecyclerView list = getCurrentActivity().findViewById(R.id.building_details_students);
        int count = list.getAdapter().getItemCount();

        // assert that current capacity = length of list
        onView(withId(R.id.building_details_capacity))
            .check(matches(
                withText(startsWith(String.format(Locale.US, "Capacity: %d/", count)))
            ));

        for (int i = 0; i < count; i++) {
            // scroll to student
            onView(withId(R.id.building_details_students))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // open profile
            onView(withRecyclerView(R.id.building_details_students)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert that current building matches
            onView(withId(R.id.currentBuilding)).check(matches(withText(BUILDING)));
            Espresso.pressBack();
        }
        Espresso.pressBack();
    }

    /** Must be run alone to give the transactions time to finish. */
    @Test
    public void openBuilding_fakeCheckInThenOut() {
        final String BUILDING = "Mudd Hall";
        String STUDENT = "kZx50sTS79Wp8az6NHZbJjFU6AI2";
        String NAME = "Testing, Student";

        // select building
        onView(withId(R.id.building_list))
            .perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText(BUILDING)), click()));

        sleep(WAIT_UI);

        RecyclerView list = getCurrentActivity().findViewById(R.id.building_details_students);
        int count = list.getAdapter().getItemCount();

        // fake a check-in event
        Server.checkInStudent(STUDENT, "yiFJ8xf2VT4ge6aWXxCJ", new Callback<Building>() {
            @Override
            public void onSuccess(Building result) {
                sleep(WAIT_DATA);
                // this is a testing env, so we may assume there are no other users checking in/out
                assert list.getAdapter().getItemCount() == count + 1;
                onView(withId(R.id.building_details_capacity))
                    .check(matches(
                        withText(startsWith(String.format(Locale.US, "Capacity: %d/", list.getAdapter().getItemCount())))
                    ));

                // assert that student is in the list
                onView(withId(R.id.building_list))
                    .check(matches(hasDescendant(withText(NAME))));

                // fake a check-out event
                Server.checkOutStudent(STUDENT, new Callback<Building>() {
                    @Override
                    public void onSuccess(Building result) {
                        sleep(WAIT_DATA);
                        assert list.getAdapter().getItemCount() == count;
                        onView(withId(R.id.building_details_capacity))
                            .check(matches(
                                withText(startsWith(String.format(Locale.US, "Capacity: %d/", list.getAdapter().getItemCount())))
                            ));

                        // assert that student is not in the list
                        onView(withId(R.id.building_list))
                            .check(matches(not(hasDescendant(withText("Tester, Student")))));
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        exception.printStackTrace();
                        assert false;
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
                assert false;
            }
        });
    }
}