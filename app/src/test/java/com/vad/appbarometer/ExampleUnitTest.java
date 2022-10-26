package com.vad.appbarometer;

import org.junit.Test;

import static org.junit.Assert.*;

import com.vad.appbarometer.utils.math.MathSets;

public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }

    private static final double DELTA = 0.1;

    @Test
    public void convertToMmHG_isCorrect() {
        assertEquals(789.3553f, MathSets.convertToMmHg(1053), DELTA);
        assertEquals(794.6026f, MathSets.convertToMmHg(1060), DELTA);
        assertEquals(0f, MathSets.convertToMmHg(0), DELTA);
    }

    @Test
    public void degree_isCorrect() {
        assertEquals(195, MathSets.getGradus(1065), DELTA);
        assertEquals(-105, MathSets.getGradus(950), DELTA);
        assertEquals(0, MathSets.getGradus(0), DELTA);
        assertEquals(-2997, MathSets.getGradus(1), DELTA);
    }
}