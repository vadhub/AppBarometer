package com.vad.appbarometer.screens.main;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

public interface PressureView {
    void setStartPositionUnit(float value);
    void showError(String str);
    void showDialog(Status status);
    GoogleApiClient getGoogleApiClient();
}
