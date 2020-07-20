package com.hergomsoft.easyorienteering.ui.scan;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.data.api.responses.PendienteResponse;
import com.hergomsoft.easyorienteering.data.model.pagers.RegistroControl;
import com.hergomsoft.easyorienteering.data.repositories.RegistroRepository;
import com.hergomsoft.easyorienteering.util.Utils;

import static android.Manifest.permission.CAMERA;

public class ScanViewModel extends AndroidViewModel {

    // Permisos que utiliza la actividad
    private String[] permisosNecesarios = new String[]{ CAMERA };

    // Modos de las vistas
    public static final int MODO_INICIO_RECORRIDO = 0;
    public static final int MODO_CARRERA = 1;

    // Datos de triángulo escaneado
    private String codigo;
    private Long idCarrera;
    private Long idRecorrido;
    private String secreto;

    private RegistroRepository registroRepository;

    private LiveData<Boolean> peticionPendiente;
    private LiveData<RegistroControl> peticionRegistro;
    private LiveData<String> peticionRegistroError;

    private MutableLiveData<Boolean> pantallaEscaneo;

    private MutableLiveData<Integer> modoVista;

    public ScanViewModel(Application app) {
        super(app);
        registroRepository = new RegistroRepository();
        peticionPendiente = registroRepository.getPendienteResponse();
        peticionRegistro = registroRepository.getRegistroResponse();
        peticionRegistroError = registroRepository.getRegistroResponseError();
        pantallaEscaneo = new MutableLiveData<>(true);
        modoVista = new MutableLiveData<>(MODO_INICIO_RECORRIDO);
    }

    public LiveData<Boolean> carreraPendienteResponse() { return peticionPendiente; }
    public LiveData<RegistroControl> getResultadoRegistro() {
        return peticionRegistro;
    }
    public LiveData<String> getResultadoRegistroError() { return peticionRegistroError; }
    public LiveData<Boolean> getAlternadoVistas() { return pantallaEscaneo; }
    public LiveData<Integer> getModoVista() { return modoVista; }

    public void alternarVistas() {
        pantallaEscaneo.postValue(!pantallaEscaneo.getValue());
    }

    public void pasaAModoCarrera() { modoVista.postValue(MODO_CARRERA); }

    public void registraControl(String escaneado) {
        actualizaDatosEscaneado(escaneado);
        registroRepository.registraControl(codigo, idCarrera, idRecorrido, secreto);
    }

    /**
     * Realiza una petición de comprobación de recorrido pendiente.
     */
    public void compruebaRecorridoPendiente() {
        registroRepository.comprobarRecorridoPendiente(getApplication());
    }

    /**
     * Realiza una petición de confirmación de recorrido.
     */
    public void confirmaInicioRecorrido() {
        registroRepository.registraControl(codigo, idCarrera, idRecorrido, secreto);
    }

    /**
     * Actualiza los datos de inicio de recorrido.
     * @param escaneado Datos del triángulo escaneado
     * @return True si se han actualizado correctamente, false si ha ocurrido algún error
     */
    public boolean actualizaDatosEscaneado(String escaneado) {
        codigo = Utils.getCodigoControlEscaneado(escaneado);
        idCarrera = Utils.getIdentificadorCarreraEscaneado(escaneado);
        idRecorrido = Utils.getIdentificadorRecorridoEscaneado(escaneado);
        secreto = Utils.getSecretoControlEscaneado(escaneado);

        return codigo != null && idCarrera != null && idRecorrido != null && secreto != null;
    }

    /**
     * Devuelve la lista de permisos necesarios para la actividad.
     * @return Permisos
     */
    public String[] getPermisosNecesarios() {
        return permisosNecesarios;
    }

}
