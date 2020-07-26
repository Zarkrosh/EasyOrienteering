package com.hergomsoft.easyorienteering.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.data.api.responses.CarrerasUsuarioResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;


import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarreraRepository extends ApiRepository {
    private MutableLiveData<Recurso<Carrera>> carreraResponse;
    private MutableLiveData<Recurso<CarrerasUsuarioResponse>> carrerasUsuarioResponse;

    public CarreraRepository() {
        carreraResponse = new MutableLiveData<>();
        carrerasUsuarioResponse = new MutableLiveData<>();
    }

    public LiveData<Recurso<Carrera>> getCarreraResponse() {
        return carreraResponse;
    }
    public LiveData<Recurso<CarrerasUsuarioResponse>> getCarrerasUsuarioResponse() { return carrerasUsuarioResponse; }

    public void getCarrera(long id) {
        apiClient.getCarrera(id).enqueue(new Callback<Carrera>() {
            @Override
            public void onResponse(Call<Carrera> call, Response<Carrera> response) {
                if(response.body() != null) {
                    carreraResponse.postValue(new Recurso<>(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Carrera> call, Throwable t) {
                carreraResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }

    public void getCarrerasUsuario() {
        apiClient.getCarrerasUsuario().enqueue(new Callback<CarrerasUsuarioResponse>() {
            @Override
            public void onResponse(Call<CarrerasUsuarioResponse> call, Response<CarrerasUsuarioResponse> response) {
                carrerasUsuarioResponse.postValue(new Recurso<>(response.body()));
            }

            @Override
            public void onFailure(Call<CarrerasUsuarioResponse> call, Throwable t) {
                carrerasUsuarioResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }
}
