package com.vad.appbarometer.utils.gps;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.location.LocationRequest;
import com.vad.appbarometer.screens.main.Response;

public class GPSdata {

    private final LocationManager mLocationManager;
    private final Response response;

    public GPSdata(LocationManager mLocationManager, Response view) {
        this.mLocationManager = mLocationManager;
        this.response = view;
    }

    public LocationRequest getLocationRequest() {
        return LocationRequest.create();
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            LocationListener mLocationListenerGPS = new LocationListener() {
                @SuppressLint("SetTextI18n")
                public void onLocationChanged(Location location) {
                    response.toResponse((float) location.getLatitude(), (float) location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {

                }
            };

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 0, mLocationListenerGPS);
        }

    }

}
