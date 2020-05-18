package com.hergomsoft.easyorienteering.ui.conexion.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.data.LoginRepository;
import com.hergomsoft.easyorienteering.model.Result;
import com.hergomsoft.easyorienteering.model.Usuario;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.model.Utils;

public class LoginViewModel extends ViewModel {
    private final int MIN_PASSWORD_LENGTH = 8;
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;


    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<Usuario> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            Usuario data = ((Result.Success<Usuario>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getNombre())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_incorrecto));
        }
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
