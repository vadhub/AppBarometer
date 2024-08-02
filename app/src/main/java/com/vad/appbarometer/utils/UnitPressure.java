package com.vad.appbarometer.utils;

public class UnitPressure {

    public final static int hPA = 0;
    public final static int mmHG = 1;
    public final static int mBAR = 2;

    public void setUnit(int type, Runnable hgp, Runnable mmHg, Runnable mBar) {
        switch (type) {
            case hPA -> hgp.run();
            case mmHG -> mmHg.run();
            case mBAR -> mBar.run();
        }
    }
}
