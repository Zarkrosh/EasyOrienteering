package com.hergomsoft.easyorienteering.ui.scan;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.data.api.ApiClient;
import com.hergomsoft.easyorienteering.data.api.responses.MessageResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Control;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.Registro;
import com.hergomsoft.easyorienteering.data.repositories.RegistroRepository;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.Utils;

import java.io.File;

import static android.Manifest.permission.CAMERA;

public class ScanViewModel extends AndroidViewModelConCarga {
    // Permisos que utiliza la actividad
    private String[] permisosNecesarios = new String[]{ CAMERA };

    // Modos de las vistas
    public enum ModoEscaneo { INICIO_RECORRIDO, CARRERA };
    public enum ModoVista { ESCANEO, MAPA };

    // Datos de control escaneado
    private String codigo;
    private Long idRecorrido;
    private String secreto;
    private String ultimoEscaneado;

    // Repositorios
    private RegistroRepository registroRepository;
    private UsuarioRepository usuarioRepository;

    // LiveDatas
    private LiveData<Resource<Boolean>> peticionPendiente;
    private LiveData<Resource<Registro>> registroResponse;
    private LiveData<Resource<MessageResponse>> abandonoResponse;
    // MutableLiveDatas
    private MutableLiveData<ModoVista> modoVista;
    private MutableLiveData<ModoEscaneo> modoEscaneo;
    private MutableLiveData<Boolean> estadoCapturaCamara;
    // MediatorLiveData
    private MediatorLiveData<Resource<File>> mapaResponse;


    public ScanViewModel(Application app) {
        super(app);
        registroRepository = RegistroRepository.getInstance(app);
        usuarioRepository = UsuarioRepository.getInstance(app);
        peticionPendiente = registroRepository.getPendienteResponse();
        registroResponse = registroRepository.getRegistroResponse();
        abandonoResponse = registroRepository.getAbandonoResponse();
        modoVista = new MutableLiveData<>(ModoVista.ESCANEO);
        modoEscaneo = new MutableLiveData<>(ModoEscaneo.INICIO_RECORRIDO);
        estadoCapturaCamara = new MutableLiveData<>(false);
        mapaResponse = new MediatorLiveData<>();
        ultimoEscaneado = "";
    }

    public LiveData<Resource<Boolean>> getCarreraPendienteResponse() { return peticionPendiente; }
    public LiveData<Resource<Registro>> getRegistroResponse() { return registroResponse; }
    public LiveData<Resource<MessageResponse>> getAbandonoResponse() { return abandonoResponse; }
    public LiveData<Resource<File>> getMapaResponse() { return mapaResponse; }
    public LiveData<ModoVista> getAlternadoVistas() { return modoVista; }
    public LiveData<ModoEscaneo> getModoEscaneo() { return modoEscaneo; }
    public LiveData<Boolean> getEstadoCapturaCamara() { return estadoCapturaCamara; }

    public Carrera getCarreraActual() { return registroRepository.getCarreraActual(); }
    public Recorrido getRecorridoActual() { return registroRepository.getRecorridoActual(); }
    public LiveData<Control> getSiguienteControl() { return registroRepository.getSiguienteControl(); }

    /**
     * Devuelve true si el nuevo escaneo es el mismo que el anterior.
     * @param nuevoEscaneado Nuevo dato escaneado
     * @return True si son el mismo, false si no
     */
    public boolean checkUltimoEscaneado(String nuevoEscaneado) {
        boolean res = nuevoEscaneado.contentEquals(ultimoEscaneado);
        this.ultimoEscaneado = nuevoEscaneado;
        return res;
    }
    public void clearUltimoEscaneado() { this.ultimoEscaneado = ""; }

    public void alternarModoVista() {
        if(modoVista.getValue() != null) {
            switch (modoVista.getValue()) {
                case MAPA:
                    modoVista.postValue(ModoVista.ESCANEO);
                    break;
                case ESCANEO:
                default:
                    modoVista.postValue(ModoVista.MAPA);
                    break;
            }
        }
    }
    public void pasaAModoCarrera() {
        modoEscaneo.postValue(ModoEscaneo.CARRERA);
        cargaMapaRecorrido();
    }

    /**
     * Realiza una petición de comprobación de recorrido pendiente.
     */
    public void compruebaRecorridoPendiente() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.scan_carga_pendiente));
        registroRepository.comprobarRecorridoPendiente();
    }

    /**
     * Realiza una petición de confirmación de recorrido. En esta petición se registra el control
     * de salida del recorrido, si ha sido exitoso.
     */
    public void confirmaInicioRecorrido() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.scan_carga_inicio));
        registroRepository.iniciaRecorrido(codigo, idRecorrido, secreto);
    }

    public void cargaMapaRecorrido() {
        Recorrido actual = registroRepository.getRecorridoActual();
        if(actual != null && actual.tieneMapa()) {
            // Genera petición con el token de autorización
            String token = usuarioRepository.getTokenUsuarioConectado();
            if(token == null) token = "";
            GlideUrl url = new GlideUrl(
                    ApiClient.BASE_URL + ApiClient.MAPA_URL + actual.getId(),
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build()
            );

            Glide.with(getApplication())
                .download(url)
                .into(new SimpleTarget<File>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        mapaResponse.postValue(new Resource<>(Resource.Status.LOADING, null, null));
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        mapaResponse.postValue(new Resource<File>(Resource.Status.ERROR, null, "Error en la descarga del mapa"));
                    }

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        mapaResponse.postValue(new Resource<File>(Resource.Status.SUCCESS, resource, null));
                    }
                });
        }
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
     * Devuelve true si el usuario ya ha registrado el control.
     * @param codigo Código de control
     * @return True si ya ha registrado control
     */
    public boolean haRegistradoControl(String codigo) {
        return registroRepository.haRegistradoControl(codigo);
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
        idRecorrido = Utils.getIdentificadorRecorridoEscaneado(escaneado);
        secreto = Utils.getSecretoControlEscaneado(escaneado);

        return codigo != null && idRecorrido != null && secreto != null;
    }

    public void setCapturaCamara(boolean capturar) {
        estadoCapturaCamara.postValue(capturar);
    }

    public void resetDatos() {
        registroRepository.resetDatos();
        modoVista.setValue(ModoVista.ESCANEO);
        modoEscaneo.setValue(ModoEscaneo.INICIO_RECORRIDO);
    }

    /**
     * Devuelve la lista de permisos necesarios para la actividad.
     * @return Permisos
     */
    public String[] getPermisosNecesarios() {
        return permisosNecesarios;
    }

}
