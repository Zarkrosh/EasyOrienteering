package com.hergomsoft.easyorienteering.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.data.api.requests.CambioRequest;
import com.hergomsoft.easyorienteering.data.api.responses.ApiResponse;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.data.persistence.EasyODatabase;
import com.hergomsoft.easyorienteering.data.persistence.UsuarioDAO;
import com.hergomsoft.easyorienteering.util.AppExecutors;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.NetworkBoundResource;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.SingleLiveEvent;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioRepository extends ApiRepository {
    private SingleLiveEvent<Recurso<String>> cambioNombreResponse;
    private SingleLiveEvent<Recurso<String>> cambioClubResponse;

    private UsuarioDAO usuarioDAO;

    // Singleton
    private static UsuarioRepository instance;
    public static UsuarioRepository getInstance(Context context) {
        if(instance == null) {
            instance = new UsuarioRepository(context);
        }

        return instance;
    }

    private UsuarioRepository(Context context) {
        usuarioDAO = EasyODatabase.getInstance(context).getUsuarioDAO();
        cambioNombreResponse = new SingleLiveEvent<>();
        cambioClubResponse = new SingleLiveEvent<>();
    }

    public SingleLiveEvent<Recurso<String>> getCambioNombreResponse() { return cambioNombreResponse; }
    public SingleLiveEvent<Recurso<String>> getCambioClubResponse() { return cambioClubResponse; }

    public LiveData<Resource<Usuario>> getUsuario(long id) {
        return new NetworkBoundResource<Usuario, Usuario>(AppExecutors.getInstance()) {
            @Override
            protected void saveCallResult(@NonNull Usuario item) {
                item.setTimestamp((int)(System.currentTimeMillis() / 1000));
                usuarioDAO.insertUsuario(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Usuario data) {
                int currentTimeSecs = (int) (System.currentTimeMillis() / 1000);
                if(data == null || data.getTimestamp() == null) return true;
                return currentTimeSecs - data.getTimestamp() >= Constants.REFRESH_USUARIO_TIME;
            }

            @NonNull
            @Override
            protected LiveData<Usuario> loadFromDb() {
                return usuarioDAO.getUsuario(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Usuario>> createCall() {
                return apiClient.getUsuario(id);
            }
        }.getAsLiveData();
    }


    public void cambiaNombreUsuario(String nombre) {
        CambioRequest cambio = new CambioRequest(nombre);
        apiClient.cambiaNombreUsuario(cambio).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Recurso<String> recurso = new Recurso<>();
                if(response.isSuccessful() && response.body() != null) {
                    recurso.setRecurso(response.body().getNombre());
                    // Actualiza el dato del usuario en la BD local
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            usuarioDAO.insertUsuario(response.body());
                        }
                    });
                } else {
                    recurso.setError("Error al cambiar el nombre");
                    // TODO Manejar causa error
                }

                cambioNombreResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                cambioNombreResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }

    public void cambiaClub(String nombre) {
        CambioRequest cambio = new CambioRequest(nombre);
        apiClient.cambiaClub(cambio).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Recurso<String> recurso = new Recurso<>();
                if(response.isSuccessful() && response.body() != null) {
                    recurso.setRecurso(response.body().getClub());
                    // Actualiza el dato del usuario en la BD local
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            usuarioDAO.insertUsuario(response.body());
                        }
                    });
                } else {
                    recurso.setError("Error al cambiar el club");
                    // TODO Manejar causa error
                }

                cambioClubResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                cambioClubResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }




    // TEST
    public void borraDatosUsuarios() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                usuarioDAO.clearAll();
            }
        });

    }

}
