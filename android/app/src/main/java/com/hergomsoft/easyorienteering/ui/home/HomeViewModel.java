package com.hergomsoft.easyorienteering.ui.home;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;

public class HomeViewModel extends AndroidViewModelConCarga {

    private UsuarioRepository usuarioRepository;

    public HomeViewModel(Application app) {
        super(app);
        usuarioRepository = UsuarioRepository.getInstance(app);
    }

    public void compruebaRecorridoPendiente() {
        // TODO
    }

    public LiveData<Resource<Usuario>> cargaDatosUsuario() {
        // Obtiene el ID del usuario conectado
        long idUsuario = usuarioRepository.getIdUsuarioConectado();
        return usuarioRepository.getUsuario(idUsuario);
    }
}
