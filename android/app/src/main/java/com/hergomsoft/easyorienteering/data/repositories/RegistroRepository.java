package com.hergomsoft.easyorienteering.data.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.api.responses.ApiResponse;
import com.hergomsoft.easyorienteering.data.api.responses.InicioResponse;
import com.hergomsoft.easyorienteering.data.api.responses.MessageResponse;
import com.hergomsoft.easyorienteering.data.api.responses.ParticipacionesRecorridoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Control;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.Registro;
import com.hergomsoft.easyorienteering.util.AppExecutors;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.NetworkBoundResource;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.SingleLiveEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroRepository extends ApiRepository {
    //private RegistroDAO registroDAO;
    //private ControlDAO controlDAO;
    //private RecorridoDAO recorridoDAO;

    // Datos de la carrera
    private Carrera carreraActual;
    private Recorrido recorridoActual;
    private List<Registro> registroList;

    private SingleLiveEvent<Resource<Registro>> registroResponse;         // Respuesta de registro de control
    private SingleLiveEvent<Resource<Boolean>> comprobacionPendiente;     // Respuesta de comprobación de recorrido pendiente
    private SingleLiveEvent<Resource<MessageResponse>> abandonoResponse; // Respuesta de comprobación de recorrido pendiente
    private MutableLiveData<Control> siguienteControl;

    // Singleton
    private static RegistroRepository instance;
    public static RegistroRepository getInstance(Context context) {
        if(instance == null) {
            instance = new RegistroRepository(context);
        }

        return instance;
    }

    private RegistroRepository(Context context) {
        super(context);
        //registroDAO = DatosDatabase.getDatabase(application).getRegistroDAO();
        //controlDAO = DatosDatabase.getDatabase(application).getControlDAO();
        //recorridoDAO = DatosDatabase.getDatabase(application).getRecorridoDAO();

        registroResponse = new SingleLiveEvent<>();
        comprobacionPendiente = new SingleLiveEvent<>();
        abandonoResponse = new SingleLiveEvent<>();
        siguienteControl = new MutableLiveData<>();
        registroList = new ArrayList<>();
    }

    /**
     * Inicia el recorrido de una carrera. La respuesta puede tener código 422, en cuyo caso el
     * mensaje contiene un código de error preestablecido (ver documentación).
     * @param codigo Código del control
     * @param idRecorrido ID del recorrido
     * @param secreto Secreto del control
     */
    public void iniciaRecorrido(String codigo, long idRecorrido, String secreto) {
        RegistroRequest request = new RegistroRequest(codigo, secreto);
        apiClient.iniciaRecorrido(idRecorrido, request).enqueue(new Callback<InicioResponse>() {
            @Override
            public void onResponse(Call<InicioResponse> call, Response<InicioResponse> response) {
                Resource<Registro> recurso;
                if(response.isSuccessful() && response.body() != null) {
                    InicioResponse ir = response.body();
                    carreraActual = ir.getCarrera();
                    recorridoActual = ir.getRecorrido();
                    if(carreraActual == null || recorridoActual == null) {
                        onFailure(call, new IllegalArgumentException("La carrera o el recorrido actuales son nulos"));
                    }

                    Registro registro = ir.getRegistro();
                    Control control = carreraActual.getControles().get(registro.getControl());
                    if(control == null) onFailure(call, new IllegalArgumentException("No se ha encontrado el control con código: " + registro.getControl()));
                    recurso = Resource.success(registro);
                    registroList.clear();
                    registraControlLocal(registro);
                } else if(response.code() == 422) {
                    // Error lanzado en caso de algún error. Contiene un código de error
                    String codigoError = "";
                    try {
                        codigoError = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recurso = getRecursoDesdeCodigo(codigoError);
                } else if(response.code() == 404) {
                    // No existe la carrera con el ID indicado
                    recurso = Resource.error("No existe la carrera con el ID indicado", null);
                } else {
                    // Error desconocido
                    recurso = Resource.error("Código de respuesta inesperado: " + response.code(), null);
                }

                registroResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<InicioResponse> call, Throwable t) {
                registroResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }

    /**
     * Registra un control en un recorrido. La respuesta puede tener código 422, en cuyo caso el
     * mensaje contiene un código de error preestablecido (ver documentación).
     * @param codigo Código del control
     * @param secreto Secreto del control
     */
    public void registraControl(String codigo, String secreto) {
        RegistroRequest request = new RegistroRequest(codigo, secreto);
        apiClient.registraControl(recorridoActual.getId(), request).enqueue(new Callback<Registro>() {
            @Override
            public void onResponse(Call<Registro> call, Response<Registro> response) {
                Resource<Registro> recurso;
                if(response.isSuccessful() && response.body() != null) {
                    Registro reg = response.body();
                    recurso = Resource.success(reg);
                    registraControlLocal(reg);
                } else if(response.code() == 422) {
                    // Error lanzado en caso de algún error. Contiene un código de error
                    String codigoError = "";
                    try {
                        codigoError = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recurso = getRecursoDesdeCodigo(codigoError);
                } else if(response.code() == 404) {
                    // No existe la carrera con el ID indicado
                    recurso = Resource.error("No existe la carrera con el ID indicado", null);
                } else {
                    // Error desconocido
                    recurso = Resource.error("Código de respuesta inesperado: " + response.code(), null);
                }

                registroResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<Registro> call, Throwable t) {
                registroResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }

    /**
     * Realiza la comprobación de si el usuario tiene alguna carrera pendiente.
     * Si la respuesta es false, limpia cualquier dato anterior e indica que no hay ningún recorrido pendiente.
     * Si la respuesta es true, se generan los datos necesarios y se indica que hay un recorrido pendiente.
     */
    public void comprobarRecorridoPendiente() {
        apiClient.getPendiente().enqueue(new Callback<PendienteResponse>() {
            @Override
            public void onResponse(Call<PendienteResponse> call, Response<PendienteResponse> response) {
                Resource<Boolean> recurso;
                if(response.isSuccessful()) {
                    if(response.code() == 204) {
                        // No content: no tiene ninguna carrera pendiente
                        carreraActual = null;
                        recorridoActual = null;
                        recurso = Resource.success(false);
                    } else if(response.body() != null) {
                        PendienteResponse resp = response.body();
                        if(resp.getCarrera() == null || resp.getParticipacion() == null) {
                            // Error
                            Log.d("EASYO", resp.toString());
                            recurso = Resource.error("Error. Respuesta del servidor inválida", null);
                        } else {
                            carreraActual = resp.getCarrera();
                            recorridoActual = null;
                            int i = 0;
                            while(recorridoActual == null || i < carreraActual.getRecorridos().size()) {
                                if(carreraActual.getRecorridos().get(i).getId() == resp.getIdRecorrido()) {
                                    recorridoActual = carreraActual.getRecorridos().get(i);
                                }
                                i++;
                            }

                            // Borra todos los registros anteriores e introduce los nuevos
                            registroList = new ArrayList<>();
                            registroList.addAll(resp.getParticipacion().getRegistros());
                            actualizaSiguienteControl();
                            recurso = Resource.success(true);
                        }
                    } else {
                        // No debería pasar
                        recurso = Resource.success(false);
                    }
                } else {
                    // Error desconocido
                    recurso = Resource.error("Error desconocido. Código HTTP " + response.code(), null);
                }

                comprobacionPendiente.postValue(recurso);
            }

            @Override
            public void onFailure(Call<PendienteResponse> call, Throwable t) {
                comprobacionPendiente.postValue(getRecursoConErrorConexion(t));
            }
        });
    }

    /**
     * Realiza una petición de abandono de recorrido.
     */
    public void abandonaRecorrido() {
        apiClient.abandonaRecorrido(recorridoActual.getId()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Resource<MessageResponse> recurso;
                if(response.code() == 200 && response.body() != null) {
                    recurso = Resource.success(response.body());
                } else {
                    recurso = Resource.error("Código de respuesta inesperado: " + response.code() + " (se esperaba 200)", null);
                }
                abandonoResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                abandonoResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }

    public LiveData<Resource<ParticipacionesRecorridoResponse>> getRegistrosRecorrido(long idRecorrido) {
        return new NetworkBoundResource<ParticipacionesRecorridoResponse, ParticipacionesRecorridoResponse>(AppExecutors.getInstance(), false) {
            /*
            @Override
            protected void saveCallResult(@NonNull RegistrosRecorridoResponse item) {
                item.setTimestamp((int)(System.currentTimeMillis() / 1000));
                usuarioDAO.insertUsuario(item);
            }

            @NonNull
            @Override
            protected LiveData<RegistrosRecorridoResponse> loadFromDb() {
                return usuarioDAO.getUsuario(idRecorrido);
            }


            @Override
            protected boolean shouldFetch(@Nullable RegistrosRecorridoResponse data) {
                int currentTimeSecs = (int) (System.currentTimeMillis() / 1000);
                if(data == null || data.getTimestamp() == null) return true;
                return currentTimeSecs - data.getTimestamp() >= Constants.REFRESH_USUARIO_TIME;
            } */

            @NonNull
            @Override
            protected LiveData<ApiResponse<ParticipacionesRecorridoResponse>> createCall() {
                return apiClient.getRegistrosRecorrido(idRecorrido);
            }
        }.getAsLiveData();
    }

    public void resetDatos() {
        this.carreraActual = null;
        this.recorridoActual = null;
    }

    public SingleLiveEvent<Resource<Boolean>> getPendienteResponse() { return comprobacionPendiente; }
    public SingleLiveEvent<Resource<Registro>> getRegistroResponse() { return registroResponse; }
    public SingleLiveEvent<Resource<MessageResponse>> getAbandonoResponse() { return abandonoResponse; }
    public LiveData<Control> getSiguienteControl() { return siguienteControl; }
    public Carrera getCarreraActual() { return carreraActual; }
    public Recorrido getRecorridoActual() { return recorridoActual; }

    /**
     * Obtiene el código de error del cuerpo del mensaje de la respuesta.
     * @param codigoError Codigo de error
     */
    private Resource getRecursoDesdeCodigo(String codigoError) {
        Resource res = null;
        try {
            String errorLegible = "Registro incorrecto";
            String s = Constants.erroresRegistro.get(codigoError);
            if(s != null) errorLegible = s;
            res = Resource.error(errorLegible, null);
        } catch (Exception e) {
            e.printStackTrace();
            res = Resource.error("Error inesperado al obtener el código de error", null);
        }

        return res;
    }

    private void registraControlLocal(Registro registro) {
        registroList.add(registro);
        actualizaSiguienteControl();
    }

    private void actualizaSiguienteControl() {
        // Carga siguiente control
        if(carreraActual.getModalidad().equals(Carrera.Modalidad.TRAZADO)) {
            // LINEA
            String[] trazado = recorridoActual.getTrazado();
            if(registroList.size() < trazado.length) {
                Control control = carreraActual.getControles().get(trazado[registroList.size()]);
                siguienteControl.postValue(control);
            }
        } else {
            // SCORE
            siguienteControl.postValue(null);
        }
    }

}
