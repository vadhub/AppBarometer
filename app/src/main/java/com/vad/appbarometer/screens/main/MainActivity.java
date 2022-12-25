

package com.vad.appbarometer.screens.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
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
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;

public class MainActivity extends AppCompatActivity implements PressureListener, SensorEventListener {

    private TextView mBarText;
    private ImageView imageViewArrow;
    private ImageView imageViewGauge;
    private ProgressBar progressBar;

    private int isHg = 0;
    private static float pressure = 0;

    private SaveState saveState;
    private PressurePresenter presenter;

    private Sensor mPressure;
    private SensorManager mSensorManage;

    private boolean isDarkTheme = false;
    private UnitPressure unitPressure;
    private TypeImage typeImage;

    private boolean isActive = false;

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

        BannerAdView mBanner = (BannerAdView) findViewById(R.id.adView);
        mBanner.setAdUnitId("R-M-1980164-1");
        mBanner.setAdSize(AdSize.stickySize(AdSize.FULL_SCREEN.getWidth()));
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
                ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                presenter = new PressurePresenter(this, applicationInfo.metaData.getString("keyValue"));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            checkPermission();

            textViewIndicator.setText(getResources().getText(R.string.indicateInternet));
        }

        isHg = saveState.getStatePres();
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
        unitPressure.setUnit(type,
                () -> {
                    //hgp
                    visionPressure(pressure, "hPa");
                    typeImage.setPressureImageType(isDarkTheme, this, imageViewGauge, R.drawable.gaugehgpdark, R.drawable.gaugehgp);
                },
                () -> {
                    //mmg
                    visionPressure(MathSets.convertToMmHg(pressure), "mmHg");
                    typeImage.setPressureImageType(isDarkTheme, this, imageViewGauge, R.drawable.gaugemmhgdark, R.drawable.gaugemmhg);
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
    public GoogleApiClient getGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
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
                isHg = 1;
                break;
            case R.id.hpa:
                isHg = 0;
                break;
        }

        setGauge(isHg, pressure);

        return true;
    }

    private void startAboutActivity() {
        Intent aboutActivity = new Intent(MainActivity.this, AboutAppActivity.class);
        startActivity(aboutActivity);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveState.saveStatePres(isHg);
        mSensorManage.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.disposableDispose();
        }
        mSensorManage.unregisterListener(this);
    }
}