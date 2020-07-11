package com.hergomsoft.easyorienteering.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.data.api.ApiClient;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.pagers.RegistroControl;
import com.hergomsoft.easyorienteering.util.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroControlRepository {

    private ApiClient apiClient;
    private MutableLiveData<RegistroControl> registroResponse;

    public RegistroControlRepository() {
        registroResponse = new MutableLiveData<>();

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

    public void registraControl(String codigo, long idCarrera, long idRecorrido,  String secreto) {
        RegistroRequest request = new RegistroRequest(codigo, secreto, Constants.ID_USUARIO_PRUEBA, idRecorrido);
        apiClient.registraControl(idCarrera, request).enqueue(new Callback<RegistroControl>() {
            @Override
            public void onResponse(Call<RegistroControl> call, Response<RegistroControl> response) {
                if(response.body() != null) {
                    registroResponse.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<RegistroControl> call, Throwable t) {
                // TODO Manejar error de código 422
                registroResponse.postValue(null);
            }
        });
    }

    public LiveData<RegistroControl> getRegistroResponse() {
        return registroResponse;
    }
}
