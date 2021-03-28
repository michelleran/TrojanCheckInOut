package com.team10.trojancheckinout;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityResult;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.team10.trojancheckinout.model.Record;
import com.team10.trojancheckinout.model.Server;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import androidx.test.espresso.intent.Intents.*;
import androidx.test.espresso.intent.matcher.IntentMatchers.*;
import androidx.test.espresso.intent.rule.IntentsTestRule;

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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.StringContains.containsString;


import static com.team10.trojancheckinout.TestUtils.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class ManagerProfileTest {

    private String TAG = "ManagerProfileTest";

    public final String userGivenName = "Tester1";
    public final String userSurname = "Manager1";
    public final String userEmail = "tester1@usc.edu";
    public final String userPassword = "password";

    @Before
    public void setUp() {

        ActivityScenario activityScenario = ActivityScenario.launch(StartPage.class);
        onView(withId(R.id.startLoginbtn)).perform(click());

        sleep(WAIT_UI);

        onView(withId(R.id.etEmail)).perform(typeText(userEmail));
        onView(withId(R.id.etPassword)).perform(typeText(userPassword));
        onView(withId(R.id.btnLogin)).perform(click());

        sleep(WAIT_UI);
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);

    }

    @Test
    public void checkProfileInformation() {
        String matchGivenName = "First Name: " + userGivenName;
        String matchSurname = "Surname: " + userSurname;
        String matchEmail = "Email: " + userEmail;
        onView(withId(R.id.txtGivenName)).check(matches(withText(matchGivenName)));
        onView(withId(R.id.txtSurname)).check(matches(withText(matchSurname)));
        onView(withId(R.id.txtEmail)).check(matches(withText(matchEmail)));
        onView(withId(R.id.imgPhoto)).check(matches(isDisplayed()));
    }

    @Test
    public void test_validateGalleryIntent() {

        onView(withId(R.id.tabs)).perform(selectTabAtPosition(0));
        sleep(WAIT_UI);

        Matcher<Intent> expectedIntent = AllOf.allOf(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT));
        Intents.init();
        Intents.intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, getGalleryIntent()));
        sleep(WAIT_UI);
        onView(withId(R.id.btnChangePicture)).perform(click());
        sleep(WAIT_UI);
        Intents.intended(expectedIntent);
        Intents.release();
    }

    private Intent getGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setData(Uri.parse("android.resource://com.team10.trojancheckinout/" + R.drawable.default_profile_picture));
        return intent;
    }


//    Check if we can click the logout button and then get routed back to the start page
    @After
    public void logout() {
        onView(withId(R.id.btnLogout)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
    }
}
