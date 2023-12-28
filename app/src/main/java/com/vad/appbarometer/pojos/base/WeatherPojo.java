package com.vad.appbarometer.pojos.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vad.appbarometer.pojos.base.Main;

public class WeatherPojo {

    @Expose
    private Main main;

    @SerializedName("name")
    @Expose
    private String name;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
