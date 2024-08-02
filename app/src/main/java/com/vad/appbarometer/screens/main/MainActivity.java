
package com.vad.appbarometer.screens.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.vad.appbarometer.R;
import com.vad.appbarometer.screens.aboutapp.AboutAppActivity;
import com.vad.appbarometer.utils.ConfigurationCheck;
import com.vad.appbarometer.utils.TypeImage;
import com.vad.appbarometer.utils.UnitPressure;
import com.vad.appbarometer.utils.animation.AnimationSets;
import com.vad.appbarometer.utils.math.MathSets;
import com.vad.appbarometer.utils.requestcodes.RequestCodes;
import com.vad.appbarometer.utils.savestateunit.SaveState;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;

public class MainActivity extends AppCompatActivity implements PressureListener, SensorEventListener {

    private TextView mBarText;
    private ImageView imageViewArrow;
    private ImageView imageViewGauge;
    private ProgressBar progressBar;

    private BannerAdView mBanner;

    private int typeUnitPressure = 0;
    private static float pressure = 0;

    private SaveState saveState;
    private PressurePresenter presenter;

    private FusedLocationProviderClient fusedLocationClient;

    private Sensor mPressure;
    private SensorManager mSensorManage;

    private boolean isDarkTheme = false;
    private UnitPressure unitPressure;
    private TypeImage typeImage;

    private boolean isActive = false;

    private ApplicationInfo applicationInfo;

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RequestCodes.REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RequestCodes.REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION);
        } else {
            presenter.displayLocationSettingsRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RequestCodes.REQUEST_CODE_PERMISSION_OVERLAY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.displayLocationSettingsRequest();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        isDarkTheme = new ConfigurationCheck().checkDarkTheme(this);
        typeImage = new TypeImage();

        mBanner = (BannerAdView) findViewById(R.id.adView);
        mBanner.setAdUnitId("R-M-1980164-1");
        mBanner.setAdSize(getAdSize());
        AdRequest adRequest = new AdRequest.Builder().build();
        mBanner.loadAd(adRequest);

        mSensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);
        mPressure = mSensorManage.getDefaultSensor(Sensor.TYPE_PRESSURE);
        saveState = new SaveState(this);
        unitPressure = new UnitPressure();

        mBarText = findViewById(R.id.mBarText);

        imageViewArrow = findViewById(R.id.imageViewArrow);
        imageViewGauge = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        TextView textViewIndicator = findViewById(R.id.textViewIndicator);

        //update data arrow and gauge
        activeGauge(imageViewArrow, true);
        activeGauge(imageViewGauge, false);

        textViewIndicator.setText(getResources().getText(R.string.indicateSensor));
        if (mPressure == null) {
            try {
                applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                presenter = new PressurePresenter(this, fusedLocationClient);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            checkPermission();

            textViewIndicator.setText(getResources().getText(R.string.indicateInternet));
        }

        typeUnitPressure = saveState.getStatePres();
    }

    private BannerAdSize getAdSize() {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // Calculate the width of the ad, taking into account the padding in the ad container.
        int adWidthPixels = mBanner.getWidth();
        if (adWidthPixels == 0) {
            // If the ad hasn't been laid out, default to the full screen width
            adWidthPixels = displayMetrics.widthPixels;
        }
        final int adWidth = Math.round(adWidthPixels / displayMetrics.density);

        return BannerAdSize.stickySize(this, adWidth);
    }

    private void visionPressure(float pres, String changMBar) {
        mBarText.setText(String.format("%.2f " + changMBar, pres));
        AnimationSets aset = new AnimationSets();
        AnimationSet animationSet = aset.animationRotate(MathSets.getGradus(pres));
        imageViewArrow.startAnimation(animationSet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManage.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.REQUEST_CHECK_SETTINGS) {
            checkPermission();
        }
    }

    //set alpha image
    private void activeGauge(ImageView imageView, boolean isArrow) {
        if (isActive) {
            imageView.setImageAlpha(255);
        } else {
            if (isArrow) {
                imageView.setImageAlpha(0);
            } else {
                imageView.setImageAlpha(100);
            }
        }
    }

    //visible gauge and arrow
    private void setVisibleState() {
        progressBar.setVisibility(View.INVISIBLE);
        isActive = true;
        activeGauge(imageViewGauge, false);
        activeGauge(imageViewArrow, true);
    }

    private void setGauge(int type, float pressure) {
        int mbDay;
        int mbDark;

        int hpDay;
        int hpDark;

        if (pressure < 960) {
            mbDay = R.drawable.mb_600_680;
            mbDark = R.drawable.mb_600_680_night;

            hpDay = R.drawable.hp_day_840_920;
            hpDark = R.drawable.hp_night_840_920;
        } else {
            mbDay = R.drawable.gaugemmhg;
            mbDark = R.drawable.gaugemmhgdark;

            hpDay = R.drawable.gaugehgp;
            hpDark = R.drawable.gaugehgpdark;
        }

        unitPressure.setUnit(type,
                () -> {
                    //hgp
                    visionPressure(pressure, "hPa");
                    typeImage.setPressureImageType(isDarkTheme, this, imageViewGauge, hpDark, hpDay);
                },
                () -> {
                    //mmg
                    visionPressure(MathSets.convertToMmHg(pressure), "mmHg");
                    typeImage.setPressureImageType(isDarkTheme, this, imageViewGauge, mbDark, mbDay);
                },
                () -> {
                    //mBar
                    visionPressure(pressure, "mBar");
                    typeImage.setPressureImageType(isDarkTheme, this, imageViewGauge, hpDark, hpDay);
                });
    }

    @Override
    public void setPressure(float value) {
        pressure = value;
        setGauge(saveState.getStatePres(), pressure);
        setVisibleState();
    }

    @Override
    public void showError(String str) {
        Toast.makeText(this, str + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean isDataFromInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public String getKey(boolean reserve) {

        String key = applicationInfo.metaData.getString("keyValue");

        if (reserve) {
            key = applicationInfo.metaData.getString("keyValueReserve");
        }
        return key;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        pressure = sensorEvent.values[0];
        setGauge(saveState.getStatePres(), pressure);
        mSensorManage.unregisterListener(this);
        setVisibleState();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_app_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.about_button:
                startAboutActivity();
                break;
            case R.id.mmbar:
                typeUnitPressure = UnitPressure.mmHG;
                break;
            case R.id.hpa:
                typeUnitPressure = UnitPressure.hPA;
                break;
            case R.id.mbar:
                typeUnitPressure = UnitPressure.mBAR;
                break;
        }

        setGauge(typeUnitPressure, pressure);

        return true;
    }

    private void startAboutActivity() {
        Intent aboutActivity = new Intent(MainActivity.this, AboutAppActivity.class);
        startActivity(aboutActivity);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveState.saveStatePres(typeUnitPressure);
        mSensorManage.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManage.unregisterListener(this);
    }
}