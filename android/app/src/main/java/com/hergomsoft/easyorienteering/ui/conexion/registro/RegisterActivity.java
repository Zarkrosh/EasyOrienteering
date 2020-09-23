package com.hergomsoft.easyorienteering.ui.conexion.registro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.ui.conexion.login.LoginActivity;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.Utils;

public class RegisterActivity extends BackableActivity {

    // FEATURE: Comprueba si el email está asociado a otra cuenta al perder el focus
    // FEATURE: Comprueba si el nombre de usuario ya está en uso

    private final int TIMEOUT_MENSAJE_EXITO = 2500;

    private EditText inputEmail;
    private EditText inputNombre;
    private EditText inputClub;
    private EditText inputPassword;
    private ImageView fuerzaPass;
    private EditText inputPasswordConf;
    private ImageView confirmPass;
    private CheckBox checkAcepto;
    private ImageButton btnPoliticas;
    private Button btnRegistrar;

    private RegisterViewModel viewModel;

    private boolean passConfError;

    private Activity activity; // Referencia para la ocultación de teclado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.registro_titulo));
        setContentView(R.layout.activity_registrarse);
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        activity = this;

        inputEmail = findViewById(R.id.registrarseEmail);
        inputNombre = findViewById(R.id.registrarseNombre);
        inputClub = findViewById(R.id.registrarseClub);
        inputPassword = findViewById(R.id.registrarsePassword);
        fuerzaPass = findViewById(R.id.registrarseIndicadorPass);
        inputPasswordConf = findViewById(R.id.registrarsePasswordConf);
        confirmPass = findViewById(R.id.registrarseIndicadorConf);
        checkAcepto = findViewById(R.id.registrarseAcepto);
        btnPoliticas = findViewById(R.id.registrarsePoliticas);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setEnabled(false); // Deshabilitado por defecto
        passConfError = false;

        setupObservadores();
        setupDialogoCarga();
        setupListeners();
    }

    /**
     * Configura el diálogo de uso general para operaciones de carga y notificaciones de éxito/error.
     */
    private void setupDialogoCarga() {
        DialogoCarga dialogoCarga = new DialogoCarga(RegisterActivity.this);
        dialogoCarga.setObservadorEstado(this, viewModel.getEstadoDialogo());
        dialogoCarga.setObservadorTitulo(this, viewModel.getTituloDialogo());
        dialogoCarga.setObservadorMensaje(this, viewModel.getMensajeDialogo());
    }

    private void setupObservadores() {
        // Habilita el envío de los datos si estos son válidos
        viewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }

                btnRegistrar.setEnabled(registerFormState.isDataValid() && checkAcepto.isChecked());
                if (registerFormState.getEmailError() != null && !inputEmail.isFocused()) {
                    inputEmail.setError(getString(registerFormState.getEmailError()));
                }
                if (registerFormState.getUsernameError() != null) {
                    inputNombre.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    inputPassword.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getPasswordConfError() != null) {
                    if(registerFormState.getPasswordConfError()) {
                        // Las contraseñas no coinciden
                        // Muestra imagen de error
                        confirmPass.setImageDrawable(getResources().getDrawable(R.drawable.img_incorrect));
                        inputPasswordConf.setError(getString(R.string.registro_pass_no_coinciden));
                        passConfError = true;

                    } else if(!inputPasswordConf.getText().toString().isEmpty()) {
                        // Las contraseñas coinciden
                        // Muestra imagen correcta
                        confirmPass.setImageDrawable(getResources().getDrawable(R.drawable.img_correct));
                        passConfError = false;

                    } else {
                        confirmPass.setImageDrawable(null);
                    }
                }
            }
        });

        // Muestra el diálogo de registro (o no) y sus mensajes
        viewModel.getRegisterState().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> registerResult) {
                switch (registerResult.status) {
                    case LOADING:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, getString(R.string.registro_registrando), null);
                        break;
                    case SUCCESS:
                        // Muestra que el registro ha sido exitoso
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_EXITO, getString(R.string.registro_registrada), "Ahora puedes entrar a tu cuenta con estos datos");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewModel.ocultaDialogoCarga();
                                // Tras registro exitoso redirige a la pantalla principal
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }
                        }, TIMEOUT_MENSAJE_EXITO);
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.registro_error), registerResult.message);
                        break;
                    default:
                        viewModel.ocultaDialogoCarga();
                }
            }
        });
    }

    private void setupListeners() {
        // Cuando se modifica un campo de datos, se actualiza el modelo
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                refreshForm();
            }
        };
        inputEmail.addTextChangedListener(afterTextChangedListener);
        inputNombre.addTextChangedListener(afterTextChangedListener);
        inputPassword.addTextChangedListener(afterTextChangedListener);
        inputPasswordConf.addTextChangedListener(afterTextChangedListener);

        // Indicador de fuerza de la contraseña
        TextWatcher passwordStrengthChecker = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // FEATURE: Cambia el indicador de fuerza de la contraseña en base a su largo y uso de diferentes tipos de caracteres.
            }
        };
        inputPasswordConf.addTextChangedListener(passwordStrengthChecker);

        // Checkbox de aceptación de políticas
        checkAcepto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Comprueba indirectamente el cambio del botón mediante un falso cambio en los datos
                refreshForm();
            }
        });

        // Botón de visión de políticas de uso y datos
        btnPoliticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Muestra la vista de políticas
                startActivity(new Intent(RegisterActivity.this, PoliticasActivity.class));
            }
        });

        // Botón de registro de cuenta
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Oculta teclado
                Utils.hideKeyboard(activity);

                viewModel.register(inputEmail.getText().toString(), inputNombre.getText().toString(),
                        inputClub.getText().toString(), inputPassword.getText().toString());
            }
        });
    }

    private void refreshForm() {
        viewModel.registerDataChanged(
            inputEmail.getText().toString(), inputNombre.getText().toString(),
            inputPassword.getText().toString(), inputPasswordConf.getText().toString());
    }

}
