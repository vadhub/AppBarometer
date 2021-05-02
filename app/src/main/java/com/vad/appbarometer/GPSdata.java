package com.vad.appbarometer;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class GPSdata implements LocationListener {

    private String locateLat;
    private String locateLong;

    public String getLocateLat() {
        return locateLat;
    }

    public void setLocateLat(String locateLat) {
        this.locateLat = locateLat;
    }

    public String getLocateLong() {
        return locateLong;
    }

    public void setLocateLong(String locateLong) {
        this.locateLong = locateLong;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locateLat = String.valueOf(location.getLatitude());
        locateLong = String.valueOf(location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
