package com.hergomsoft.easyorienteering.ui.conexion.registro;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.ConexionState;
import com.hergomsoft.easyorienteering.data.model.Result;
import com.hergomsoft.easyorienteering.data.RegisterRepository;
import com.hergomsoft.easyorienteering.util.Utils;

public class RegisterViewModel extends ViewModel {
    private final int TIMEOUT_MENSAJE_EXITO = 2500;
    private final int TIMEOUT_MENSAJE_ERROR = 4000;

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<ConexionState> registerState = new MutableLiveData<>();
    private RegisterRepository registerRepository;

    RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<ConexionState> getRegisterState() {
        return registerState;
    }

    public void register(String email, String username, String password) {
        final ConexionState state = new ConexionState();
        registerState.setValue(state);

        // TODO Lanzar asíncronamente
        final Result<Void> result = registerRepository.register(email, username, password);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (result instanceof Result.Success) {
                    state.setEstado(ConexionState.ESTADO_EXITO_PRE);
                    registerState.setValue(state);

                    // Tras un pequeño lapso de tiempo, actualiza el estado para la redirección
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            state.setEstado(ConexionState.ESTADO_EXITO_FIN);
                            registerState.setValue(state);
                        }
                    }, TIMEOUT_MENSAJE_EXITO);
                } else {
                    state.setEstado(ConexionState.ESTADO_ERROR);
                    // Mensaje de error
                    // TODO
                    // state.setMensaje(R.string.*);
                    registerState.setValue(state);

                    // Tras un pequeño lapso de tiempo, oculta el diálogo
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            state.setEstado(ConexionState.ESTADO_OCULTO);
                            registerState.setValue(state);
                        }
                    }, TIMEOUT_MENSAJE_ERROR);
                }
            }
        }, 3000);

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

    /**
     * Comprueba si la dirección de email especificada ya está asociada a una cuenta.
     * @param email Dirección de email
     */
    public void checkEmailOcupado(String email) {
        // TODO: comprobar si el email ya está asociado a una cuenta
    }

}
