package com.vad.appbarometer.utils.gps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vad.appbarometer.screens.main.MainActivity;
import com.vad.appbarometer.screens.main.RequestCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSdata {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager mLocationManager;
    private Geocoder geocoder;

    public GPSdata(FusedLocationProviderClient fusedLocationProviderClient, LocationManager mLocationManager, Geocoder geocoder) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.mLocationManager = mLocationManager;
        this.geocoder = geocoder;
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
    }

    public LocationRequest getLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    @SuppressLint("MissingPermission")
    public float[] getLocation() {
        final float[] coordinates = new float[2];

        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = null;
                    if(task.isSuccessful() && task.getResult()!=null){
                        location  = task.getResult();
                    }

                    if (location != null) {
                        coordinates[0] = (float) getCoordinate(location).get(0).getLatitude();
                        coordinates[1] = (float) getCoordinate(location).get(0).getLongitude();
                    }else{
                        coordinates[0] = (float) getLocationCallback().getLatitude();
                        coordinates[1] = (float) getLocationCallback().getLongitude();
                    }
                }
            });
        }

        return coordinates;
    }

    private List<Address> getCoordinate(Location location){
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addressList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    private Location getLocationCallback(){
        final Location[] location = new Location[1];
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                location[0] = locationResult.getLastLocation();
                fusedLocationProviderClient.removeLocationUpdates(this);
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), locationCallback, Looper.myLooper());
        return location[0];
    }

}
