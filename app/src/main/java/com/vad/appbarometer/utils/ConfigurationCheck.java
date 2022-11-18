package com.vad.appbarometer.utils;

import android.content.Context;
import android.content.res.Configuration;

public class ConfigurationCheck {
    public boolean checkDarkTheme(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
