package com.vad.appbarometer.screens.main;

import com.vad.appbarometer.screens.GoogleApi;

//this interface for communicate between Fragment and PressurePresenter
public interface PressureView extends GoogleApi {
    void setPressure(float value);

    void showError(String error);

    boolean isDataFromInternet();
}
