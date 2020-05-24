package com.hergomsoft.easyorienteering.ui.conexion.login;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.data.LoginRepository;
import com.hergomsoft.easyorienteering.model.ConexionState;
import com.hergomsoft.easyorienteering.model.Result;
import com.hergomsoft.easyorienteering.model.Usuario;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.model.Utils;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<ConexionState> loginState = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<ConexionState> getLoginState() {
        return loginState;
    }

    public void login(String username, String password) {
        final ConexionState state = new ConexionState();
        loginState.setValue(state);

        // TODO Lanzar asíncronamente
        final Result<Usuario> result = loginRepository.login(username, password);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (result instanceof Result.Success) {
                    // Indica éxito en el login
                    state.setEstado(ConexionState.ESTADO_EXITO_FIN);
                    loginState.setValue(state);
                } else {
                    state.setEstado(ConexionState.ESTADO_ERROR);
                    // Mensaje de error dependiendo del motivo
                    // TODO
                    state.setMensaje(R.string.login_incorrecto); // Mientras
                    loginState.setValue(state);
                }
            }
        }, 3000);
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
