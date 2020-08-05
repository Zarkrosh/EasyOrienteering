package com.hergomsoft.easyorienteering.data.repositories;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.data.api.requests.RegistroRequest;
import com.hergomsoft.easyorienteering.data.api.responses.AbandonoResponse;
import com.hergomsoft.easyorienteering.data.api.responses.InicioResponse;
import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.api.responses.RegistroResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Control;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Registro;

import org.json.JSONObject;

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

    private MutableLiveData<Recurso<Registro>> registroResponse;         // Respuesta de registro de control
    private MutableLiveData<Recurso<Boolean>> comprobacionPendiente;     // Respuesta de comprobación de recorrido pendiente
    private MutableLiveData<Recurso<AbandonoResponse>> abandonoResponse; // Respuesta de comprobación de recorrido pendiente
    private MutableLiveData<Control> siguienteControl;

    public RegistroRepository(Application application) {
        //registroDAO = DatosDatabase.getDatabase(application).getRegistroDAO();
        //controlDAO = DatosDatabase.getDatabase(application).getControlDAO();
        //recorridoDAO = DatosDatabase.getDatabase(application).getRecorridoDAO();

        registroResponse = new MutableLiveData<>();
        comprobacionPendiente = new MutableLiveData<>();
        abandonoResponse = new MutableLiveData<>();
        siguienteControl = new MutableLiveData<>();
        registroList = new ArrayList<>();
    }

    /**
     * Inicia el recorrido de una carrera. La respuesta puede tener código 422, en cuyo caso el
     * mensaje contiene un código de error preestablecido (ver documentación).
     * @param codigo Código del control
     * @param idCarrera ID de la carrera
     * @param idRecorrido ID del recorrido
     * @param secreto Secreto del control
     */
    public void iniciaRecorrido(String codigo, long idCarrera, long idRecorrido, String secreto) {
        RegistroRequest request = new RegistroRequest(codigo, secreto);
        apiClient.iniciaRecorrido(idCarrera, idRecorrido, request).enqueue(new Callback<InicioResponse>() {
            @Override
            public void onResponse(Call<InicioResponse> call, Response<InicioResponse> response) {
                Recurso<Registro> recurso = new Recurso();
                if(response.isSuccessful() && response.body() != null) {
                    InicioResponse ir = response.body();
                    carreraActual = ir.getCarrera();
                    recorridoActual = ir.getRecorrido();
                    if(carreraActual == null || recorridoActual == null) {
                        onFailure(call, new IllegalArgumentException("La carrera o el recorrido actuales son nulos"));
                    }

                    RegistroResponse regResp = ir.getRegistro();
                    Control control = carreraActual.getControles().get(regResp.getControl());
                    if(control == null) onFailure(call, new IllegalArgumentException("No se ha encontrado el control con código: " + regResp.getControl()));
                    Registro registro = new Registro(control, recorridoActual, regResp.getFecha());
                    recurso.setRecurso(registro);
                    registraControlLocal(registro);
                } else if(response.code() == 422) {
                    // Error lanzado en caso de algún error. Contiene un código de error
                    asignaCodigoError(recurso, response);
                } else if(response.code() == 404) {
                    // No existe la carrera con el ID indicado
                    recurso.setError("No existe la carrera con el ID indicado");
                } else {
                    // Error desconocido
                    recurso.setError("Código de respuesta inesperado: " + response.code());
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
        apiClient.registraControl(carreraActual.getId(), request).enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                Recurso<Registro> recurso = new Recurso();
                if(response.isSuccessful() && response.body() != null) {
                    RegistroResponse resp = response.body();
                    // Obtiene el control y el recorrido desde su código e ID
                    //Control control = controlDAO.getByCodigo(resp.getControl());
                    //Recorrido recorrido = recorridoDAO.getByID(resp.getRecorrido());
                    Control control = carreraActual.getControles().get(resp.getControl());
                    if(control == null) onFailure(call, new IllegalArgumentException("No se ha encontrado el control con código: " + resp.getControl()));

                    Registro registro = new Registro(control, recorridoActual, resp.getFecha());
                    recurso.setRecurso(registro);
                    registraControlLocal(registro);
                } else if(response.code() == 422) {
                    // Error lanzado en caso de algún error. Contiene un código de error
                    asignaCodigoError(recurso, response);
                } else if(response.code() == 404) {
                    // No existe la carrera con el ID indicado
                    recurso.setError("No existe la carrera con el ID indicado");
                } else {
                    // Error desconocido
                    recurso.setError("Código de respuesta inesperado: " + response.code());
                }

                registroResponse.postValue(recurso);
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                registroResponse.postValue(getRecursoConErrorConexion(t));
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
                    if(response.code() == 204) {
                        // No content: no tiene ninguna carrera pendiente
                        carreraActual = null;
                        recorridoActual = null;
                        pendiente = false;
                    } else {
                        PendienteResponse resp = response.body();
                        if(resp.getCarrera() == null || resp.getRegistros() == null) {
                            // Error
                            // TODO
                            resultado.setError("Error. Respuesta del servidor inválida");
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
                            //new InsertRegistrosAT(registroDAO).execute(resp.getRegistros());
                            registroList = new ArrayList<>();
                            for(RegistroResponse rr : resp.getRegistros()) {
                                Control control = carreraActual.getControles().get(rr.getControl());
                                if(control == null) onFailure(call, new IllegalArgumentException("No se ha encontrado el control con código: " + rr.getControl()));
                                registroList.add(new Registro(control, recorridoActual, rr.getFecha()));
                            }
                            actualizaSiguienteControl();
                            pendiente = true;
                        }
                    }
                } else {
                    // Error desconocido
                    resultado.setError("Error desconocido. Código HTTP " + response.code());
                }

                resultado.setRecurso(pendiente);
                comprobacionPendiente.postValue(resultado);
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
        apiClient.abandonaRecorrido(recorridoActual.getId()).enqueue(new Callback<AbandonoResponse>() {
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
                abandonoResponse.postValue(getRecursoConErrorConexion(t));
            }
        });
    }

    public LiveData<Recurso<Boolean>> getPendienteResponse() { return comprobacionPendiente; }
    public LiveData<Recurso<Registro>> getRegistroResponse() { return registroResponse; }
    public LiveData<Recurso<AbandonoResponse>> getAbandonoResponse() { return abandonoResponse; }
    public LiveData<Control> getSiguienteControl() { return siguienteControl; }
    public Carrera getCarreraActual() { return carreraActual; }
    public Recorrido getRecorridoActual() { return recorridoActual; }

    /**
     * Obtiene el código de error del cuerpo del mensaje de la respuesta.
     * @param recurso Recurso al que asignar el error
     * @param response Respuesta con el error
     */
    private void asignaCodigoError(Recurso recurso, Response response) {
        try {
            JSONObject json = new JSONObject(response.errorBody().string());
            recurso.setError(json.getString("message"));
            // TODO Convertir a error legible por el usuario
        } catch (Exception e) {
            e.printStackTrace();
            recurso.setError("Error inesperado al obtener el código de error");
        }
    }

    private void registraControlLocal(Registro registro) {
        registroList.add(registro);
        actualizaSiguienteControl();
    }

    private void actualizaSiguienteControl() {
        // Carga siguiente control
        if(carreraActual.getModalidad().equals(Carrera.Modalidad.LINEA)) {
            // LINEA
            String[] trazado = recorridoActual.getTrazado();
            if(registroList.size() < trazado.length) {
                Control control = carreraActual.getControles().get(trazado[registroList.size()]);
                siguienteControl.postValue(control);
            }
        }
    }

    /**
     * Tarea asíncrona que guarda los datos de una carrera en la BD local.

    private static class GuardaDatosCarrera extends AsyncTask<Carrera, Void, Void> {

        GuardaDatosCarrera() {

        }

        @Override
        protected Void doInBackground(final Carrera... params) {
            Carrera carrera = params[0];


            return null;
        }
    }*/

    /**
     * Tarea asíncrona que inserta los registros de una carrera pendiente.

    private static class InsertRegistrosAT extends AsyncTask<Registro[], Void, Void> {
        private RegistroDAO dao;
        InsertRegistrosAT(RegistroDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Registro[]... params) {
            dao.deleteAll();
            dao.insert(params[0]);
            return null;
        }
    }*/

}
