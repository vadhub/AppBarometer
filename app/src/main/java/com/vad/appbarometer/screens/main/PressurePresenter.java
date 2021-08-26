package com.vad.appbarometer.screens.main;


import android.content.Context;
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
import com.vad.appbarometer.utils.pressure.PressureSensor;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PressurePresenter {

    public static final String API_KEY ="e19089086c20c76bdc3bfbbe2a6ad29c";
    private GPSdata gps;
    private PressureSensor pressureSensor;
    private PressureView view;
    private Context context;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager mLocationManager;
    private Geocoder geocoder;

    public PressurePresenter(PressureView view, Context context) {
        this.view=view;
        this.context = context;
        pressureSensor = new PressureSensor(context);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(context, Locale.getDefault());
        gps = new GPSdata(fusedLocationProviderClient, mLocationManager, geocoder);
    }

    public void response(float lat, float lon) {
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
            System.out.println("sensor does not");
            view.checkPermission();
        }else{
            view.setStartPositionUnit(pressureSensor.getValue());
            System.out.println("sensor++"+pressureSensor.getValue()+" "+pressureSensor);
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
                        setCoordinate();
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

    public void setCoordinate(){
        float[] i = gps.getLocation();
        response(i[0], i[1]);
    }


}
