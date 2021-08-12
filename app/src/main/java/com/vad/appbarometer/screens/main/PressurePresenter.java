package com.vad.appbarometer.screens.main;

import android.location.Address;
import android.os.AsyncTask;
import android.widget.Toast;

import com.vad.appbarometer.pojos.WeatherPojo;
import com.vad.appbarometer.retrofitzone.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PressurePresenter {

    public static final String API_KEY ="e19089086c20c76bdc3bfbbe2a6ad29c";

    private PressureView view;
    public PressurePresenter(PressureView view) {
        this.view=view;
    }

    private void response(float lat, float lon) {
        RetrofitClient.getInstance().getJsonApi().getData(lat, lon, API_KEY).enqueue(new Callback<WeatherPojo>() {
            @Override
            public void onResponse(Call<WeatherPojo> call, Response<WeatherPojo> response) {
                if (response.body() != null) {
                    float pressure = response.body().getMain().getPressure();
                    view.setStartPositionUnit(pressure);
                }
            }

            @Override
            public void onFailure(Call<WeatherPojo> call, Throwable t) {
                view.showError(t.getMessage());
            }
        });
    }

    class GSPQueryTask extends AsyncTask<Void, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

        }
    }
}
