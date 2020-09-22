package com.hergomsoft.easyorienteering.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.api.requests.CambioPassRequest;
import com.hergomsoft.easyorienteering.data.api.requests.CambioRequest;
import com.hergomsoft.easyorienteering.data.api.requests.LoginRequest;
import com.hergomsoft.easyorienteering.data.api.requests.RegistroCuentaRequest;
import com.hergomsoft.easyorienteering.data.api.responses.ApiResponse;
import com.hergomsoft.easyorienteering.data.api.responses.LoginResponse;
import com.hergomsoft.easyorienteering.data.api.responses.MessageResponse;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.data.persistence.EasyODatabase;
import com.hergomsoft.easyorienteering.data.persistence.UsuarioDAO;
import com.hergomsoft.easyorienteering.util.AppExecutors;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.NetworkBoundResource;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.SingleLiveEvent;
import com.hergomsoft.easyorienteering.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioRepository extends ApiRepository {
    private SingleLiveEvent<Resource<String>> loginState;             // Respuesta de login
    private SingleLiveEvent<Resource<String>> registerState;          // Respuesta de registro
    private SingleLiveEvent<Resource<String>> logoutState;            // Respuesta de logout
    private SingleLiveEvent<Resource<String>> borradoCuentaState;     // Respuesta de borrado de cuenta
    private SingleLiveEvent<Resource<String>> cambioNombreResponse;   // Respuesta de cambio de nombre
    private SingleLiveEvent<Resource<String>> cambioClubResponse;     // Respuesta de cambio de club
    private SingleLiveEvent<Resource<String>> cambioPasswordResponse; // Respuesta de cambio de contraseña

    private UsuarioDAO usuarioDAO;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    private String tokenUsuarioConectado;
    private Long idUsuarioConectado;

    // Singleton
    private static UsuarioRepository instance;
    public static UsuarioRepository getInstance(Context context) {
        if(instance == null) {
            instance = new UsuarioRepository(context);
        }

        return instance;
    }

    private UsuarioRepository(Context context) {
        super(context);
        usuarioDAO = EasyODatabase.getInstance(context).getUsuarioDAO();
        loginState = new SingleLiveEvent<>();
        registerState = new SingleLiveEvent<>();
        logoutState = new SingleLiveEvent<>();
        borradoCuentaState = new SingleLiveEvent<>();
        cambioNombreResponse = new SingleLiveEvent<>();
        cambioClubResponse = new SingleLiveEvent<>();
        cambioPasswordResponse = new SingleLiveEvent<>();

        cargaDatosUsuarioConectado();
    }

    public SingleLiveEvent<Resource<String>> getLoginState() { return loginState; }
    public SingleLiveEvent<Resource<String>> getRegisterState() { return registerState; }
    public SingleLiveEvent<Resource<String>> getEstadoLogout() { return logoutState; }
    public SingleLiveEvent<Resource<String>> getEstadoBorradoCuenta() { return borradoCuentaState; }
    public SingleLiveEvent<Resource<String>> getCambioNombreResponse() { return cambioNombreResponse; }
    public SingleLiveEvent<Resource<String>> getCambioClubResponse() { return cambioClubResponse; }
    public SingleLiveEvent<Resource<String>> getCambioPasswordResponse() { return cambioPasswordResponse; }

    public long getIdUsuarioConectado() {
        return idUsuarioConectado;
    }
    public String getTokenUsuarioConectado() {
        return tokenUsuarioConectado;
    }
    public boolean isLoggedIn() {
        return idUsuarioConectado != null;
    }

    public LiveData<Resource<Usuario>> getUsuario(long id) {
        return new NetworkBoundResource<Usuario, Usuario>(AppExecutors.getInstance(), true) {
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
                Resource<String> recurso;
                if(response.isSuccessful() && response.body() != null) {
                    recurso = Resource.success(response.body().getNombre());
                    // Actualiza datos del usuario en la BD local
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            usuarioDAO.insertUsuario(response.body());
                        }
                    });
                } else if(response.code() == 400) {
                    // Datos inválidos
                    MessageResponse messageResponse = Utils.deserializeMessageResponseFromError(response);
                    if(messageResponse != null) {
                        recurso = Resource.error(messageResponse.getMessage(), null);
                    } else {
                        recurso = Resource.error("Datos incorrectos", null);
                    }
                } else {
                    recurso = Resource.error("Error al cambiar el nombre", null);
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
        apiClient.cambiaClubUsuario(cambio).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Resource<String> recurso;
                if(response.isSuccessful() && response.body() != null) {
                    recurso = Resource.success(response.body().getClub());
                    // Actualiza datos del usuario en la BD local
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            usuarioDAO.insertUsuario(response.body());
                        }
                    });
                } else {
                    recurso = Resource.error("Error al cambiar el club", null);
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

    public void cambiaPassword(String prevPass, String newPass) {
        CambioPassRequest cambio = new CambioPassRequest(prevPass, newPass);
        apiClient.cambiaPassword(cambio).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Resource<String> recurso;
                if(response.isSuccessful() && response.body() != null) {
                    recurso = Resource.success(response.body().getMessage());
                } else {
                    if(response.body() != null) {
                        recurso = Resource.error(response.body().getMessage(), null);
                    } else {
                        // TODO Manejar más causas error
                        recurso = Resource.error("Error al cambiar la contraseña", null);
                    }
                }

                cambioPasswordResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                cambioPasswordResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }



    public void logout() {
        borraDatosUsuarioConectado();
        apiClient.logoutUsuario().enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Resource<String> recurso;
                if(response.isSuccessful()) {
                    recurso = Resource.success("");
                    borraDatosUsuarioConectado();
                } else {
                    recurso = Resource.error("No se pudo cerrar la sesión", null);
                }

                logoutState.postValue(recurso);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                logoutState.postValue(Resource.error("No se pudo conectar con el servidor", null));
            }
        });
    }

    public void login(String username, String password) {
        loginState.postValue(Resource.loading(null));

        LoginRequest request = new LoginRequest(username, password);
        apiClient.loginUsuario(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Resource<String> recurso;
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        LoginResponse resp = response.body();
                        idUsuarioConectado = resp.getId();
                        tokenUsuarioConectado = resp.getToken();
                        guardaDatosUsuario(resp.getId(), resp.getToken());
                        recurso = Resource.success("");
                    } else {
                        recurso = Resource.error("Error inesperado en los datos", null);
                    }
                } else if(response.code() == 401) {
                    // Credenciales inválidas
                    recurso = Resource.error("Datos incorrectos", null);
                } else {
                    // Error desconocido
                    recurso = Resource.error("Código de respuesta inesperado: " + response.code(), null);
                }

                loginState.postValue(recurso);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginState.postValue(Resource.error("No se pudo conectar con el servidor", null));
            }
        });
    }

    public void register(String username, String email, String club, String password) {
        registerState.postValue(Resource.loading(null));

        RegistroCuentaRequest request = new RegistroCuentaRequest(username, email, club, password);
        apiClient.registerUsuario(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Resource<String> recurso;
                if(response.isSuccessful()) {
                    recurso = Resource.success("");
                } else if(response.code() == 400 || response.code() == 500) {
                    // Algún dato inválido
                    MessageResponse messageResponse = Utils.deserializeMessageResponseFromError(response);
                    if(messageResponse != null) {
                        recurso = Resource.error(messageResponse.getMessage(), null);
                    } else {
                        recurso = Resource.error("Datos incorrectos", null);
                    }
                } else {
                    // Error desconocido
                    recurso = Resource.error("Código de respuesta inesperado: " + response.code(), null);
                }

                registerState.postValue(recurso);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                registerState.postValue(Resource.error("No se pudo conectar con el servidor", null));
            }
        });
    }

    public void borrarCuenta() {
        borraDatosUsuarioConectado();
        apiClient.borrarCuenta().enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Resource<String> recurso;
                if(response.isSuccessful()) {
                    recurso = Resource.success("");
                } else {
                    recurso = Resource.error("No se pudo borrar la cuenta", null);
                }

                borradoCuentaState.postValue(recurso);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                borradoCuentaState.postValue(Resource.error("No se pudo conectar con el servidor", null));
            }
        });
    }

    private void guardaDatosUsuario(Long idUsuario, String tokenUsuario) {
        this.idUsuarioConectado = idUsuario;
        this.tokenUsuarioConectado = tokenUsuario;
        // Guarda los datos en las SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Constants.PREFS_ID_USUARIO, idUsuario);
        editor.putString(Constants.PREFS_TOKEN_USUARIO, tokenUsuario);
        editor.apply();
    }

    private void cargaDatosUsuarioConectado() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long id = prefs.getLong(Constants.PREFS_ID_USUARIO, -1);
        String token = prefs.getString(Constants.PREFS_TOKEN_USUARIO, "");
        if(id != -1 && !token.isEmpty()) {
            this.idUsuarioConectado = id;
            this.tokenUsuarioConectado = token;
        } else {
            this.idUsuarioConectado = null;
            this.tokenUsuarioConectado = null;
        }
    }

    private void borraDatosUsuarioConectado() {
        this.idUsuarioConectado = null;
        this.tokenUsuarioConectado = null;
        // Borra los datos de las SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.PREFS_ID_USUARIO);
        editor.remove(Constants.PREFS_TOKEN_USUARIO);
        editor.apply();
    }

}
