package com.vad.appbarometer.utils.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

public class GPSdata {
    public LocationRequest getLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        return locationRequest;
    }



}
