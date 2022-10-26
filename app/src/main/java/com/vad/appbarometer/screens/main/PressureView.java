package com.vad.appbarometer.screens.main;

import com.vad.appbarometer.screens.GoogleApi;

import java.util.Random;

//this interface for communicate between MainActivity and PressurePresenter
public interface PressureView extends GoogleApi {
    void setPressure(float value);

    void showError(String str);

    boolean isDataFromInternet();
}
