package com.hergomsoft.easyorienteering.ui.configuracion;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.SingleLiveEvent;


public class ConfiguracionViewModel extends AndroidViewModelConCarga {

    private UsuarioRepository usuarioRepository;

    private SingleLiveEvent<Recurso<String>> cambioNombreResponse;
    private SingleLiveEvent<Recurso<String>> cambioClubResponse;
    private SingleLiveEvent<Boolean> envioCambioNombreHabilitado;

    private String nuevoNombre;
    private String nuevoClub;

    public ConfiguracionViewModel(Application app) {
        super(app);
        usuarioRepository = UsuarioRepository.getInstance(app);
        cambioNombreResponse = usuarioRepository.getCambioNombreResponse();
        cambioClubResponse = usuarioRepository.getCambioClubResponse();
        envioCambioNombreHabilitado = new SingleLiveEvent<>();
    }

    public SingleLiveEvent<Recurso<String>> getCambioNombreResponse() { return cambioNombreResponse; }
    public SingleLiveEvent<Recurso<String>> getCambioClubResponse() { return cambioClubResponse; }
    public SingleLiveEvent<Boolean> getEnvioCambioNombreHabilitado() { return envioCambioNombreHabilitado; }
    public long getIdUsuarioConectado() { return usuarioRepository.getIdUsuarioConectado(); }

    public LiveData<Resource<Usuario>> cargaDatosUsuario() {
        // Obtiene el ID del usuario conectado
        // TODO
        long idUsuario = Constants.ID_USUARIO_PRUEBA;
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
        usuarioRepository.cambiaNombreUsuario(nuevoNombre);
    }
    public void realizaCambioClub() {
        usuarioRepository.cambiaClub(nuevoClub);
    }
}
