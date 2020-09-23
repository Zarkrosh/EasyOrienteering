package com.hergomsoft.easyorienteering.ui.configuracion;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.SingleLiveEvent;


public class ConfiguracionViewModel extends AndroidViewModelConCarga {

    private UsuarioRepository usuarioRepository;

    private SingleLiveEvent<Resource<String>> cambioNombreResponse;
    private SingleLiveEvent<Resource<String>> cambioClubResponse;
    private SingleLiveEvent<Resource<String>> cambioPasswordResponse;
    private SingleLiveEvent<Boolean> envioCambioNombreHabilitado;

    private String nuevoNombre;
    private String nuevoClub;

    public ConfiguracionViewModel(Application app) {
        super(app);
        usuarioRepository = UsuarioRepository.getInstance(app);
        cambioNombreResponse = usuarioRepository.getCambioNombreResponse();
        cambioClubResponse = usuarioRepository.getCambioClubResponse();
        cambioPasswordResponse = usuarioRepository.getCambioPasswordResponse();
        envioCambioNombreHabilitado = new SingleLiveEvent<>();
    }

    public SingleLiveEvent<Resource<String>> getCambioNombreResponse() { return cambioNombreResponse; }
    public SingleLiveEvent<Resource<String>> getCambioClubResponse() { return cambioClubResponse; }
    public SingleLiveEvent<Resource<String>> getCambioPasswordResponse() { return cambioPasswordResponse; }
    public SingleLiveEvent<Boolean> getEnvioCambioNombreHabilitado() { return envioCambioNombreHabilitado; }
    public SingleLiveEvent<Resource<String>> getEstadoLogout() { return usuarioRepository.getEstadoLogout(); }
    public SingleLiveEvent<Resource<String>> getEstadoBorradoCuenta() { return usuarioRepository.getEstadoBorradoCuenta(); }

    public LiveData<Resource<Usuario>> cargaDatosUsuario() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO,"", getApplication().getApplicationContext().getString(R.string.cargando_datos));
        // Obtiene el ID del usuario conectado
        long idUsuario = usuarioRepository.getIdUsuarioConectado();
        return usuarioRepository.getUsuario(idUsuario);
    }

    public void actualizaInputCambioNombre(String nuevoNombre) {
        this.nuevoNombre = nuevoNombre;
        if(nuevoNombre.trim().isEmpty()) {
            envioCambioNombreHabilitado.postValue(false);
        } else {
            if(!envioCambioNombreHabilitado.getValue()) envioCambioNombreHabilitado.postValue(true);
        }
    }

    public void actualizaInputCambioClub(String nuevoClub) {
        this.nuevoClub = nuevoClub;
        // No hay tratamiento, se puede no tener club (vacío)
    }

    public void realizaCambioNombreUsuario() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.conf_cambiando_nombre));
        usuarioRepository.cambiaNombreUsuario(nuevoNombre);
    }

    public void realizaCambioClub() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.conf_cambiando_club));
        usuarioRepository.cambiaClub(nuevoClub);
    }

    public void realizaCambioPassword(String prevPass, String nuevaPass) {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.conf_cambiando_password));
        usuarioRepository.cambiaPassword(prevPass, nuevaPass);
    }

    public void cerrarSesion() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.conf_cerrando_sesion));
        usuarioRepository.logout();
    }

    public void confirmaBorradoCuenta() {
        actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getApplication().getApplicationContext().getString(R.string.conf_borrando_cuenta));
        usuarioRepository.borrarCuenta();
    }
}
