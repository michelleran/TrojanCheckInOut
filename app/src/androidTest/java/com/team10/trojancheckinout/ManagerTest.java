package com.team10.trojancheckinout;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.TestOnly;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static com.team10.trojancheckinout.RecyclerViewMatcher.*;
import static com.team10.trojancheckinout.TestUtils.*;

@RunWith(AndroidJUnit4.class)
public class ManagerTest {
    private final int WAIT_DATA = 3000;
    private final int WAIT_UI = 1000;

    @Rule
    public ActivityTestRule<ManagerActivity> activityRule =
        new ActivityTestRule<>(ManagerActivity.class);

    @Test
    public void listBuildings_openDetails() {
        final String BUILDING = "Mudd Hall";
        // navigate to buildings tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        // select a building
        onView(withId(R.id.building_list))
            .perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText(BUILDING)), click()));
        // check building name
        onView(withId(R.id.building_details_name))
            .check(matches(withText(BUILDING)));

        // TODO: assert that current capacity, max capacity match (how to get values?)

        RecyclerView list = activityRule.getActivity().findViewById(R.id.building_details_students);
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // open profile of checked-in student
            onView(withRecyclerView(R.id.building_details_students)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert that current building matches
            onView(withId(R.id.currentBuilding)).check(matches(withText(BUILDING)));
        }
    }

    @Test
    public void filterBy_invalidId() {
        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));

        try {
            Thread.sleep(WAIT_UI);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // input too short USC id
        onView(withId(R.id.filter_student_id_field)).perform(typeText("12345"));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // assert that toast was shown
        onView(withText(R.string.filter_invalid_usc_id))
            .inRoot(withDecorView(not(is(activityRule.getActivity().getWindow().getDecorView()))))
            .check(matches(isDisplayed()));
    }

    @Test
    public void filterBy_building() {
        final String BUILDING = "Mudd Hall";
        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));

        try {
            Thread.sleep(WAIT_UI);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // input building name
        onView(withId(R.id.filter_building_field)).perform(typeText(BUILDING));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        try {
            Thread.sleep(WAIT_DATA);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RecyclerView list = activityRule.getActivity().findViewById(R.id.results_list);
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // check that record is for the right building
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_building_name))
                .check(matches(withText(BUILDING)));
        }
    }

    @Test
    public void filterBy_major() {
        // TODO
    }

    // TODO: Espresso can't test date/time picker?

    @NonNull
    private static ViewAction selectTabAtPosition(final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription() {
                return "with tab at index" + String.valueOf(position);
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view instanceof TabLayout) {
                    TabLayout tabLayout = (TabLayout) view;
                    TabLayout.Tab tab = tabLayout.getTabAt(position);

                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        };
    }
}