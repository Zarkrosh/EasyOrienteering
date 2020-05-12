package com.hergomsoft.easyorienteering.ui.registro;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.Result;
import com.hergomsoft.easyorienteering.data.RegisterRepository;

public class RegisterViewModel extends ViewModel {
    private final int MIN_PASSWORD_LENGTH = 8;
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private RegisterRepository registerRepository;

    private final String REGEX_USERNAME = "^a-zA-Z0-9_$";
    // https://emailregex.com/ (RFC 5322 Official Standard)
    private final String REGEX_EMAIL = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";

    RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String email, String username, String password) {
        // can be launched in a separate asynchronous job
        Result<Void> result = registerRepository.register(email, username, password);

        if (result instanceof Result.Success) {
            // TODO Mostrar éxito
        } else {
            // TODO Mostrar error
        }
    }

    public void registerDataChanged(String email, String username, String password, String passwordConf) {
        if (!emailValido(email)) {
            // TODO ¿Largo 0?
            registerFormState.setValue(new RegisterFormState(R.string.TODO, null, null, null));
        } else if (!nombreUsuarioValido(username)) {
            // TODO ¿Largo 0?
            registerFormState.setValue(new RegisterFormState(null, R.string.TODO, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.TODO, null));
        } else if(!password.contentEquals(passwordConf)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, true));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    // Comprueba nombre de usuario válido
    private boolean nombreUsuarioValido(String username) {
        return username != null && username.matches(REGEX_USERNAME);
    }

    // Comprueba email válido
    private boolean emailValido(String email) {
        return email != null && email.matches(REGEX_EMAIL);
    }

    // Comprueba requisitos de seguridad de la contraseña
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= MIN_PASSWORD_LENGTH;
    }
}
