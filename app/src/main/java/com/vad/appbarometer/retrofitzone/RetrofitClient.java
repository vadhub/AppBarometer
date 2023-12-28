package com.vad.appbarometer.retrofitzone;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit2;
    private static final String URL_BASE = "http://api.openweathermap.org/";
    private static final String URL_RESERVE = "https://api.weatherapi.com/";
    
    private static RetrofitClient retrofitClient;

    private RetrofitClient(String URL) {
        retrofit2 = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(URL).build();
    }

    public static RetrofitClient getBaseInstance(boolean reserve) {

        String url = URL_BASE;

        if (reserve) {
            retrofit2 = null;
            retrofitClient = null;
            url = URL_RESERVE;
        }

        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient(url);
        }
        return retrofitClient;
    }

    public JsonPlaceHolder getJsonApi() {
        return retrofit2.create(JsonPlaceHolder.class);
    }

    public ReserveServiceWeather getReserveApi() {
        return retrofit2.create(ReserveServiceWeather.class);
    }
}
