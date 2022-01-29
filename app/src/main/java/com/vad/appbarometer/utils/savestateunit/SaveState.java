package com.vad.appbarometer.utils.savestateunit;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

public class SaveState {

    private SharedPreferences prefer;
    private Context context;

    public SaveState(Context context) {
        this.context = context;
    }

    //save state
    // 0 = hPa
    // 1 = mmHg
    public void saveStatePres(int type) {
        prefer = context.getSharedPreferences("pressure_state_app", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefer.edit();
        ed.putInt("type_pressure_", type);
        ed.apply();
    }

    //get state hpa or mmHg
    public int getStatePres() {
        prefer = context.getSharedPreferences("pressure_state_app", MODE_PRIVATE);
        return prefer.getInt("type_pressure_", 0);
    }
}
