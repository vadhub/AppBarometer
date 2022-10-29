package com.vad.appbarometer.utils.savestateunit;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

public class SaveStateTest {

    private SaveState saveState;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        saveState = new SaveState(context);
    }

    @Test
    public void saveHpa_isCorrect() {
        saveState.saveStatePres(0);
        assertEquals(saveState.getStatePres(), 0);
    }

    @Test
    public void getMmhg_isCorrect() {
        saveState.saveStatePres(1);
        assertEquals(saveState.getStatePres(), 1);
    }
}