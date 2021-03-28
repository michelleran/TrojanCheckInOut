package com.team10.trojancheckinout;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.sleep;
import static org.junit.Assert.*;

public class StudentActivityTest {

    @Rule
    public ActivityTestRule<StudentActivity> activityRule =
            new ActivityTestRule<>(StudentActivity.class);

    @Test
    public void openStudentHistory() {
        Intents.init();
        sleep(WAIT_UI);
        onView(withId(R.id.viewHistorybtn)).perform(click());
        intended(hasComponent(StudentHistory.class.getName()));
        Intents.release();

    }

    @Test
    public void editProfileImage() {
    }


    @Test
    public void checkOut() {
    }

}