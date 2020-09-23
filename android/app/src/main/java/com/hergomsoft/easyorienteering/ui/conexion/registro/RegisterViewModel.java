package com.hergomsoft.easyorienteering.ui.conexion.registro;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.Utils;

public class RegisterViewModel extends AndroidViewModelConCarga {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private UsuarioRepository usuarioRepository;

    public RegisterViewModel(Application app) {
        super(app);
        this.usuarioRepository = UsuarioRepository.getInstance(app);
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<Resource<String>> getRegisterState() {
        return usuarioRepository.getRegisterState();
    }

    public void register(String email, String username, String club, String password) {
        usuarioRepository.register(username, email, club, password);
    }

    public void registerDataChanged(String email, String username, String password, String passwordConf) {
        if (!Utils.emailValido(email)) {
            registerFormState.setValue(new RegisterFormState(R.string.registro_email_no_valido, null, null, null));
        } else if (!Utils.nombreUsuarioValido(username)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.registro_nombre_invalido, null, null));
        } else if (!Utils.isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.registro_pass_largo, null));
        } else if(!password.contentEquals(passwordConf)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, true));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

}
