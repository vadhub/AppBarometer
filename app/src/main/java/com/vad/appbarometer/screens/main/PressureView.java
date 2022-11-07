package com.vad.appbarometer.screens.main;

//this interface for communicate between MainActivity and PressurePresenter
public interface PressureView {
    void setPressure(float value);

    void showError(String str);

    boolean isDataFromInternet();
}
