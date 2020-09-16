package com.hergomsoft.easyorienteering.ui.conexion.login;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.repositories.UsuarioRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.Utils;

public class LoginViewModel extends AndroidViewModelConCarga {
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    UsuarioRepository usuarioRepository;

    public LoginViewModel(Application app) {
        super(app);
        this.usuarioRepository = UsuarioRepository.getInstance(app);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }
    LiveData<Resource<String>> getLoginState() { return usuarioRepository.getLoginState(); }

    public void login(String username, String password) {
        usuarioRepository.login(username, password);
    }

    public void loginDataChanged(String emailUsername, String password) {
        if (!Utils.nombreUsuarioValido(emailUsername) && !Utils.emailValido(emailUsername)) {
            loginFormState.setValue(new LoginFormState(R.string.conexion_nombre_email_invalido, null));
        } else if (!Utils.isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.conexion_password_largo));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

}
