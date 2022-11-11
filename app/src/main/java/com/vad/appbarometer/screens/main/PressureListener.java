package com.vad.appbarometer.screens.main;

import com.vad.appbarometer.screens.GoogleApi;

//this interface for communicate between MainActivity and PressurePresenter
public interface PressureListener extends GoogleApi {
    void setPressure(float value);

    void showError(String str);

    boolean isDataFromInternet();
}
