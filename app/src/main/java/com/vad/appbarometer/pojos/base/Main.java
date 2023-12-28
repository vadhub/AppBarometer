package com.vad.appbarometer.pojos.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("pressure")
    @Expose
    private Integer pressure;

    public Integer getPressure() {
        return pressure;
    }

}
