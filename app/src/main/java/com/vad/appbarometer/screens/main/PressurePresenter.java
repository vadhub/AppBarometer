package com.vad.appbarometer.screens.main;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApi;
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
import com.vad.appbarometer.R;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.gps.GPSdata;
import com.vad.appbarometer.utils.requestcodes.RequestCodes;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PressurePresenter implements PresenterView {

    private final PressureView view;
    private final Activity activity;
    private final CompositeDisposable compositeDisposable;
    private final String key;

    public PressurePresenter(PressureView view, Activity activity, String key) {
        this.view = view;
        this.activity = activity;
        this.key = key;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void response(float lat, float lon) {

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

    public void disposableDispose() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }


}
