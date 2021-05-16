package com.vad.appbarometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vad.appbarometer.pojos.WeatherPojo;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.MathSets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView mBarText;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private ImageView imageViewArrow;
    private ImageView imageViewGauge;
    private ProgressBar progressBar;
    private LocationManager mlocationManager;
    private String[] barChange;

    private Spinner spinnerBar;
    private String changMBar;
    private ProgressBar progressBarForTextView;

    private AdView mAdView;

    private boolean isActive = false;

    public static final int REQUEST_CHECK_SETTINGS = 1209;
    public static final String API_KEY = "e19089086c20c76bdc3bfbbe2a6ad29c";
    private static final int REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION = 1043;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float[] values = sensorEvent.values;
                            visionPreasure(values[0]);
                            progressBar.setVisibility(View.INVISIBLE);
                            progressBarForTextView.setVisibility(View.INVISIBLE);
                            isActive = true;
                            activeGauge(imageViewGauge);
                            activeGauge(imageViewArrow);

                            sensorManager.unregisterListener(sensorEventListener);
                            sensorEventListener=null;
                        }
                    });

                }
            }).start();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    private void checkPermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)&&(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.permission_name)).setMessage(getResources().getString(R.string.permission_message_body))
                    .setPositiveButton(getResources().getString(R.string.permission_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
                        }
                    }).setNegativeButton(getResources().getString(R.string.permission_deny), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }else{
            displayLocationSettingsRequest(this, MainActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayLocationSettingsRequest(this, MainActivity.this);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        barChange = getResources().getStringArray(R.array.bar);
        spinnerBar = (Spinner) findViewById(R.id.spinnerChangeMeter);

        //adapter for spinner
        ArrayAdapter<?> adapter =ArrayAdapter.createFromResource(this, R.array.bar, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBar.setAdapter(adapter);
        mBarText = (TextView) findViewById(R.id.mBarText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        pressureSensor = null;//sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        imageViewArrow = (ImageView) findViewById(R.id.imageViewArrow);
        imageViewGauge = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarForTextView = (ProgressBar) findViewById(R.id.progressBarForTextView);

        //update data arrow and gauge
        activeGauge(imageViewArrow);
        activeGauge(imageViewGauge);

        if(pressureSensor==null){
            checkPermission();
        }

    }

    private void visionPreasure(float pres){
        mBarText.setText(String.format("%.2f "+changMBar, pres));
        AnimationSet animationSet = animationRotate(MathSets.getGradus(pres));
        imageViewArrow.startAnimation(animationSet);
    }

    private void response(float lat, float lon){
            RetrofitClient.getInstance().getJsonApi().getData(lat, lon, API_KEY).enqueue(new Callback<WeatherPojo>() {
                @Override
                public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                    if(response.body()!=null){
                        float pressure = response.body().getMain().getPressure();
                        visionPreasure(pressure);
                    }
                }

                @Override
                public void onFailure(Call<WeatherPojo> call, Throwable t) {
                    Toast.makeText(MainActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (pressureSensor != null){
            sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    response((float) location.getLatitude(),(float) location.getLongitude());
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBarForTextView.setVisibility(View.INVISIBLE);
                    isActive = true;
                    activeGauge(imageViewGauge);
                    activeGauge(imageViewArrow);
                }
            }).start();

        }

        if(mlocationManager!=null){
            mlocationManager.removeUpdates(this);
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private void displayLocationSettingsRequest(Context context, Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        setLocationSetting();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(context, "GPS unable", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CHECK_SETTINGS){
            setLocationSetting();
        }
    }

    @SuppressLint("MissingPermission")
    private void setLocationSetting(){
        mlocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
    }

    private void activeGauge(ImageView imageView){
        if(isActive){
            imageView.setImageAlpha(255);
        }else{
            imageView.setImageAlpha(100);
        }
    }

    private AnimationSet animationRotate(float degrees){
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(0.0f, degrees,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(1500);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        return animSet;
    }
}