package com.hergomsoft.easyorienteering.ui.scan;

import android.app.Application;
import android.os.Handler;

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
    public enum ModoEscaneo { INICIO_RECORRIDO, CARRERA };
    public enum ModoVista { ESCANEO, MAPA };

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
    private MutableLiveData<Control> siguienteControl;
    private MutableLiveData<ModoVista> modoVista;
    private MutableLiveData<ModoEscaneo> modoEscaneo;

    public ScanViewModel(Application app) {
        super(app);
        registroRepository = RegistroRepository.getInstance(app);
        peticionPendiente = registroRepository.getPendienteResponse();
        registroResponse = registroRepository.getRegistroResponse();
        abandonoResponse = registroRepository.getAbandonoResponse();
        siguienteControl = new MutableLiveData<>();
        modoVista = new MutableLiveData<>(ModoVista.ESCANEO);
        modoEscaneo = new MutableLiveData<>(ModoEscaneo.INICIO_RECORRIDO);
    }

    public LiveData<Recurso<Boolean>> getCarreraPendienteResponse() { return peticionPendiente; }
    public LiveData<Recurso<Registro>> getRegistroResponse() {
        return registroResponse;
    }
    public LiveData<Recurso<AbandonoResponse>> getAbandonoResponse() { return abandonoResponse; }
    public LiveData<ModoVista> getAlternadoVistas() { return modoVista; }
    public LiveData<ModoEscaneo> getModoEscaneo() { return modoEscaneo; }

    public Carrera getCarreraActual() { return registroRepository.getCarreraActual(); }
    public Recorrido getRecorridoActual() { return registroRepository.getRecorridoActual(); }
    public LiveData<Control> getSiguienteControl() { return registroRepository.getSiguienteControl(); }

    public void alternarModoVista() {
        switch (modoVista.getValue()) {
            case MAPA:
                modoVista.postValue(ModoVista.ESCANEO);
                break;
            case ESCANEO:
                modoVista.postValue(ModoVista.MAPA);
                break;
        }
    }
    public void pasaAModoCarrera() { modoEscaneo.postValue(ModoEscaneo.CARRERA); }

    /**
     * Realiza una petición de comprobación de recorrido pendiente.
     */
    public void compruebaRecorridoPendiente() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.scan_carga_pendiente));

        // TEST
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                registroRepository.comprobarRecorridoPendiente();


            }
        }, 2000);
    }

    /**
     * Realiza una petición de confirmación de recorrido. En esta petición se registra el control
     * de salida del recorrido, si ha sido exitoso.
     */
    public void confirmaInicioRecorrido() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.scan_carga_inicio));
        registroRepository.iniciaRecorrido(codigo, idCarrera, idRecorrido, secreto);
    }

    /**
     * Realiza una petición de registro de control.
     * @param escaneado Texto escaneado en el control
     */
    public void registraControl(String escaneado) {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.scan_carga_registrando));
        codigo = Utils.getCodigoControlEscaneado(escaneado);
        secreto = Utils.getSecretoControlEscaneado(escaneado);
        registroRepository.registraControl(codigo, secreto);
    }

    /**
     * Realiza una petición de abandono del recorrido actual.
     */
    public void confirmaAbandonoRecorrido() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.scan_carga_abandono));
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
