package com.vad.appbarometer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MathSets {

    public final static int STANDART_GRAD = 10;

    public static float getGradus(float pressure){
        float v = 0;
        float coef = 3;
        if(pressure>1000){
            v = (pressure - 1000) * coef;
        } else{
            v = -(1000-pressure)*coef;
        }
        return v;
    }


}
