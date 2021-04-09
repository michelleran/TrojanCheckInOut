package com.team10.trojancheckinout;

import android.util.Log;

import com.team10.trojancheckinout.model.Record;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.TimeZone;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.team10.trojancheckinout.TestUtils.selectDateTime;
import static org.hamcrest.Matchers.not;
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
    private final String BUILDING = "Doheny";

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
        final String INPUT = "n";
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

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
        final String INPUT = "Tester";
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

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
            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));
            // assert that major matches
            onView(withId(R.id.major)).check(matches(withText(MAJOR)));
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByBuilding() {
        selectInSpinner(R.id.search_building_spinner, BUILDING);
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

            // view history
            onView(withId(R.id.viewHistoryBtn_basic)).perform(click());
            // record for this building may not be visible, so we'll directly check the adapter
            RecordAdapter adapter = (RecordAdapter)
                ((RecyclerView) getCurrentActivity().findViewById(R.id.student_history_list)).getAdapter();
            // assert that building is in history
            boolean buildingInHistory = false;
            for (Record record : adapter.records) {
                if (record.getBuildingName().equals(BUILDING)) {
                    buildingInHistory = true;
                    break;
                }
            }
            assert buildingInHistory;
            Espresso.pressBack();
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByBuildingStartDate() {
        Calendar cal = getStartDate();
        long startEpochTime = cal.toInstant().getEpochSecond();
        onView(withId(R.id.search_start_date)).perform(click());
        selectDateTime(cal);

        selectInSpinner(R.id.search_building_spinner, BUILDING);
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

            // view history
            onView(withId(R.id.viewHistoryBtn_basic)).perform(click());
            // record for this building may not be visible, so we'll directly check the adapter
            RecordAdapter adapter = (RecordAdapter)
                ((RecyclerView) getCurrentActivity().findViewById(R.id.student_history_list)).getAdapter();

            boolean visitedBuildingInTimePeriod = false;
            for (Record record : adapter.records) {
                if (record.getBuildingName().equals(BUILDING) &&
                    record.getEpochTime() >= startEpochTime) {
                    visitedBuildingInTimePeriod = true;
                    break;
                }
            }
            assert visitedBuildingInTimePeriod;
            Espresso.pressBack();
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByBuildingEndDate() {
        Calendar cal = getEndDate();
        long endEpochTime = cal.toInstant().getEpochSecond();
        onView(withId(R.id.search_end_date)).perform(click());
        selectDateTime(cal);

        selectInSpinner(R.id.search_building_spinner, BUILDING);
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

            // view history
            onView(withId(R.id.viewHistoryBtn_basic)).perform(click());
            // record for this building may not be visible, so we'll directly check the adapter
            RecordAdapter adapter = (RecordAdapter)
                ((RecyclerView) getCurrentActivity().findViewById(R.id.student_history_list)).getAdapter();

            boolean visitedBuildingInTimePeriod = false;
            for (Record record : adapter.records) {
                if (record.getBuildingName().equals(BUILDING) &&
                    record.getEpochTime() <= endEpochTime) {
                    visitedBuildingInTimePeriod = true;
                    break;
                }
            }
            assert visitedBuildingInTimePeriod;
            Espresso.pressBack();
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByBuildingStartEndDate() {
        Calendar cal = getStartDate();
        long startEpochTime = cal.toInstant().getEpochSecond();
        onView(withId(R.id.search_start_date)).perform(click());
        selectDateTime(cal);

        cal = getEndDate();
        long endEpochTime = cal.toInstant().getEpochSecond();
        onView(withId(R.id.search_end_date)).perform(click());
        selectDateTime(cal);

        selectInSpinner(R.id.search_building_spinner, BUILDING);
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

            // view history
            onView(withId(R.id.viewHistoryBtn_basic)).perform(click());
            // record for this building may not be visible, so we'll directly check the adapter
            RecordAdapter adapter = (RecordAdapter)
                ((RecyclerView) getCurrentActivity().findViewById(R.id.student_history_list)).getAdapter();

            boolean visitedBuildingInTimePeriod = false;
            for (Record record : adapter.records) {
                if (record.getBuildingName().equals(BUILDING) &&
                    record.getEpochTime() >= startEpochTime && record.getEpochTime() <= endEpochTime) {
                    visitedBuildingInTimePeriod = true;
                    break;
                }
            }
            assert visitedBuildingInTimePeriod;
            Espresso.pressBack();
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByNameMajor() {
        // input name
        final String INPUT = "n";
        onView(withId(R.id.search_name)).perform(typeText(INPUT));
        Espresso.closeSoftKeyboard();

        // select major
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

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
        // input name
        final String INPUT = "n";
        onView(withId(R.id.search_name)).perform(typeText(INPUT));
        Espresso.closeSoftKeyboard();

        // select major
        selectInSpinner(R.id.search_major_spinner, MAJOR);

        // select building
        selectInSpinner(R.id.search_building_spinner, BUILDING);
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

            // assert again that student's name contains input
            try {
                onView(withId(R.id.givenName)).check(matches(withText(containsString(INPUT))));
            } catch (AssertionFailedError e) {
                onView(withId(R.id.surname)).check(matches(withText(containsString(INPUT))));
            }
            // assert that major matches
            onView(withId(R.id.major)).check(matches(withText(MAJOR)));

            // view history
            onView(withId(R.id.viewHistoryBtn_basic)).perform(click());
            // record for this building may not be visible, so we'll directly check the adapter
            RecordAdapter adapter = (RecordAdapter)
                ((RecyclerView) getCurrentActivity().findViewById(R.id.student_history_list)).getAdapter();
            // assert that building is in history
            boolean buildingInHistory = false;
            for (Record record : adapter.records) {
                if (record.getBuildingName().equals(BUILDING)) {
                    buildingInHistory = true;
                    break;
                }
            }
            assert buildingInHistory;
            Espresso.pressBack();
            Espresso.pressBack();
        }
    }

    @Test
    public void searchByNameMajorBuildingStartEndDate() {
        // input name
        final String INPUT = "n";
        onView(withId(R.id.search_name)).perform(typeText(INPUT));
        Espresso.closeSoftKeyboard();

        // select major
        selectInSpinner(R.id.search_major_spinner, MAJOR);

        Calendar cal = getStartDate();
        long startEpochTime = cal.toInstant().getEpochSecond();
        onView(withId(R.id.search_start_date)).perform(click());
        selectDateTime(cal);

        cal = getEndDate();
        long endEpochTime = cal.toInstant().getEpochSecond();
        onView(withId(R.id.search_end_date)).perform(click());
        selectDateTime(cal);

        selectInSpinner(R.id.search_building_spinner, BUILDING);
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

            // assert student is not deleted
            onView(withId(R.id.deletedAccount)).check(matches(not(isDisplayed())));

            // assert again that student's name contains input
            try {
                onView(withId(R.id.givenName)).check(matches(withText(containsString(INPUT))));
            } catch (AssertionFailedError e) {
                onView(withId(R.id.surname)).check(matches(withText(containsString(INPUT))));
            }
            // assert that major matches
            onView(withId(R.id.major)).check(matches(withText(MAJOR)));

            // view history
            onView(withId(R.id.viewHistoryBtn_basic)).perform(click());
            // record for this building may not be visible, so we'll directly check the adapter
            RecordAdapter adapter = (RecordAdapter)
                ((RecyclerView) getCurrentActivity().findViewById(R.id.student_history_list)).getAdapter();

            boolean visitedBuildingInTimePeriod = false;
            for (Record record : adapter.records) {
                if (record.getBuildingName().equals(BUILDING) &&
                    record.getEpochTime() >= startEpochTime && record.getEpochTime() <= endEpochTime) {
                    visitedBuildingInTimePeriod = true;
                    break;
                }
            }
            assert visitedBuildingInTimePeriod;
            Espresso.pressBack();
            Espresso.pressBack();
        }
    }

    private Calendar getStartDate() {
        return new Calendar.Builder()
            .setDate(2021, 2, 29)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
    }

    private Calendar getEndDate() {
        return new Calendar.Builder()
            .setDate(2021, 3, 6)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
    }
}
