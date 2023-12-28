package com.vad.appbarometer.retrofitzone;

import com.vad.appbarometer.pojos.base.WeatherPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolder {
    @GET("data/2.5/weather")
    Call<WeatherPojo> getData(@Query("lat") float lat, @Query("lon") float lon, @Query("appid") String appid);
}
