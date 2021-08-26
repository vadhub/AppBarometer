package com.vad.appbarometer.screens.main;

import com.google.android.gms.common.api.GoogleApiClient;

public interface PressureView {
    void setPressure(float value);
    void showError(String str);
    GoogleApiClient getGoogleApiClient();
}
