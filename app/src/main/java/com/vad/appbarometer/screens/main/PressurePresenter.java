package com.vad.appbarometer.screens.main;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vad.appbarometer.R;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.gps.GPSdata;
import com.vad.appbarometer.utils.requestcodes.RequestCodes;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PressurePresenter implements Response {
    private final GPSdata gps;
    private final PressureListener view;
    private final Activity activity;
    private final CompositeDisposable compositeDisposable;
    private final String key;

    public PressurePresenter(Activity activity, String key) {
        this.view = ((MainActivity) activity);
        this.activity = activity;
        this.key = key;
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        LocationManager mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        gps = new GPSdata(fusedLocationProviderClient, mLocationManager, this);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void toResponse(float lat, float lon) {

        if (view.isDataFromInternet()) {
            Disposable disposable = RetrofitClient.getInstance().getJsonApi().getData(lat, lon, key)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            weatherPojo -> {
                                view.setPressure(weatherPojo.getMain().getPressure());
                            },
                            throwable -> {
                                view.showError(throwable.getMessage());
                            });

            compositeDisposable.add(disposable);
        } else {
            view.showError(activity.getString(R.string.network_connection));
        }
    }

    public void displayLocationSettingsRequest() {
        GoogleApiClient apiClient = view.getGoogleApiClient();
        LocationRequest location = gps.getLocationRequest();
        apiClient.connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(location);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();

            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    setCoordinate();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(activity, RequestCodes.REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        view.showError(e.getMessage());
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    view.showError("GPS unable");
                    break;
            }
        });
    }

    public void disposableDispose() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public void setCoordinate() {
        gps.getLocation();
    }

}
