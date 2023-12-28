package com.vad.appbarometer.pojos.reserve;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PressurePojo {

    @SerializedName("current")
    @Expose
    private Current current;

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

}

