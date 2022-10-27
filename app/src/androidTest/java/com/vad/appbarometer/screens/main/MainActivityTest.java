package com.vad.appbarometer.screens.main;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.core.app.ActivityScenario;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.vad.appbarometer.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    @Before
    public void beforeBaseTest() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void test_isActivityInView() {
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void test_visibilityTextView() {
        onView(withId(R.id.textViewIndicator)).check(matches(isDisplayed()));
        onView(withId(R.id.mBarText)).check(matches(isDisplayed()));
    }



}