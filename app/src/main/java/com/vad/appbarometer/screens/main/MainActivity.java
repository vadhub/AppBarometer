package com.vad.appbarometer.screens.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationSet;

import android.widget.AdapterView;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vad.appbarometer.R;
import com.vad.appbarometer.pojos.WeatherPojo;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.animation.AnimationSets;
import com.vad.appbarometer.utils.math.MathSets;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vad.appbarometer.R.drawable.guage;

public class MainActivity extends AppCompatActivity implements PressureView{

    private TextView mBarText;
    private ImageView imageViewArrow;
    private ImageView imageViewGauge;
    private ProgressBar progressBar;
    private LocationManager mLocationManager;
    private Spinner spinnerBar;
    private String changMBar;
    private float sensorValue;
    private SharedPreferences prefer;
    private int isHg = 0;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private AdView mAdView;
    private String[] barChange;

    private boolean isActive = false;

    public static final int REQUEST_CHECK_SETTINGS = 12091;
    private static final int REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION = 10431;

    private void checkPermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
        } else {
            displayLocationSettingsRequest(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayLocationSettingsRequest(this);
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

        spinnerBar = (Spinner) findViewById(R.id.spinnerChangeMeter);

        //adapter for spinner
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.bar, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBar.setAdapter(adapter);

        mBarText = (TextView) findViewById(R.id.mBarText);


        barChange = getResources().getStringArray(R.array.bar);

        imageViewArrow = (ImageView) findViewById(R.id.imageViewArrow);
        imageViewGauge = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //update data arrow and gauge
        activeGauge(imageViewArrow, true);
        activeGauge(imageViewGauge, false);

        if (pressureSensor == null) {
            checkPermission();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        if(getStatePres()==0){
            spinnerBar.setSelection(0);
            changMBar = barChange[0];
        }else{
            spinnerBar.setSelection(1);
            changMBar = barChange[1];
        }

        spinnerBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changMBar = barChange[i];
                if (i == 0) {
                    visionPreasure(sensorValue);
                    imageViewGauge.setImageDrawable(getDrawable(guage));

                } else {
                    visionPreasure(MathSets.convertToMmHg(sensorValue));
                    imageViewGauge.setImageDrawable(getDrawable(R.drawable.gaugehg));
                }
                isHg=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void visionPreasure(float pres) {
        mBarText.setText(String.format("%.2f " + changMBar, pres));
        AnimationSets aset = new AnimationSets();

        AnimationSet animationSet = aset.animationRotate(MathSets.getGradus(pres));
        imageViewArrow.startAnimation(animationSet);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveStatePres(isHg);
    }

    //LOCATION GET
    @SuppressLint("MissingPermission")
    private void getLocation() {
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = null;
                    if(task.isSuccessful() && task.getResult()!=null){
                        location  = task.getResult();
                    }

                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            response((float) addressList.get(0).getLatitude(), (float) addressList.get(0).getLongitude());
                            setVisibleState();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location = locationResult.getLastLocation();
                                response((float) location.getLatitude(), (float) location.getLongitude());
                                setVisibleState();
                                fusedLocationProviderClient.removeLocationUpdates(this);
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(getLocationRequest(), locationCallback, Looper.myLooper());
                        if(sensorValue!=0){
                            fusedLocationProviderClient=null;
                        }
                    }
                }
            });
        }else{
            checkPermission();
        }
    }


    private void displayLocationSettingsRequest(Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(getLocationRequest());
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(activity, "GPS unable", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CHECK_SETTINGS){
            getLocation();
        }
    }

    //set alpha image
    private void activeGauge(ImageView imageView, boolean isArrow){
        if(isActive){
            imageView.setImageAlpha(255);
        }else{
            if(isArrow){
                imageView.setImageAlpha(0);
            }else{
                imageView.setImageAlpha(100);
            }
        }
    }

    //set pressure from sensorEvent
    private void setPressure(float values){
        sensorValue= values;
        progressBar.setVisibility(View.INVISIBLE);
        isActive = true;
        activeGauge(imageViewGauge, false);
        activeGauge(imageViewArrow, true);
    }

    //visible guage and arrow
    private void setVisibleState(){
        progressBar.setVisibility(View.INVISIBLE);
        isActive = true;
        activeGauge(imageViewGauge, false);
        activeGauge(imageViewArrow, true);
    }

    //save state hpa or mmhg
    private void saveStatePres(int type){
        prefer = getSharedPreferences("pressure_state_app", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putInt("type_pressure_", type);
        ed.apply();
    }

    //get state hpa or mmhg
    private int getStatePres(){
        prefer = getSharedPreferences("pressure_state_app", MODE_PRIVATE);
        return prefer.getInt("type_pressure_", 0);
    }

    @Override
    public void setStartPositionUnit(float value){
        if(changMBar.equals(barChange[0])){
            imageViewGauge.setImageDrawable(getDrawable(guage));
            visionPreasure(value);
        }else{
            imageViewGauge.setImageDrawable(getDrawable(R.drawable.gaugehg));
            visionPreasure(MathSets.convertToMmHg(value));
        }
    }

    @Override
    public void showError(String str) {
        Toast.makeText(this, ""+str, Toast.LENGTH_SHORT).show();

    }

}