package com.hergomsoft.easyorienteering.ui.scan;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.api.responses.AbandonoResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Control;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Registro;
import com.hergomsoft.easyorienteering.data.repositories.RegistroRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.util.Utils;

import static android.Manifest.permission.CAMERA;

public class ScanViewModel extends AndroidViewModelConCarga {
    // Permisos que utiliza la actividad
    private String[] permisosNecesarios = new String[]{ CAMERA };

    // Modos de las vistas
    public enum ModoVista { INICIO_RECORRIDO, CARRERA };

    // Estados de los diálogos
    public static final int ESTADO_ELECCION_PENDIENTE = 4;

    // Datos de control escaneado
    private String codigo;
    private Long idCarrera;
    private Long idRecorrido;
    private String secreto;

    // Repositorios
    private RegistroRepository registroRepository;

    // LiveDatas
    private LiveData<Recurso<Boolean>> peticionPendiente;
    private LiveData<Recurso<Registro>> registroResponse;
    private LiveData<Recurso<AbandonoResponse>> abandonoResponse;
    // MutableLiveDatas
    private MutableLiveData<Integer> estadoDialogo;
    private MutableLiveData<String> tituloDialogo;
    private MutableLiveData<String> mensajeDialogo;
    private MutableLiveData<Control> siguienteControl;
    private MutableLiveData<Boolean> pantallaEscaneo;
    private MutableLiveData<ModoVista> modoVista;

    public ScanViewModel(Application app) {
        super(app);
        registroRepository = RegistroRepository.getInstance(app);
        peticionPendiente = registroRepository.getPendienteResponse();
        registroResponse = registroRepository.getRegistroResponse();
        abandonoResponse = registroRepository.getAbandonoResponse();
        estadoDialogo = new MutableLiveData<>(DialogoCarga.ESTADO_OCULTO);
        tituloDialogo = new MutableLiveData<>("");
        mensajeDialogo = new MutableLiveData<>("");
        siguienteControl = new MutableLiveData<>();
        pantallaEscaneo = new MutableLiveData<>(true);
        modoVista = new MutableLiveData<>(ModoVista.INICIO_RECORRIDO);
    }

    public LiveData<Recurso<Boolean>> getCarreraPendienteResponse() { return peticionPendiente; }
    public LiveData<Recurso<Registro>> getRegistroResponse() {
        return registroResponse;
    }
    public LiveData<Recurso<AbandonoResponse>> getAbandonoResponse() {
        return abandonoResponse;
    }
    public LiveData<Integer> getEstadoDialogoCarga() { return estadoDialogo; }
    public LiveData<Boolean> getAlternadoVistas() { return pantallaEscaneo; }
    public LiveData<ModoVista> getModoVista() { return modoVista; }

    public Carrera getCarreraActual() { return registroRepository.getCarreraActual(); }
    public Recorrido getRecorridoActual() { return registroRepository.getRecorridoActual(); }
    public LiveData<Control> getSiguienteControl() { return registroRepository.getSiguienteControl(); }

    public void alternarVistas() {
        pantallaEscaneo.postValue(!pantallaEscaneo.getValue());
    }
    public void pasaAModoCarrera() { modoVista.postValue(ModoVista.CARRERA); }

    /**
     * Realiza una petición de comprobación de recorrido pendiente.
     */
    public void compruebaRecorridoPendiente() {
        tituloDialogo.postValue("");
        mensajeDialogo.postValue(getApplication().getApplicationContext().getString(R.string.scan_carga_pendiente));
        estadoDialogo.postValue(DialogoCarga.ESTADO_CARGANDO);
        registroRepository.comprobarRecorridoPendiente();
    }

    /**
     * Muestra el diálogo de elección para un recorrido pendiente del usuario.
     */
    public void pideEleccionPendiente() {
        estadoDialogo.postValue(ESTADO_ELECCION_PENDIENTE);
    }

    /**
     * Realiza una petición de confirmación de recorrido. En esta petición se registra el control
     * de salida del recorrido, si ha sido exitoso.
     */
    public void confirmaInicioRecorrido() {
        tituloDialogo.postValue("");
        mensajeDialogo.postValue(getApplication().getString(R.string.scan_carga_inicio));
        estadoDialogo.postValue(DialogoCarga.ESTADO_CARGANDO);
        registroRepository.iniciaRecorrido(codigo, idCarrera, idRecorrido, secreto);
    }

    /**
     * Realiza una petición de registro de control.
     * @param escaneado Texto escaneado en el control
     */
    public void registraControl(String escaneado) {
        tituloDialogo.postValue("");
        mensajeDialogo.postValue(getApplication().getString(R.string.scan_carga_registrando));
        estadoDialogo.postValue(DialogoCarga.ESTADO_CARGANDO);
        codigo = Utils.getCodigoControlEscaneado(escaneado);
        secreto = Utils.getSecretoControlEscaneado(escaneado);
        registroRepository.registraControl(codigo, secreto);
    }

    /**
     * Realiza una petición de abandono del recorrido actual.
     */
    public void confirmaAbandonoRecorrido() {
        tituloDialogo.postValue("");
        mensajeDialogo.postValue(getApplication().getString(R.string.scan_carga_abandono));
        estadoDialogo.postValue(DialogoCarga.ESTADO_CARGANDO);
        registroRepository.abandonaRecorrido();
    }

    /**
     * Actualiza los datos de inicio de recorrido.
     * @param escaneado Datos del triángulo escaneado
     * @return True si se han actualizado correctamente, false si ha ocurrido algún error
     */
    public boolean setDatosEscaneadoInicio(String escaneado) {
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
