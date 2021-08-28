package com.vad.appbarometer.screens.main;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.gps.GPSdata;
import com.vad.appbarometer.utils.requestcodes.RequestCodes;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PressurePresenter implements PresenterView{

    public static final String API_KEY ="e19089086c20c76bdc3bfbbe2a6ad29c";
    private final GPSdata gps;
    private final PressureView view;
    private final Activity activity;
    private Disposable disposable;
    private CompositeDisposable compositeDisposable;

    public PressurePresenter(PressureView view, Activity activity) {
        this.view=view;
        this.activity = activity;
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        LocationManager mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        gps = new GPSdata(fusedLocationProviderClient, mLocationManager, this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void response(float lat, float lon) {
        System.out.println(lat+" "+lon+"--------------------");

        disposable =RetrofitClient.getInstance().getJsonApi().getData(lat, lon, API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).
                    subscribe(
                            weatherPojo -> {
                                view.setPressure(weatherPojo.getMain().getPressure());
                                },
                            throwable -> {
                                view.showError(throwable.getMessage());
                            });
        compositeDisposable.add(disposable);
    }

    public void displayLocationSettingsRequest() {
        GoogleApiClient apiClient = view.getGoogleApiClient();
        LocationRequest location = gps.getLocationRequest();
        apiClient.connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(location);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        setCoordinate();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, RequestCodes.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            view.showError(e.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        view.showError("GPS unable");
                        break;
                }
            }
        });
    }

    public void disposableDispose(){
        if(compositeDisposable!=null){
            compositeDisposable.dispose();
        }
    }

    public void setCoordinate(){
        gps.getLocation();
    }


}
