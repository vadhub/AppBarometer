package com.vad.appbarometer.utils.math;


public class MathSets {

    public static float getGradus(float pressure) {
        float v = 0;
        float coef = 3;
        v = (pressure - 1000) * coef;
        return v;
    }

    public static float convertToMmHg(float gPa) {
        return (float) (gPa / 1.334);
    }


}
