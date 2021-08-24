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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import com.vad.appbarometer.utils.gps.GPSdata;
import com.vad.appbarometer.utils.math.MathSets;
import com.vad.appbarometer.utils.savestateunit.SaveState;

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

    private Spinner spinnerBar;
    private String changMBar;
    private float sensorValue;
    private int isHg = 0;

    private AdView mAdView;
    private String[] barChange;
    private SaveState saveState;
    private PressurePresenter presenter;
    private GPSdata gps;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager mLocationManager;
    private Geocoder geocoder;

    private boolean isActive = false;

    private void checkPermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RequestCodes.REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCodes.REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
        } else {
            displayLocationSettingsRequest(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodes.REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION) {
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(MainActivity.this,Locale.getDefault());

        gps = new GPSdata(fusedLocationProviderClient, mLocationManager, geocoder);
        saveState = new SaveState(this);
        presenter = new PressurePresenter(this, gps);

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

        if(saveState.getStatePres()==0){
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
        saveState.saveStatePres(isHg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RequestCodes.REQUEST_CHECK_SETTINGS){
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

    @Override
    public void showDialog(Status status) {
        try {
            // Show the dialog by calling startResolutionForResult(), and check the result
            // in onActivityResult().
            status.startResolutionForResult(this, RequestCodes.REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {

            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}