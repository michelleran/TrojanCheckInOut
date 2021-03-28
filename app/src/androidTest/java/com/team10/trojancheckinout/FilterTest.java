package com.team10.trojancheckinout;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.team10.trojancheckinout.model.Record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.TimeZone;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.instanceOf;

import static com.team10.trojancheckinout.TestUtils.*;

@RunWith(AndroidJUnit4.class)
public class FilterTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
        new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    @Before
    public void login_navigateToFilterTab() {
        // login
        onView(withId(R.id.etEmail)).perform(typeText("ranmiche@usc.edu"));
        onView(withId(R.id.etPassword)).perform(typeText("12345678"));
        onView(withId(R.id.etPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);

        // navigate to filter tab
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(2));
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
    public void filterBy_invalidId() {
        // input too short USC id
        onView(withId(R.id.filter_student_id_field)).perform(typeText("12345"));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // assert that toast was shown
        onView(withText(R.string.filter_invalid_usc_id))
            .inRoot(withDecorView(not(is(getCurrentActivity().getWindow().getDecorView()))))
            .check(matches(isDisplayed()));
    }

    @Test
    public void filterBy_building() {
        final String BUILDING = "Mudd Hall";
        // input building name
        onView(withId(R.id.filter_building_field)).perform(typeText(BUILDING));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
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
        // input student id
        onView(withId(R.id.filter_student_id_field)).perform(typeText(ID));
        onView(withId(R.id.filter_student_id_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // scroll to student
            onView(withId(R.id.results_list))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // open profile
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert that id matches
            onView(withId(R.id.id)).check(matches(withText(ID)));
            Espresso.pressBack();
        }
    }

    @Test
    public void filterBy_major() {
        final String MAJOR = "CSCI";
        // select major
        onView(withId(R.id.filter_major_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(MAJOR))).perform(click());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // scroll to student
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
    public void filterBy_buildingId() {
        final String BUILDING = "Mudd Hall";
        final String ID = "0123456789";
        // input building name
        onView(withId(R.id.filter_building_field)).perform(typeText(BUILDING));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());

        // input student id
        onView(withId(R.id.filter_student_id_field)).perform(typeText(ID));
        onView(withId(R.id.filter_student_id_field)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // assert that building name matches
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_building_name))
                .check(matches(withText(BUILDING)));
            // scroll to student
            onView(withId(R.id.results_list))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // open profile
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert that id matches
            onView(withId(R.id.id)).check(matches(withText(ID)));
            Espresso.pressBack();
        }
    }

    @Test
    public void filterBy_buildingMajor() {
        final String BUILDING = "Mudd Hall";
        final String MAJOR = "CSCI";
        // input building name
        onView(withId(R.id.filter_building_field)).perform(typeText(BUILDING));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());

        // select major
        onView(withId(R.id.filter_major_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(MAJOR))).perform(click());
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // assert that building name matches
            onView(withRecyclerView(R.id.results_list)
                .atPositionOnView(i, R.id.record_building_name))
                .check(matches(withText(BUILDING)));
            // scroll to student
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
    public void filterBy_startDate() {
        // select start date field
        onView(withId(R.id.start_date)).perform(click());

        // select March 22, 2021 09:00 PDT
        Calendar cal = new Calendar.Builder()
            .setDate(2021, 3, 22)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
        long startEpochTime = cal.toInstant().getEpochSecond();

        selectDateTime(cal);
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        RecordAdapter adapter = (RecordAdapter) list.getAdapter();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            // assert that time is at or after start date
            long time = adapter.getEpochTimeOfRecord(i);
            assert time >= startEpochTime;
        }
    }

    @Test
    public void filterBy_endDate() {
        // select end date field
        onView(withId(R.id.end_date)).perform(click());

        // select March 22, 2021 09:00 PDT
        Calendar cal = new Calendar.Builder()
            .setDate(2021, 3, 22)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
        long endEpochTime = cal.toInstant().getEpochSecond();

        selectDateTime(cal);
        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        RecordAdapter adapter = (RecordAdapter) list.getAdapter();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            // assert that time is before or at end date
            long time = adapter.getEpochTimeOfRecord(i);
            assert time <= endEpochTime;
        }
    }

    @Test
    public void filterBy_startEndDate() {
        // select start date field
        onView(withId(R.id.start_date)).perform(click());

        // select March 20, 2021 09:00 PDT
        Calendar cal = new Calendar.Builder()
            .setDate(2021, 3, 20)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
        long startEpochTime = cal.toInstant().getEpochSecond();
        selectDateTime(cal);

        // select end date field
        onView(withId(R.id.end_date)).perform(click());

        // select March 24, 2021 09:00 PDT
        cal = new Calendar.Builder()
            .setDate(2021, 3, 24)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
        long endEpochTime = cal.toInstant().getEpochSecond();
        selectDateTime(cal);

        onView(withId(R.id.filter_button)).perform(click());

        // wait for results to load
        sleep(WAIT_DATA);

        RecyclerView list = getCurrentActivity().findViewById(R.id.results_list);
        RecordAdapter adapter = (RecordAdapter) list.getAdapter();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            // assert that time is between start and end date
            long time = adapter.getEpochTimeOfRecord(i);
            assert startEpochTime <= time && time <= endEpochTime;
        }
    }

    @Test
    public void filterBy_startDateAfterEndDate() {
        // select start date field
        onView(withId(R.id.start_date)).perform(click());

        // select March 24, 2021 09:00 PDT
        Calendar cal = new Calendar.Builder()
            .setDate(2021, 3, 24)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
        long startEpochTime = cal.toInstant().getEpochSecond();
        selectDateTime(cal);

        // select end date field
        onView(withId(R.id.end_date)).perform(click());

        // select March 20, 2021 09:00 PDT
        cal = new Calendar.Builder()
            .setDate(2021, 3, 20)
            .setTimeOfDay(9, 0, 0)
            .setTimeZone(TimeZone.getTimeZone(Record.pst))
            .build();
        long endEpochTime = cal.toInstant().getEpochSecond();
        selectDateTime(cal);

        onView(withId(R.id.filter_button)).perform(click());

        // assert that toast was shown
        onView(withText(R.string.filter_invalid_dates))
            .inRoot(withDecorView(not(is(getCurrentActivity().getWindow().getDecorView()))))
            .check(matches(isDisplayed()));
    }

    private void selectDateTime(Calendar cal) {
        // select date
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
            .perform(PickerActions.setDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)));
        // select ok
        onView(withId(android.R.id.button1)).perform(click());

        // select time
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
            .perform(PickerActions.setTime(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)));
        // select ok
        onView(withId(android.R.id.button1)).perform(click());
    }
}
