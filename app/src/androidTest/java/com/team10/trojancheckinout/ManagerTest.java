package com.team10.trojancheckinout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;
import static org.hamcrest.Matchers.startsWith;

import static org.hamcrest.Matchers.allOf;

import static com.team10.trojancheckinout.TestUtils.*;

/** Must be logged in as a manager. */
@RunWith(AndroidJUnit4.class)
public class ManagerTest {
    @Rule
    public ActivityTestRule<ManagerActivity> activityRule =
        new ActivityTestRule<>(ManagerActivity.class);

    @Test
    public void listBuildings_openDetails() { // TODO: only works when run alone?
        final String BUILDING = "Mudd Hall";
        // navigate to buildings tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        // select a building
        onView(withId(R.id.building_list))
            .perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText(BUILDING)), click()));

        sleep(WAIT_UI);

        // check building name
        onView(withId(R.id.building_details_name))
            .check(matches(allOf(withText(BUILDING), isDisplayed())));

        RecyclerView list = activityRule.getActivity().findViewById(R.id.building_details_students);
        int count = list.getAdapter().getItemCount();

        // assert that current capacity = length of list
        onView(withId(R.id.building_details_capacity))
            .check(matches(
                withText(startsWith(String.format(Locale.US, "Capacity: %d/", count)))
            ));

        for (int i = 0; i < count; i++) {
            // open profile of checked-in student
            onView(withRecyclerView(R.id.building_details_students)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert that current building matches
            onView(withId(R.id.currentBuilding)).check(matches(withText(BUILDING)));
        }
    }
}