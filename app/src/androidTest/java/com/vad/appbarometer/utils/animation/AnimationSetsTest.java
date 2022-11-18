package com.vad.appbarometer.utils.animation;

import static org.junit.Assert.*;


import android.view.animation.AnimationSet;

import org.junit.Before;
import org.junit.Test;

public class AnimationSetsTest {

    private AnimationSet animationSet;

    @Before
    public void setUp() {
        animationSet = new AnimationSets().animationRotate(90);
    }

    @Test
    public void test_animationSetIsCorrect() {
        assertEquals(1500, animationSet.getDuration());
    }
}