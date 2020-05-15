package com.hergomsoft.easyorienteering.ui.conexion.registro;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.Result;
import com.hergomsoft.easyorienteering.data.RegisterRepository;
import com.hergomsoft.easyorienteering.data.model.Utils;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private RegisterRepository registerRepository;

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
