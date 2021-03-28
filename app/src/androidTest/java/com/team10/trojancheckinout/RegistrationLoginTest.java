package com.team10.trojancheckinout;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class RegistrationLoginTest {

    @Rule
    public ActivityTestRule<StudentRegisterActivity> activityRule =
            new ActivityTestRule<>(StudentRegisterActivity.class);
    @Test
    public void registerStudent(){

    }

    @Rule
    public ActivityTestRule<ManagerRegisterActivity> activityRule2 =
            new ActivityTestRule<>(ManagerRegisterActivity.class);
    @Test
    public void registerManager(){

    }

    @Rule
    public ActivityTestRule<LoginActivity> activityRule3 =
            new ActivityTestRule<>(LoginActivity.class);
    @Test
    public void loginUser(){

    }
}