package com.vad.appbarometer.retrofitzone;

import com.vad.appbarometer.pojos.reserve.PressurePojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReserveServiceWeather {
    @GET("v1/current.json")
    Call<PressurePojo> getData(@Query("key") String key, @Query("q") String q);
}
