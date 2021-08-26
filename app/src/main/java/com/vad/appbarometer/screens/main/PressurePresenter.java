package com.vad.appbarometer.screens.main;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.LocationManager;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vad.appbarometer.pojos.WeatherPojo;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.gps.GPSdata;

import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PressurePresenter {

    public static final String API_KEY ="e19089086c20c76bdc3bfbbe2a6ad29c";
    private GPSdata gps;
    private PressureView view;
    private Activity activity;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager mLocationManager;
    private Geocoder geocoder;

    public PressurePresenter(PressureView view, Activity activity) {
        this.view=view;
        this.activity = activity;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(activity, Locale.getDefault());
        gps = new GPSdata(fusedLocationProviderClient, mLocationManager, geocoder);
    }

    public void response(float lat, float lon) {
        RetrofitClient.getInstance().getJsonApi().getData(lat, lon, API_KEY).enqueue(new Callback<WeatherPojo>() {
            @Override
            public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                if (response.body() != null) {
                    float pressure = response.body().getMain().getPressure();
                    view.setPressure(pressure);
                }
            }

            @Override
            public void onFailure(Call<WeatherPojo> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }

    public void displayLocationSettingsRequest() {
        view.getGoogleApiClient().connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(gps.getLocationRequest());
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(view.getGoogleApiClient(), builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        setCoordinate();
                        System.out.println("1 setCoor");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            System.out.println("2 show dialog");
                            status.startResolutionForResult(activity, RequestCodes.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            view.showError(e.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        System.out.println("3 not ");
                        view.showError("GPS unable");
                        break;
                }
            }
        });
    }

    public void setCoordinate(){
        float[] i = gps.getLocation();
        System.out.println(Arrays.toString(i)+"---------------");
        response(i[0], i[1]);
    }


}
