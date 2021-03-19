package com.team10.trojancheckinout;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ManagerTest {
    @Rule
    public ActivityScenarioRule<ManagerActivity> activityRule =
        new ActivityScenarioRule<>(ManagerActivity.class);

    @Test
    public void inputInvalidFilters() {
        onView(withId(R.id.filter_student_id_field)).perform(typeText("12345"));
        onView(withId(R.id.filter_button)).perform(click());
        // TODO
    }

    @Test
    public void inputValidFilters() {
        onView(withId(R.id.filter_building_field)).perform(typeText("SAL"));
        onView(withId(R.id.filter_button)).perform(click());
        // TODO
    }
}