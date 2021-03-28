package com.team10.trojancheckinout;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.team10.trojancheckinout.TestUtils.WAIT_UI;
import static com.team10.trojancheckinout.TestUtils.sleep;

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
        sleep(WAIT_UI);
        //check if appropriate recycler view is displayed on following page
        onView(withId(R.id.student_history_list)).check(matches(isDisplayed()));
        Intents.release();

    }

    @Test
    public void editProfileImage() {


    }

    @Test
    public void checkOut() {
        sleep(WAIT_UI);
        onView(withId(R.id.checkOutbtn)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.currentBuilding)).check(matches(withText(R.string.none)));
    }

    @Test
    public void cancelDeleteAccountPopUp(){
        sleep(3000);
        onView(withId(R.id.floatingActionButton2)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //cancel delete
        onView(withText(R.string.cancel)).perform(click());
    }

    @Test
    public void confirmDeleteAccountPopUp(){
        sleep(WAIT_UI);
        onView(withId(R.id.floatingActionButton2)).perform(click());
        sleep(WAIT_UI);
        onView(withText(R.string.delete_dialog_message)).check(matches(isDisplayed()));

        //confirm delete
        onView(withText(R.string.confirm)).perform(click());
        sleep(WAIT_UI);
        onView(withId(R.id.studentRegisterBtn)).check(matches(isDisplayed()));
    }
}