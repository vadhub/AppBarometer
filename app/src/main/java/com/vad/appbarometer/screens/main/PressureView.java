package com.vad.appbarometer.screens.main;

import com.vad.appbarometer.screens.GoogleApi;

public interface PressureView extends GoogleApi {
    void setPressure(float value);

    void showError(String str);

    boolean isOnline();
}
