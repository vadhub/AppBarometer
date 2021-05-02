package com.vad.appbarometer.utils;

public class MathSets {

    public final static int STANDART_GRAD = 10;

    public static float getGradus(float pressure){
        float v = 0;
        float coef = 3;
        if(pressure>1000){
            v = (pressure - 1000) * coef;
        } else{
            v = -(1000-pressure)*coef;
        }
        return v;
    }
}
