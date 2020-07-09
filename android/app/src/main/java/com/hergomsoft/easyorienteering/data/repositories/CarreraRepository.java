package com.hergomsoft.easyorienteering.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.data.api.ApiClient;
import com.hergomsoft.easyorienteering.data.model.Carrera;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarreraRepository {

    private ApiClient apiClient;
    private MutableLiveData<Carrera> carreraResponse;

    public CarreraRepository() {
        carreraResponse = new MutableLiveData<>();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        apiClient = new retrofit2.Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient.class);
    }

    public void buscaCarreraPorId(long id) {
        apiClient.getCarrera(id).enqueue(new Callback<Carrera>() {
            @Override
            public void onResponse(Call<Carrera> call, Response<Carrera> response) {
                if(response.body() != null) {
                    carreraResponse.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Carrera> call, Throwable t) {
                carreraResponse.postValue(null);
            }
        });
    }

    public LiveData<Carrera> getCarreraResponse() {
        return carreraResponse;
    }
}
