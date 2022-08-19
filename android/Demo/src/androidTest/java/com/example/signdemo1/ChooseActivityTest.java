package com.example.signdemo1;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChooseActivityTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule(ChooseActivity.class);

    @Test
    public void test() {
        Espresso.onView(withId(R.id.sign_in_activity)).perform(click());
        Espresso.onView(withId(R.id.title_text)).check(matches(withText("Microsoft Quick Auth SDK")));
        // close page
        Espresso.pressBack();

        Espresso.onView(withId(R.id.token_activity)).perform(click());
        Espresso.onView(withId(R.id.title_text)).check(matches(withText("Microsoft Quick Auth SDK")));
    }
}
