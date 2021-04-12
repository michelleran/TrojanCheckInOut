package com.team10.trojancheckinout;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.AssertionFailedError;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_LONG_OP;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.getCurrentActivity;
import static com.team10.trojancheckinout.TestUtils.selectTabAtPosition;
import static com.team10.trojancheckinout.TestUtils.sleep;
import static com.team10.trojancheckinout.TestUtils.withRecyclerView;

@RunWith(AndroidJUnit4.class)
public class BuildingModificationTest {

    private String TAG = "BuildingTest";

    public final String userGivenName = "Tester1";
    public final String userSurname = "Manager1";
    public final String userEmail = "tester1@usc.edu";
    public final String userPassword = "password";

    public final String buildingToAdd = "SampleBuilding";
    public int buildingMaxCap = 60;

    public int matchRowBuilding(String buildingToMatch) {
        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_DATA);
        RecyclerView list = getCurrentActivity().findViewById(R.id.building_list);
        int totalBuildingCount = list.getAdapter().getItemCount();
        int buildingIndex = -1;
        for (int i = 0; i < totalBuildingCount; i++) {
            onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(i));
            try {
                onView(withRecyclerView(R.id.building_list)
                        .atPositionOnView(i, R.id.building_name))
                        .check(matches(withText(buildingToMatch)));
                buildingIndex = i;
                break;
            }
            catch (AssertionFailedError e) {}
        }
        return buildingIndex;

    }

    public void addFunction() {
        onView(withId(R.id.btnAddBuilding)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.edtBcName)).perform(typeText(buildingToAdd));
        onView(withId(R.id.edtBcMaxCap)).perform(typeText(String.valueOf(buildingMaxCap)));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.btnBcConfirm)).perform(click());

        sleep(WAIT_LONG_OP);

        onView(withId(R.id.building_tab_content)).check(matches(isDisplayed()));
        onView(withId(R.id.building_list)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAddBuilding)).check(matches(isDisplayed()));

        int buildingIndex = matchRowBuilding(buildingToAdd);
        onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(buildingIndex));
        onView(withRecyclerView(R.id.building_list).atPositionOnView(buildingIndex, R.id.building_name)).check(matches(withText(buildingToAdd)));
    }

    public void editFunction() {
        String buildingToEdit = buildingToAdd;
        buildingMaxCap = buildingMaxCap + 1;
        String newCapacity = String.valueOf(buildingMaxCap);
        int buildingPosition = matchRowBuilding(buildingToEdit);
        Assert.assertNotEquals(buildingPosition, -1);

        onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(buildingPosition));
        sleep(WAIT_UI);
        onView(withRecyclerView(R.id.building_list).atPositionOnView(buildingPosition, R.id.btnBuildingEdit)).perform(click());
        sleep(WAIT_UI);
        onView(withText("Edit Capacity")).inRoot(isPlatformPopup()).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.edtBcMaxCap)).perform(clearText());
        onView(withId(R.id.edtBcMaxCap)).perform(typeText(newCapacity));
        onView(withId(R.id.edtBcMaxCap)).perform(ViewActions.closeSoftKeyboard());
        sleep(WAIT_UI);
        onView(withId(R.id.btnBcConfirm)).perform(click());
        sleep(WAIT_LONG_OP);

        onView(withId(R.id.building_list)).check(matches(isDisplayed()));

        int newBuildingPosition = matchRowBuilding(buildingToEdit);
        onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(newBuildingPosition));
        sleep(WAIT_UI);

        onView(withRecyclerView(R.id.building_list)
                .atPositionOnView(buildingPosition, R.id.txtBuildingMaximumCapacity))
                .check(matches(withText("Maximum Capacity: " + newCapacity)));
    }

    public void deleteFunction() {
        String buildingToDelete = buildingToAdd;
        RecyclerView list = getCurrentActivity().findViewById(R.id.building_list);
        int initialTotalBuildingCount = list.getAdapter().getItemCount();

        int buildingPosition = matchRowBuilding(buildingToDelete);
        Assert.assertNotEquals(buildingPosition, -1);

        onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(buildingPosition - 1));
        Matcher last = withRecyclerView(R.id.building_list).atPositionOnView(initialTotalBuildingCount - 2, R.id.building_name);

        onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(buildingPosition));
        onView(withRecyclerView(R.id.building_list).atPositionOnView(buildingPosition, R.id.btnBuildingEdit)).perform(click());

        onView(withText("Delete Building")).inRoot(isPlatformPopup()).perform(click());
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("OK")).inRoot(isDialog()).perform(click());

        sleep(WAIT_DATA);

        int currPosition = matchRowBuilding(buildingToDelete);
        Assert.assertEquals(currPosition, -1);
    }


    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        onView(withId(R.id.etEmail)).perform(typeText(userEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etPassword)).perform(typeText(userPassword));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_UI);
    }

    /*@Test
    public void verifyProperAddBuilding() {
        addFunction();
    }

    @Test
    public void testEditBuilding() {
        editFunction();
    }

    @Test
    public void testDeleteBuildingOK() {
       deleteFunction();
    }*/

    @Test
    public void consolidatedBuildingTest() {
        addFunction();
        editFunction();
        deleteFunction();
    }

    @After
    public void shutdown(){
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);
        onView(withId(R.id.btnLogout)).perform(click());
    }

}
