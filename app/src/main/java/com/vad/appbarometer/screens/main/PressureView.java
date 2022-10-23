package com.vad.appbarometer.screens.main;

import com.vad.appbarometer.screens.GoogleApi;

//this interface for comunicate between MainActivity and PressurePresenter
public interface PressureView extends GoogleApi {
    void setPressure(float value);

    void showError(String str);

    boolean isOnline();
}
