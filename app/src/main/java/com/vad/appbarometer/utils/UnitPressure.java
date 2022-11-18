package com.vad.appbarometer.utils;

public class UnitPressure {

    public void setUnit(int type, Runnable hgp, Runnable mmHg) {
        if (type == 0) {
            hgp.run();
        } else {
            mmHg.run();
        }
    }
}
