package com.team10.trojancheckinout;

import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
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
import static org.hamcrest.core.StringContains.containsString;


import static com.team10.trojancheckinout.TestUtils.*;

@RunWith(AndroidJUnit4.class)
public class ManagerProfileTest {

    private String TAG = "ManagerProfileTest";

    @Rule
    public ActivityTestRule<ManagerActivity> activityRule =
            new ActivityTestRule<>(ManagerActivity.class);

    @Before
    public void setUp() {
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);
    }

    @Test
    public void checkProfileInformation() {
        onView(withId(R.id.txtGivenName)).check(matches(isDisplayed()));
        onView(withId(R.id.txtSurname)).check(matches(isDisplayed()));
        onView(withId(R.id.txtEmail)).check(matches(isDisplayed()));

    }

    @Test
    public void changePass() {

        onView(withId(R.id.btnEdit)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.edtNewPassword)).check(matches(isDisplayed()));

    }
}
