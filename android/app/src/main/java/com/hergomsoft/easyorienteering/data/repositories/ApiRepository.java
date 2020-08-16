package com.hergomsoft.easyorienteering.data.repositories;

import androidx.lifecycle.LiveData;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hergomsoft.easyorienteering.data.api.ApiClient;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.util.LiveDataCallAdapterFactory;
import com.hergomsoft.easyorienteering.util.Resource;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class ApiRepository {
    protected final ApiClient apiClient;

    public ApiRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        apiClient = new retrofit2.Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .client(client)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient.class);
    }

    /**
     * Devuelve un recurso con el error de conexión asociado (en cadena de texto).
     * @param t Excepción del error
     * @return Recurso con el error
     */
    protected Recurso getRecursoConErrorConexion(Throwable t) {
        Recurso recurso = new Recurso<>();
        if(t.getCause() instanceof ConnectException) {
            recurso.setError("No hay conexión con el servidor");
        } else if(t.getCause() instanceof SocketTimeoutException) {
            recurso.setError("No se ha podido contactar con el servidor (tiempo expirado)");
        } else {
            // TODO Manejar más errores concretos
            recurso.setError("Error de conexión desconocido");
        }

        return recurso;
    }

}