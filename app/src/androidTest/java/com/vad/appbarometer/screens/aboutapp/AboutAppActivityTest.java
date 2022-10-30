package com.vad.appbarometer.screens.aboutapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.vad.appbarometer.R;
import com.vad.appbarometer.screens.main.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AboutAppActivityTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.about_button)).perform(click());
    }

    @Test
    public void test_backToMainActivity() {
        pressBack();
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }
}