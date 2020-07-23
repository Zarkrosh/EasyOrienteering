package com.hergomsoft.easyorienteering.data.repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.api.ApiClient;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.api.responses.AbandonoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.db.RegistroDatabase;
import com.hergomsoft.easyorienteering.data.db.dao.RegistroDAO;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Registro;
import com.hergomsoft.easyorienteering.data.model.RegistroControl;
import com.hergomsoft.easyorienteering.util.Constants;

import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroRepository {

    private ApiClient apiClient;
    private RegistroDAO registroDAO;

    private MutableLiveData<Recurso<RegistroControl>> registroResponse; // Respuesta de registro de control
    private MutableLiveData<Recurso<Boolean>> comprobacionPendiente;    // Respuesta de comprobación de recorrido pendiente
    private MutableLiveData<Recurso<AbandonoResponse>> abandonoResponse;    // Respuesta de comprobación de recorrido pendiente

    public RegistroRepository(Application application) {
        registroDAO = RegistroDatabase.getDatabase(application).getRegistroDAO();

        registroResponse = new MutableLiveData<>();
        comprobacionPendiente = new MutableLiveData<>();
        abandonoResponse = new MutableLiveData<>();

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
                Recurso<RegistroControl> res = new Recurso();
                if(response.isSuccessful()) {
                    res.setRecurso(response.body());
                } else if(response.code() == 422) {
                    // Error lanzado en caso de algún error. Contiene un código de error
                    res.setError(response.message());
                } else if(response.code() == 404) {
                    // No existe la carrera con el ID indicado
                    res.setError("No existe la carrera con el ID indicado");
                } else {
                    // Error desconocido
                    res.setError("Error inesperado");
                }

                registroResponse.postValue(res);
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
                    String keyNombreCarrera = context.getString(R.string.sp_carrera_actual_nombrecarrera);
                    String keyNombreRecorrido = context.getString(R.string.sp_carrera_actual_nombrerecorrido);

                    if(response.code() == 204) {
                        // No content: no tiene ninguna carrera pendiente
                        // Borra datos locales anteriores
                        editor.remove(keyIDCarrera);
                        editor.remove(keyIDRecorrido);
                        editor.remove(keyNombreCarrera);
                        editor.remove(keyNombreRecorrido);
                        pendiente = false;
                    } else {
                        PendienteResponse resp = response.body();
                        if(resp.getCarrera() == null || resp.getRegistros() == null) {
                            // Error
                            // TODO
                            resultado.setError("Error. Respuesta del servidor inválida");
                        } else {
                            Carrera carrera = resp.getCarrera();
                            // Obtiene el nombre del recorrido
                            String nombreRecorrido = null;
                            int i = 0;
                            while(nombreRecorrido == null || i < carrera.getRecorridos().length) {
                                if(carrera.getRecorridos()[i].getId() == resp.getIdRecorrido()) {
                                    nombreRecorrido = carrera.getRecorridos()[i].getNombre();
                                }
                                i++;
                            }

                            editor.putLong(keyIDCarrera, carrera.getId());
                            editor.putLong(keyIDRecorrido, resp.getIdRecorrido());
                            editor.putString(keyNombreCarrera, carrera.getNombre());
                            editor.putString(keyNombreRecorrido, nombreRecorrido);

                            // Borra todos los registros anteriores e introduce los nuevos
                            new insertRegistrosAT(registroDAO).execute(resp.getRegistros());
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
                Recurso<Boolean> recurso = new Recurso<>();
                if(t.getCause() instanceof SocketTimeoutException) {
                    recurso.setError("No se ha podido contactar con el servidor (tiempo expirado)");
                } else {
                    // TODO Manejar algún error en concreto (sin internet, etc)
                    recurso.setError("Error de conexión desconocido");
                }

                comprobacionPendiente.postValue(recurso);
            }
        });
    }

    public void abandonaRecorrido(long idRecorrido) {
        apiClient.abandonaRecorrido(idRecorrido).enqueue(new Callback<AbandonoResponse>() {
            @Override
            public void onResponse(Call<AbandonoResponse> call, Response<AbandonoResponse> response) {
                Recurso<AbandonoResponse> recurso = new Recurso<>();
                if(response.code() == 200) {
                    recurso.setRecurso(response.body());
                } else {
                    recurso.setError("Código de respuesta inesperado: " + response.code() + " (se esperaba 200)");
                }
                abandonoResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<AbandonoResponse> call, Throwable t) {
                Recurso<AbandonoResponse> recurso = new Recurso<>();
                if(t.getCause() instanceof SocketTimeoutException) {
                    recurso.setError("No se ha podido contactar con el servidor (tiempo expirado)");
                } else {
                    // TODO Manejar algún error en concreto (sin internet, etc)
                    recurso.setError("Error de conexión desconocido");
                }

                abandonoResponse.postValue(recurso);
            }
        });
    }

    public LiveData<Recurso<Boolean>> getPendienteResponse() { return comprobacionPendiente; }
    public LiveData<Recurso<RegistroControl>> getRegistroResponse() { return registroResponse; }
    public LiveData<Recurso<AbandonoResponse>> getAbandonoResponse() { return abandonoResponse; }


    private static class insertRegistrosAT extends AsyncTask<Registro[], Void, Void> {
        private RegistroDAO dao;
        insertRegistrosAT(RegistroDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Registro[]... params) {
            dao.deleteAll();
            dao.add(params[0]);
            return null;
        }
    }

}
