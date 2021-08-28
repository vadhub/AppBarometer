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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vad.appbarometer.screens.main.PresenterView;

public class GPSdata {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager mLocationManager;
    private PresenterView view;

    public GPSdata(FusedLocationProviderClient fusedLocationProviderClient, LocationManager mLocationManager, PresenterView view) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.mLocationManager = mLocationManager;
        this.view = view;
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
    }

    public LocationRequest getLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {

        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = null;
                    if(task.isSuccessful() && task.getResult()!=null){
                        location  = task.getResult();
                    }
                    if (location != null) {
                            view.response((float) location.getLatitude(),(float) location.getLongitude());
                    }else{
                        getLocationCallback();
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationCallback(){
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
