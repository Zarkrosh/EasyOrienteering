package com.hergomsoft.easyorienteering.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.hergomsoft.easyorienteering.data.LoginRepository;
import com.hergomsoft.easyorienteering.data.Result;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.R;

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

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.conexion_nombre_email_invalido, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.conexion_password_largo));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // Comprueba nombre de usuario o email válido
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // Comprueba requisitos de seguridad de la contraseña
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= MIN_PASSWORD_LENGTH;
    }
}
