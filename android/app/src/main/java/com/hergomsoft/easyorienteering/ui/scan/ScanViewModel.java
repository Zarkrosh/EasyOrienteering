package com.hergomsoft.easyorienteering.ui.scan;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Control;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.RegistroControl;
import com.hergomsoft.easyorienteering.data.repositories.RegistroRepository;
import com.hergomsoft.easyorienteering.util.Utils;

import static android.Manifest.permission.CAMERA;

public class ScanViewModel extends AndroidViewModel {
    // Permisos que utiliza la actividad
    private String[] permisosNecesarios = new String[]{ CAMERA };

    // Modos de las vistas
    public static final int MODO_INICIO_RECORRIDO = 0;
    public static final int MODO_CARRERA = 1;

    // Estados de los diálogos
    public static final int ESTADO_OCULTO = 0;
    public static final int ESTADO_ERROR = 1;
    public static final int ESTADO_CARGANDO = 2;
    public static final int ESTADO_ELECCION_PENDIENTE = 3;

    private String tituloDialogo;
    private String mensajeDialogo;

    // Datos de triángulo escaneado
    private String codigo;
    private Long idCarrera;
    private Long idRecorrido;
    private String secreto;

    private RegistroRepository registroRepository;

    private LiveData<Recurso<Boolean>> peticionPendiente;
    private LiveData<Recurso<RegistroControl>> peticionRegistro;
    private LiveData<Recurso<Control>> siguienteControl;

    private MutableLiveData<Integer> estadoDialogo;
    private MutableLiveData<Boolean> pantallaEscaneo;
    private MutableLiveData<Integer> modoVista;

    public ScanViewModel(Application app) {
        super(app);
        registroRepository = new RegistroRepository(app);
        peticionPendiente = registroRepository.getPendienteResponse();
        peticionRegistro = registroRepository.getRegistroResponse();
        estadoDialogo = new MutableLiveData<>(ESTADO_OCULTO);
        siguienteControl = new MutableLiveData<>();
        pantallaEscaneo = new MutableLiveData<>(true);
        modoVista = new MutableLiveData<>(MODO_INICIO_RECORRIDO);
        mensajeDialogo = "";
    }

    public LiveData<Recurso<Boolean>> getCarreraPendienteResponse() { return peticionPendiente; }
    public LiveData<Recurso<RegistroControl>> getResultadoRegistro() {
        return peticionRegistro;
    }
    public LiveData<Recurso<Control>> getSiguienteControl() {
        return siguienteControl;
    }
    public LiveData<Integer> getEstadoDialogoCarga() { return estadoDialogo; }
    public LiveData<Boolean> getAlternadoVistas() { return pantallaEscaneo; }
    public LiveData<Integer> getModoVista() { return modoVista; }

    public void alternarVistas() {
        pantallaEscaneo.postValue(!pantallaEscaneo.getValue());
    }
    public void pasaAModoCarrera() { modoVista.postValue(MODO_CARRERA); }

    /**
     * Asigna un nuevo estado al diálogo de carga de la vista.
     * @param estado Nuevo estado (ver constantes)
     */
    public void setEstadoDialogo(int estado) {
        estadoDialogo.postValue(estado);
    }

    /**
     * Asigna el estado de error al diálogo de carga de la vista con el título y mensaje especificados.
     * @param mensaje Mensaje de error
     */
    public void setEstadoDialogoError(String titulo, String mensaje) {
        estadoDialogo.postValue(ESTADO_ERROR);
        tituloDialogo = titulo;
        mensajeDialogo = mensaje;
    }

    /**
     * Devuelve el mensaje del diálogo de error.
     * @return Mensaje de error
     */
    public String getMensajeDialogo() { return mensajeDialogo; }
    /**
     * Devuelve el titulo del diálogo de error.
     * @return Título de error
     */
    public String getTituloDialogo() { return tituloDialogo; }

    /**
     * Realiza una petición de registro de control.
     * @param escaneado Texto escaneado en el control
     */
    public void registraControl(String escaneado) {
        if(idCarrera == null || idRecorrido == null) {
            // Obtiene los datos desde preferencias compartidas
            Context c = getApplication();
            SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.sp_carrera_actual), Context.MODE_PRIVATE);
            String keyIDCarrera = c.getString(R.string.sp_carrera_actual_idcarrera);
            String keyIDRecorrido = c.getString(R.string.sp_carrera_actual_idrecorrido);
            long idC = sharedPref.getLong(keyIDCarrera, -1);
            long idR = sharedPref.getLong(keyIDRecorrido, -1);
            if(idC == -1 || idR == -1) {
                // Error
                // TODO
            } else {
                idCarrera = idC;
                idRecorrido = idR;
            }
        }

        registroRepository.registraControl(codigo, idCarrera, idRecorrido, secreto);
    }

    /**
     * Realiza una petición de comprobación de recorrido pendiente.
     */
    public void compruebaRecorridoPendiente() {
        tituloDialogo = "";
        mensajeDialogo = getApplication().getApplicationContext().getString(R.string.scan_carga_pendiente);
        estadoDialogo.postValue(ESTADO_CARGANDO);
        registroRepository.comprobarRecorridoPendiente(getApplication());
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
        registroRepository.registraControl(codigo, idCarrera, idRecorrido, secreto);
    }

    /**
     * Realiza una petición de abandono del recorrido actual.
     */
    public void confirmaAbandonoRecorrido() {
        tituloDialogo = "";
        mensajeDialogo = getApplication().getString(R.string.scan_carga_abandono);
        estadoDialogo.postValue(ESTADO_CARGANDO);
        // TODO
    }

    /**
     * Realiza una petición de carga de datos de la carrera actual.
     */
    public void cargaDatosCarrera() {
        tituloDialogo = "";
        mensajeDialogo = getApplication().getApplicationContext().getString(R.string.scan_carga_carrera);
        estadoDialogo.postValue(ESTADO_CARGANDO);
        //registroRepository.comprobarRecorridoPendiente(getApplication());
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
