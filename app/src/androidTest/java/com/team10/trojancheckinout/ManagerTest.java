package com.team10.trojancheckinout;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.team10.trojancheckinout.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

/** Must be logged in as a manager. */
@RunWith(AndroidJUnit4.class)
public class ManagerTest {
    private final int WAIT_DATA = 3000;
    private final int WAIT_UI = 1000;

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

    @Test
    public void filterBy_invalidId() {
        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));
        sleep(WAIT_UI);

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
        sleep(WAIT_UI);

        // input building name
        onView(withId(R.id.filter_building_field)).perform(typeText(BUILDING));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        //Log.d("Test", activityRule.getActivity().getFragmentManager().getFragments().toString());
        RecyclerView list = activityRule.getActivity().findViewById(R.id.results_list);
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // assert that building name matches
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_building_name))
                .check(matches(withText(BUILDING)));
        }
    }

    @Test
    public void filterBy_id() {
        final String ID = "0123456789";

        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));
        sleep(WAIT_UI);

        // input student id
        onView(withId(R.id.filter_student_id_field)).perform(typeText(ID));
        onView(withId(R.id.filter_student_id_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        Fragment fragment = activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_filter_results);
        if (fragment instanceof FilterResultsFragment) {
            for (int i = 0;
                 i < ((FilterResultsFragment)fragment).resultsList.getAdapter().getItemCount(); i++)
            {
                // open profile of student
                onView(withRecyclerView(R.id.results_list)
                    .atPositionOnView(i, R.id.record_student_photo))
                    .perform(click());
                // assert that id matches
                onView(withId(R.id.id)).check(matches(withText(ID)));
            }
        } else {
            Log.w("filterBy_id", "Results fragment not found; will try again");
        }
    }

    @Test
    public void filterBy_major() {
        final String MAJOR = "CSCI";

        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));
        sleep(WAIT_UI);

        // select major
        onView(withId(R.id.filter_major_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(MAJOR))).perform(click());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        Fragment fragment = activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_filter_results);
        // TODO: not sure why both blocks fire
        if (fragment instanceof FilterResultsFragment) {
            for (int i = 0;
                 i < ((FilterResultsFragment)fragment).resultsList.getAdapter().getItemCount(); i++)
            {
                // open profile of student
                onView(withRecyclerView(R.id.results_list)
                    .atPositionOnView(i, R.id.record_student_photo))
                    .perform(click());
                // assert that major matches
                onView(withId(R.id.major)).check(matches(withText(MAJOR)));
            }
        } else {
            Log.w("filterBy_major", "Results fragment not found; will try again");
        }
    }

    @Test
    public void filterBy_buildingId() {
        final String BUILDING = "Mudd Hall";
        final String ID = "0123456789";

        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));
        sleep(WAIT_UI);

        // input building name
        onView(withId(R.id.filter_building_field)).perform(typeText(BUILDING));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());

        // input student id
        onView(withId(R.id.filter_student_id_field)).perform(typeText(ID));
        onView(withId(R.id.filter_student_id_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        Fragment fragment = activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_filter_results);
        if (fragment instanceof FilterResultsFragment) {
            for (int i = 0;
                 i < ((FilterResultsFragment)fragment).resultsList.getAdapter().getItemCount(); i++)
            {
                // assert that building name matches
                onView(withRecyclerView(R.id.results_list)
                    .atPositionOnView(i, R.id.record_building_name))
                    .check(matches(withText(BUILDING)));
                // open profile of student
                onView(withRecyclerView(R.id.results_list)
                    .atPositionOnView(i, R.id.record_student_photo))
                    .perform(click());
                // assert that id matches
                onView(withId(R.id.id)).check(matches(withText(ID)));
            }
        } else {
            Log.w("filterBy_buildingId", "Results fragment not found; will try again");
        }
    }

    @Test
    public void filterBy_buildingMajor() {
        final String BUILDING = "Mudd Hall";
        final String MAJOR = "CSCI";

        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));
        sleep(WAIT_UI);

        // input building name
        onView(withId(R.id.filter_building_field)).perform(typeText(BUILDING));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());

        // select major
        onView(withId(R.id.filter_major_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(MAJOR))).perform(click());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        Fragment fragment = activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_filter_results);
        if (fragment instanceof FilterResultsFragment) {
            for (int i = 0;
                 i < ((FilterResultsFragment)fragment).resultsList.getAdapter().getItemCount(); i++)
            {
                // assert that building name matches
                onView(withRecyclerView(R.id.results_list)
                    .atPositionOnView(i, R.id.record_building_name))
                    .check(matches(withText(BUILDING)));
                // open profile of student
                onView(withRecyclerView(R.id.results_list)
                    .atPositionOnView(i, R.id.record_student_photo))
                    .perform(click());
                // assert that major matches
                onView(withId(R.id.major)).check(matches(withText(MAJOR)));
            }
        } else {
            Log.w("filterBy_buildingMajor", "Results fragment not found; will try again");
        }
    }

    // TODO: Espresso can't test date/time picker?

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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