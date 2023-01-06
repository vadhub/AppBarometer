package com.vad.appbarometer.screens.main;

import android.app.Activity;

//this interface for communicate between MainActivity and PressurePresenter
public interface PressureListener {
    void setPressure(float value);

    void showError(String str);

    boolean isDataFromInternet();

    Activity getActivity();
}
