package com.vad.appbarometer.utils;

import android.content.Context;
import android.widget.ImageView;

public class TypeImage {
    public void setPressureImageType(boolean isDarkTheme, Context context, ImageView imageViewGauge, int dark, int light) {
        if (isDarkTheme) {
            imageViewGauge.setImageDrawable(context.getDrawable(dark));
        } else {
            imageViewGauge.setImageDrawable(context.getDrawable(light));
        }
    }
}
