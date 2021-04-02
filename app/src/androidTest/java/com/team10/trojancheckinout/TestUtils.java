package com.team10.trojancheckinout;

import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.AllOf;

import java.util.Calendar;
import java.util.Collection;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Modified from https://github.com/dannyroa/espresso-samples
 */
public class TestUtils {

    public static <VH extends RecyclerView.ViewHolder> ViewAction actionOnItemViewAtPosition(int position,
                                                                                             @IdRes
                                                                                                 int viewId,
                                                                                             ViewAction viewAction) {
        return new ActionOnItemViewAtPositionViewAction(position, viewId, viewAction);
    }

    private static final class ActionOnItemViewAtPositionViewAction<VH extends RecyclerView
        .ViewHolder>
        implements

        ViewAction {
        private final int position;
        private final ViewAction viewAction;
        private final int viewId;

        private ActionOnItemViewAtPositionViewAction(int position,
                                                     @IdRes int viewId,
                                                     ViewAction viewAction) {
            this.position = position;
            this.viewAction = viewAction;
            this.viewId = viewId;
        }

        public Matcher<View> getConstraints() {
            return Matchers.allOf(new Matcher[] {
                ViewMatchers.isAssignableFrom(RecyclerView.class), ViewMatchers.isDisplayed()
            });
        }

        public String getDescription() {
            return "actionOnItemAtPosition performing ViewAction: "
                + this.viewAction.getDescription()
                + " on item at position: "
                + this.position;
        }

        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            (new ScrollToPositionViewAction(this.position)).perform(uiController, view);
            uiController.loopMainThreadUntilIdle();

            View targetView = recyclerView.getChildAt(this.position).findViewById(this.viewId);

            if (targetView == null) {
                throw (new PerformException.Builder()).withActionDescription(this.toString())
                    .withViewDescription(

                        HumanReadables.describe(view))
                    .withCause(new IllegalStateException(
                        "No view with id "
                            + this.viewId
                            + " found at position: "
                            + this.position))
                    .build();
            } else {
                this.viewAction.perform(uiController, targetView);
            }
        }
    }

    private static final class ScrollToPositionViewAction implements ViewAction {
        private final int position;

        private ScrollToPositionViewAction(int position) {
            this.position = position;
        }

        public Matcher<View> getConstraints() {
            return Matchers.allOf(new Matcher[] {
                ViewMatchers.isAssignableFrom(RecyclerView.class), ViewMatchers.isDisplayed()
            });
        }

        public String getDescription() {
            return "scroll RecyclerView to position: " + this.position;
        }

        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.scrollToPosition(this.position);
        }
    }


    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {

        return new RecyclerViewMatcher(recyclerViewId);
    }

    public static final int WAIT_DATA = 3000;
    public static final int WAIT_LONG_OP = 5000;
    public static final int WAIT_UI = 1000;

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static ViewAction selectTabAtPosition(final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription() {
                return "with tab at index" + String.valueOf(position);
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view instanceof TabLayout) {
                    TabLayout tabLayout = (TabLayout) view;
                    TabLayout.Tab tab = tabLayout.getTabAt(position);

                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        };
    }

    public static AppCompatActivity getCurrentActivity() {
        final AppCompatActivity[] activity = new AppCompatActivity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            AppCompatActivity currentActivity = null;
            Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            if (resumedActivities.iterator().hasNext()){
                currentActivity = (AppCompatActivity) resumedActivities.iterator().next();
                activity[0] = currentActivity;
            }
        });

        return activity[0];
    }

    public static void selectInSpinner(int id, String item) {
        onView(withId(id)).perform(click());
        onData(AllOf.allOf(is(instanceOf(String.class)), is(item))).perform(click());
    }

    public static void selectDateTime(Calendar cal) {
        // select date
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
            .perform(PickerActions.setDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
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