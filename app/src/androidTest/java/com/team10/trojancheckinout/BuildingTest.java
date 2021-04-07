package com.team10.trojancheckinout;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import junit.framework.AssertionFailedError;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.team10.trojancheckinout.TestUtils.WAIT_DATA;
import static com.team10.trojancheckinout.TestUtils.WAIT_LONG_OP;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.getCurrentActivity;
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
import java.util.Locale;
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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

public class BuildingTest {

    private String TAG = "BuildingTest";

    public final String userGivenName = "Tester1";
    public final String userSurname = "Manager1";
    public final String userEmail = "tester1@usc.edu";
    public final String userPassword = "password";

    public final String buildingToAdd = "SampleBuilding";
    public final int buildingMaxCap = 60;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
        new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void login() {
        onView(withId(R.id.etEmail)).perform(typeText(userEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.etPassword)).perform(typeText(userPassword));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_DATA);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        sleep(WAIT_UI);
    }

   // Check if the building fragments are being displayed
    @Test
    public void verifyView() {
        onView(withId(R.id.building_tab_content)).check(matches(isDisplayed()));
        onView(withId(R.id.building_list)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAddBuilding)).check(matches(isDisplayed()));
        sleep(WAIT_UI);
    }

    @Test
    public void verifyBuildingRowClick() {
        final String BUILDING = "Mudd Hall";
        // select building
        onView(withId(R.id.building_list))
            .perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText(BUILDING)), click()));

        sleep(WAIT_UI);

        // check building name
        onView(withId(R.id.building_details_name))
            .check(matches(allOf(withText(BUILDING), isDisplayed())));

        RecyclerView list = getCurrentActivity().findViewById(R.id.building_details_students);
        int count = list.getAdapter().getItemCount();

        // assert that current capacity = length of list
        onView(withId(R.id.building_details_capacity))
            .check(matches(
                withText(startsWith(String.format(Locale.US, "Capacity: %d/", count)))
            ));

        for (int i = 0; i < count; i++) {
            // scroll to student
            onView(withId(R.id.building_details_students))
                .perform(RecyclerViewActions.scrollToPosition(i));
            // open profile
            onView(withRecyclerView(R.id.building_details_students)
                .atPositionOnView(i, R.id.record_student_photo))
                .perform(click());
            // assert that current building matches
            onView(withId(R.id.currentBuilding)).check(matches(withText(BUILDING)));
            Espresso.pressBack();
        }
        Espresso.pressBack();
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

    @Test
    public void verifyCancelFunctionality() {
        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));

        onView(withId(R.id.btnAddBuilding)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.btnBcCancel)).perform(click());
        sleep(WAIT_UI);

        // Check if we have come back to original screen
        onView(withId(R.id.building_tab_content)).check(matches(isDisplayed()));
        onView(withId(R.id.building_list)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAddBuilding)).check(matches(isDisplayed()));
    }

    @Test
    public void verifyConfirmFunctionality() {
        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));

        onView(withId(R.id.btnAddBuilding)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.btnBcConfirm)).perform(click());
        sleep(WAIT_UI);

        //Check if proper toast is displayed
        onView(withText("Please fill out all of the following fields"))
                .inRoot(withDecorView(not(is(getCurrentActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    /*@Test
    public void verifyProperAddBuilding() {

        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));

        sleep(WAIT_DATA);
        onView(withId(R.id.btnAddBuilding)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.edtBcName)).perform(typeText(buildingToAdd));
        onView(withId(R.id.edtBcMaxCap)).perform(typeText(String.valueOf(buildingMaxCap)));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnBcConfirm)).perform(click());

        sleep(WAIT_LONG_OP);

        onView(withId(R.id.building_tab_content)).check(matches(isDisplayed()));
        onView(withId(R.id.building_list)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAddBuilding)).check(matches(isDisplayed()));

        int buildingIndex = matchRowBuilding(buildingToAdd);
        onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(buildingIndex));
        onView(withRecyclerView(R.id.building_list).atPositionOnView(buildingIndex, R.id.building_name)).check(matches(withText(buildingToAdd)));
    }*/

    @Test
    public void verifyNoNameAddBuilding() {
        String newBuilding = "";
        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));

        onView(withId(R.id.btnAddBuilding)).perform(click());
        sleep(WAIT_UI);

        onView(withId(R.id.edtBcName)).perform(typeText(newBuilding));
        sleep(WAIT_UI);
        onView(withId(R.id.edtBcName)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.edtBcMaxCap)).perform(typeText(String.valueOf(buildingMaxCap)));
        onView(withId(R.id.filter_building_field)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.edtBcName)).check(matches(hasErrorText("Building name cannot be empty")));
        onView(withId(R.id.btnBcCancel)).perform(click());
    }

    @Test
    public void testDeleteBuildingCancel() {

        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
        RecyclerView list = getCurrentActivity().findViewById(R.id.building_list);
        int initialTotalBuildingCount = list.getAdapter().getItemCount();

        onView(withId(R.id.building_list)).perform(RecyclerViewActions.scrollToPosition(initialTotalBuildingCount - 1));
        Matcher previous = withRecyclerView(R.id.building_list).atPositionOnView(initialTotalBuildingCount - 1, R.id.building_name);
        onView(withRecyclerView(R.id.building_list).atPositionOnView(initialTotalBuildingCount - 1, R.id.btnBuildingEdit)).perform(click());

        onView(withText("Delete Building")).inRoot(isPlatformPopup()).perform(click());
        onView(withText("CANCEL")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("CANCEL")).inRoot(isDialog()).perform(click());
        sleep(WAIT_DATA);

        int finalTotalBuildingCount = list.getAdapter().getItemCount();

        Assert.assertEquals(initialTotalBuildingCount, finalTotalBuildingCount);
        onView(withRecyclerView(R.id.building_list).atPositionOnView(initialTotalBuildingCount - 1, R.id.building_name)).check(matches(previous));
    }

    /*@Test
    public void testDeleteBuildingOK() {
        String buildingToDelete = buildingToAdd;
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1));
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
    }*/

    @Test
    public void verifyUniqueQRCodes() {
        RecyclerView list = getCurrentActivity().findViewById(R.id.building_list);
        Bitmap lastQR = null;
        for (int i = 0; i < list.getAdapter().getItemCount(); i++) {
            // view building's QR code
            onView(withId(R.id.building_list))
                .perform(RecyclerViewActions.scrollToPosition(i));
            onView(withRecyclerView(R.id.building_list)
                .atPositionOnView(i, R.id.btnBuildingEdit)).perform(click());
            onView(withText("View QR Code")).inRoot(isPlatformPopup()).perform(click());

            sleep(WAIT_DATA);
            Bitmap qr = ((BitmapDrawable) ((ImageView) getCurrentActivity()
                .findViewById(R.id.imgBcQR))
                .getDrawable())
                .getBitmap();
            Espresso.pressBack();

            assert qr != lastQR;
            lastQR = qr;
        }
    }

    @After
    public void logout() {
        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);
        onView(withId(R.id.btnLogout)).perform(click());
        sleep(WAIT_UI);
    }

}
