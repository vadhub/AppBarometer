package com.vad.appbarometer.utils.gps;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
                    System.out.println("GPS -------------------------");
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

//    private Observable<List<Address>> getCoordinate(Location location){
//        List<Address> addressList = new ArrayList<>();
//        System.out.println("getcoordinate--------------");
//         return Observable
//                .just(addressList)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                 .map(addresses -> geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1));
//    }

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
