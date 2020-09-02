package com.hergomsoft.easyorienteering.ui.perfil;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Resource;

public class PerfilViewModel extends AndroidViewModelConCarga {

    UsuarioRepository usuarioRepository;

    public PerfilViewModel(Application app) {
        super(app);
        usuarioRepository = UsuarioRepository.getInstance(app);
    }

    public LiveData<Resource<Usuario>> cargaDatosUsuario(long idUsuario) {
        return usuarioRepository.getUsuario(idUsuario);
    }
}
