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
import retrofit2.http.HTTP;

public class RegistroControlRepository {

    private ApiClient apiClient;
    private MutableLiveData<RegistroControl> registroResponse;
    private MutableLiveData<String> registroResponseError;

    public RegistroControlRepository() {
        registroResponse = new MutableLiveData<>();
        registroResponseError = new MutableLiveData<>();

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
                if(response.isSuccessful()) {
                    registroResponse.postValue(response.body());
                } else if(response.code() == 422) {
                    // Error lanzado en caso de algún error. Contiene un código de error
                    registroResponseError.postValue(response.message());
                } else if(response.code() == 404) {
                    // No existe la carrera con el ID indicado
                    registroResponseError.postValue("No existe la carrera con el ID indicado");
                } else {
                    // Error desconocido
                    registroResponseError.postValue("Error inesperado");
                }
            }

            @Override
            public void onFailure(Call<RegistroControl> call, Throwable t) {
                // TODO Manejar algún error en concreto (sin internet, etc)
                registroResponse.postValue(null);
            }
        });
    }

    public LiveData<RegistroControl> getRegistroResponse() {
        return registroResponse;
    }

    public MutableLiveData<String> getRegistroResponseError() {
        return registroResponseError;
    }
}
