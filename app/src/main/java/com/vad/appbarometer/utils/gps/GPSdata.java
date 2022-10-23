package com.vad.appbarometer.utils.gps;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.vad.appbarometer.screens.main.PresenterView;

public class GPSdata {

    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationManager mLocationManager;
    private final PresenterView view;

    public GPSdata(FusedLocationProviderClient fusedLocationProviderClient, LocationManager mLocationManager, PresenterView view) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.mLocationManager = mLocationManager;
        this.view = view;
    }

    public LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = null;
                if (task.isSuccessful() && task.getResult() != null) {
                    location = task.getResult();
                }
                if (location != null) {
                    view.response((float) location.getLatitude(), (float) location.getLongitude());
                } else {
                    getLocationCallback();
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationCallback() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                view.response((float) location.getLatitude(), (float) location.getLongitude());
                fusedLocationProviderClient.removeLocationUpdates(this);
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), locationCallback, Looper.myLooper());
    }

}
