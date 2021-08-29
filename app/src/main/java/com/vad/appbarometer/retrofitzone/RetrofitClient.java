package com.vad.appbarometer.retrofitzone;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit2;
    private static final String URL_BASE = "http://api.openweathermap.org/";
    private static RetrofitClient retrofitClient;

    private RetrofitClient(){
        retrofit2 = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(URL_BASE).build();
    }

    public static RetrofitClient getInstance(){
        if(retrofitClient==null){
            retrofitClient = new RetrofitClient();
        }
        return retrofitClient;
    }

    public JsonPlaceHolder getJsonApi(){
        return retrofit2.create(JsonPlaceHolder.class);
    }
}
