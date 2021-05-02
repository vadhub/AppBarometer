package com.vad.appbarometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vad.appbarometer.pojos.WeatherPojo;
import com.vad.appbarometer.retrofitzone.RetrofitClient;
import com.vad.appbarometer.utils.MathSets;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mBarText;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private final static int REQUEST_CODE = 445;
    private ImageView imageViewArrow;
    private float tempPressure;

    private float lat;
    private float lon;

    public static final String API_KEY = "e19089086c20c76bdc3bfbbe2a6ad29c";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            tempPressure = values[0];
            mBarText.setText(String.format("%.2f mBar", tempPressure));
            imageViewArrow.setRotation(MathSets.getGradus(tempPressure));

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getGPSdata();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    private void getGPSdata() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null){
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        lat = (float) addresses.get(0).getLatitude();
                        lon = (float) addresses.get(0).getLongitude();
                        response();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBarText = (TextView) findViewById(R.id.mBarText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        imageViewArrow = (ImageView) findViewById(R.id.imageViewArrow);
        checkPermission();


    }

    private void response(){
        System.out.println(lat+" "+lon);
            RetrofitClient.getInstance().getJsonApi().getData(lat, lon, API_KEY).enqueue(new Callback<WeatherPojo>() {
                @Override
                public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                    if(response.body()!=null){
                        float pressure = response.body().getMain().getPressure();
                        System.out.println(pressure);
                    }
                }

                @Override
                public void onFailure(Call<WeatherPojo> call, Throwable t) {
                    Toast.makeText(MainActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(t.getMessage());
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pressureSensor != null)
            sensorManager.registerListener(sensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}