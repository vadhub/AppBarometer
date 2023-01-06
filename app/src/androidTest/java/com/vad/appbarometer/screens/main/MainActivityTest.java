package com.vad.appbarometer.screens.main;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;
import androidx.test.core.app.ActivityScenario;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.vad.appbarometer.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    @Before
    public void setUp() {
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

    @Test
    public void test_visibilityGauger() {
        onView(withId(R.id.imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.imageViewArrow)).check(matches(isDisplayed()));
    }

    @Test
    public void test_visibilityText() {
        onView(withId(R.id.textViewIndicator)).check(matches(isDisplayed()));
    }

    @Test
    public void test_containTextPressure() {
        onView(withId(R.id.mBarText)).check(matches(withText("1013.25 hPa")));
    }

    @Test
    public void test_openAboutActivity() {
        onView(withId(R.id.about_button)).perform(click());
        onView(withId(R.id.about_activity)).check(matches(isDisplayed()));
    }

//    @Test
//    public void test_progressBarCheck() {
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Drawable notAnimatedDrawable = ContextCompat.getDrawable(appContext, R.drawable.guage);
//        ((ProgressBar) appContext.findViewById(R.id.progressBar)).setIndeterminateDrawable(notAnimatedDrawable);
//        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
//    }


}