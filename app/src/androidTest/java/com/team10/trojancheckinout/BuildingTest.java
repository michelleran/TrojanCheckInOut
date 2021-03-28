package com.team10.trojancheckinout;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.rule.ActivityTestRule;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.selectTabAtPosition;
import static com.team10.trojancheckinout.TestUtils.sleep;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Calendar;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.action.ViewActions.*;
import static com.team10.trojancheckinout.TestUtils.withRecyclerView;

public class BuildingTest {

    private String TAG = "BuildingTest";

    public final String userGivenName = "Tester1";
    public final String userSurname = "Manager1";
    public final String userEmail = "tester1@usc.edu";
    public final String userPassword = "password";

    public final String buildingToAdd = "SampleBuilding";
    public final int buildingMaxCap = 60;

    @Rule
    public ActivityTestRule<ManagerActivity> activityRule = new ActivityTestRule<>(ManagerActivity.class);


    @Before
    public void login() {

        ActivityScenario activityScenario = ActivityScenario.launch(StartPage.class);
        onView(withId(R.id.startLoginbtn)).perform(click());

        sleep(WAIT_UI);

        onView(withId(R.id.etEmail)).perform(typeText(userEmail));
        onView(withId(R.id.etPassword)).perform(typeText(userPassword));
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_UI);
    }


   // Check if the building fragments are being displayed
    @Test
    public void verifyView() {
        onView(withId(R.id.building_list)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAddBuilding)).check(matches(isDisplayed()));
        sleep(WAIT_UI);
    }

    // Renders list properly (check for KAP building)
    @Test
    public void verifyBuildingRowNameMatch() {
        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_DATA);
        String buildingToMatch = "KAP";
        RecyclerView list = activityRule.getActivity().findViewById(R.id.building_list);
        int totalBuildingCount = list.getAdapter().getItemCount();
        int buildingIndex = -1;
        for (int i = 0; i < totalBuildingCount; i++) {
            if (i != totalBuildingCount - 1){
                try {
                    onView(withRecyclerView(R.id.building_list)
                            .atPositionOnView(i, R.id.building_name))
                            .check(matches(withText(buildingToMatch)));
                    buildingIndex = i;
                    break;
                }
                catch (AssertionFailedError e) {}
            }
            else {
                onView(withRecyclerView(R.id.building_list)
                        .atPositionOnView(i, R.id.building_name))
                        .check(matches(withText(buildingToMatch)));
            }
        }

        onView(withRecyclerView(R.id.building_list).atPositionOnView(buildingIndex, R.id.building_name)).perform(click());
        sleep(WAIT_DATA);
    }

    @Test
    public void verifyBuildingRowClick() {
        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_UI);


        RecyclerView list = activityRule.getActivity().findViewById(R.id.building_list);
        onView(withRecyclerView(R.id.building_list)
                .atPositionOnView(0, R.id.building_name)).perform(click());
        sleep(WAIT_DATA);

        //Check we have went to building details
        onView(withId(R.id.building_details_name)).check(matches(isDisplayed()));
    }

    @Test
    public void verifyBuildingChangeFragmentRender() {
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_UI);

        onView(withId(R.id.btnAddBuilding)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.edtBcName)).check(matches(isDisplayed()));
        onView(withId(R.id.edtBcMaxCap)).check(matches(isDisplayed()));
        onView(withId(R.id.btnBcConfirm)).check(matches(isDisplayed()));
        onView(withId(R.id.btnBcCancel)).check(matches(isDisplayed()));

        onView(withId(R.id.edtBcName)).check(matches(withHint("Building Name")));
        onView(withId(R.id.edtBcMaxCap)).check(matches(withHint("Maximum Capacity")));
        onView(withId(R.id.btnBcCancel)).check(matches(withText("Cancel")));
        onView(withId(R.id.btnBcConfirm)).check(matches(withText("Confirm")));
    }



    @After
    public void logout() {
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);
        onView(withId(R.id.btnLogout)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
    }

}
