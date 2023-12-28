package com.vad.appbarometer.screens.main;

import static com.vad.appbarometer.utils.requestcodes.RequestCodes.REQUEST_CHECK_SETTINGS;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.vad.appbarometer.R;
import com.vad.appbarometer.pojos.WeatherPojo;
import com.vad.appbarometer.retrofitzone.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PressurePresenter {

    private final PressureListener view;
    private final String key;
    private final FusedLocationProviderClient fusedLocationClient;

    public PressurePresenter(PressureListener view, FusedLocationProviderClient fusedLocationClient, String key) {
        this.view = view;
        this.fusedLocationClient = fusedLocationClient;
        this.key = key;
    }

    public void requestPressure(float lat, float lon) {

        if (view.isDataFromInternet()) {
            RetrofitClient.getInstance().getJsonApi().getData(lat, lon, key).enqueue(new Callback<WeatherPojo>() {
                        @Override
                        public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                            view.setPressure(response.body().getMain().getPressure());
                        }

                        @Override
                        public void onFailure(Call<WeatherPojo> call, Throwable t) {
                            view.showError(t.getMessage());
                        }
                    });
        } else {
            view.showError(view.getActivity().getString(R.string.network_connection));
        }
    }

    public void displayLocationSettingsRequest() {

        if (ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();
        SettingsClient settingsClient = LocationServices.getSettingsClient(view.getActivity());

        Task<LocationSettingsResponse> taskCheckLocationSettings = settingsClient.checkLocationSettings(
                locationSettingsRequestBuilder
                        .addLocationRequest(locationRequest)
                        .setAlwaysShow(true)
                        .build()
        );

        taskCheckLocationSettings.addOnFailureListener(view.getActivity(), e -> {
            if (e instanceof ResolvableApiException){
                try {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    resolvableApiException.startResolutionForResult(view.getActivity(),
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                    Toast.makeText(view.getActivity(), view.getActivity().getString(R.string.gps_not_available), Toast.LENGTH_SHORT).show();
                }
            }
        });

        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = null;
            if (task.isSuccessful() && task.getResult() != null) {
                location = task.getResult();
                requestPressure((float) location.getLatitude(), (float) location.getLongitude());
            }

            if (location == null) {
                LocationCallback callback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location loc = locationResult.getLastLocation();
                        requestPressure((float) loc.getLatitude(), (float) loc.getLongitude());
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                };
                fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(view.getActivity(), view.getActivity().getString(R.string.gps_not_available) + " callback", Toast.LENGTH_SHORT).show();
        });

    }

}
