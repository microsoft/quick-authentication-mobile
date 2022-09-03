package com.example.signdemo1;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.example.signdemo1.test.TestActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestActivityTest {
  @Rule public ActivityTestRule activityTestRule = new ActivityTestRule(TestActivity.class);

  @Test
  public void test() {
    //        Espresso.onView(withId(R.id.text_click_button)).perform(typeText("2"),
    // closeSoftKeyboard());
    Espresso.onView(withId(R.id.text_click_button)).perform(click());
    // 通过id找到textview，并判断是否与文本匹配
    Espresso.onView(withId(R.id.helloWorldTextView)).check(matches(withText("clickMeBtn")));
    Espresso.onView(withId(R.id.helloWorldTextView)).check(matches(withText("Hello world!")));
  }
}
