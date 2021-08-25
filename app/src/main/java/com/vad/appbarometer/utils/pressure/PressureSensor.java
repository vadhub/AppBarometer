package com.vad.appbarometer.utils.pressure;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PressureSensor implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Context context;

    public PressureSensor(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public Sensor getPressureSensor() {
        return pressureSensor;
    }

    public void setPressureSensor(Sensor pressureSensor) {
        this.pressureSensor = pressureSensor;
    }
}
