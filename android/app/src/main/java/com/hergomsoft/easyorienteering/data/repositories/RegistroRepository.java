package com.hergomsoft.easyorienteering.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.api.ApiClient;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.pagers.RegistroControl;
import com.hergomsoft.easyorienteering.util.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.HTTP;

public class RegistroRepository {

    private ApiClient apiClient;
    private MutableLiveData<RegistroControl> registroResponse; // Respuesta de registro de control
    private MutableLiveData<String> registroResponseError;     // Respuesta de registro de control (error)
    private MutableLiveData<Recurso<Boolean>> comprobacionPendiente;    // Respuesta de comprobación de recorrido pendiente

    public RegistroRepository() {
        registroResponse = new MutableLiveData<>();
        registroResponseError = new MutableLiveData<>();
        comprobacionPendiente = new MutableLiveData<>();

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

    /**
     * Registra un control en un recorrido. La respuesta puede tener código 422, en cuyo caso el
     * mensaje contiene un código de error preestablecido (ver documentación).
     * @param codigo Código del control
     * @param idCarrera ID de la carrera
     * @param idRecorrido ID del recorrido
     * @param secreto Secreto del control
     */
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

    /**
     * Realiza la comprobación de si el usuario tiene alguna carrera pendiente.
     * Si la respuesta es false, limpia cualquier dato anterior e indica que no hay ningún recorrido pendiente.
     * Si la respuesta es true, se generan los datos necesarios y se indica que hay un recorrido pendiente.
     * @param context Contexto para preferencias compartidas
     */
    public void comprobarRecorridoPendiente(Context context) {
        apiClient.getPendiente().enqueue(new Callback<PendienteResponse>() {
            @Override
            public void onResponse(Call<PendienteResponse> call, Response<PendienteResponse> response) {
                Recurso<Boolean> resultado = new Recurso<>();
                Boolean pendiente = null;
                if(response.isSuccessful()) {
                    SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.sp_carrera_actual), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String keyIDCarrera = context.getString(R.string.sp_carrera_actual_idcarrera);
                    String keyIDRecorrido = context.getString(R.string.sp_carrera_actual_idrecorrido);

                    if(response.code() == 204) {
                        // No content: no tiene ninguna carrera pendiente
                        // Borra datos locales anteriores
                        editor.remove(keyIDCarrera);
                        editor.remove(keyIDRecorrido);
                        pendiente = false;
                    } else {
                        PendienteResponse resp = response.body();
                        if(resp.getCarrera() == null || resp.getRegistros() == null) {
                            // Error
                            // TODO
                            resultado.setError("Error. Respuesta del servidor inválida");
                        } else {
                            editor.putLong(keyIDCarrera, resp.getCarrera().getId());
                            editor.putLong(keyIDRecorrido, resp.getIdRecorrido());

                            // TODO BD Registros

                            pendiente = true;
                        }
                    }

                    editor.commit();
                } else {
                    // Error desconocido
                    // TODO
                    resultado.setError("Error desconocido");
                }

                resultado.setRecurso(pendiente);
                comprobacionPendiente.postValue(resultado);
            }

            @Override
            public void onFailure(Call<PendienteResponse> call, Throwable t) {
                // TODO Manejar algún error en concreto (sin internet, etc)
                Recurso<Boolean> recurso = new Recurso<>();
                recurso.setError("Error de conexión");
                comprobacionPendiente.postValue(recurso);
            }
        });
    }

    public LiveData<Recurso<Boolean>> getPendienteResponse() {
        return comprobacionPendiente;
    }

    public LiveData<RegistroControl> getRegistroResponse() {
        return registroResponse;
    }

    public MutableLiveData<String> getRegistroResponseError() {
        return registroResponseError;
    }
}
