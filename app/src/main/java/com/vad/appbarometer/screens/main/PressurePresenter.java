package com.vad.appbarometer.screens.main;


import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vad.appbarometer.pojos.WeatherPojo;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.gps.GPSdata;
import com.vad.appbarometer.utils.pressure.PressureSensor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PressurePresenter {

    public static final String API_KEY ="e19089086c20c76bdc3bfbbe2a6ad29c";
    private GPSdata gps;
    private PressureSensor pressureSensor;
    private PressureView view;

    public PressurePresenter(PressureView view, GPSdata gps, PressureSensor pressureSensor) {
        this.view=view;
        this.gps=gps;
        this.pressureSensor=pressureSensor;
    }

    private void response(float lat, float lon) {
        RetrofitClient.getInstance().getJsonApi().getData(lat, lon, API_KEY).enqueue(new Callback<WeatherPojo>() {
            @Override
            public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                if (response.body() != null) {
                    float pressure = response.body().getMain().getPressure();
                    view.setStartPositionUnit(pressure);
                }
            }

            @Override
            public void onFailure(Call<WeatherPojo> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }

    public void checkSensor(){
        if(pressureSensor.getPressureSensor()==null){
            view.checkPermission();
        }else{
            view.setStartPositionUnit(pressureSensor.getValue());
        }
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
                        gps.getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        view.showDialog(status);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        view.showError("GPS unable");
                        break;
                }
            }
        });
    }


}
