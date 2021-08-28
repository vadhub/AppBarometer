package com.vad.appbarometer.retrofitzone;

import com.vad.appbarometer.pojos.WeatherPojo;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolder {
    @GET("data/2.5/weather")
    Observable<WeatherPojo> getData(@Query("lat") float lat, @Query("lon") float lon, @Query("appid") String appid);
}
