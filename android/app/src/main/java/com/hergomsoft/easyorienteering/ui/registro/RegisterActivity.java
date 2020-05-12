package com.hergomsoft.easyorienteering.ui.registro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnAtras;
    private EditText inputEmail;
    private EditText inputNombre;
    private TextView textErrorNombre;
    private EditText inputPassword;
    private ImageView fuerzaPass;
    private EditText inputPasswordConf;
    private ImageView confirmPass;
    private TextView textErrorPasses;
    private CheckBox checkAcepto;
    private Button btnRegistrar;

    private RegisterViewModel registerViewModel;

    private boolean passConfError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        registerViewModel = ViewModelProviders.of(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        btnAtras = findViewById(R.id.registrarseBtnAtras);
        inputEmail = findViewById(R.id.registrarseEmail);
        inputNombre = findViewById(R.id.registrarseNombre);
        textErrorNombre = findViewById(R.id.registrarseErrorNombre);
        inputPassword = findViewById(R.id.registrarsePassword);
        fuerzaPass = findViewById(R.id.registrarseIndicadorPass);
        inputPasswordConf = findViewById(R.id.registrarsePasswordConf);
        confirmPass = findViewById(R.id.registrarseIndicadorConf);
        textErrorPasses = findViewById(R.id.registrarseErrorPasses);
        checkAcepto = findViewById(R.id.registrarseAcepto);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setEnabled(false); // Deshabilitado por defecto
        passConfError = false;

        // Botón para volver a la actividad anterior
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Habilita el envío de los datos si estos son válidos
        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }

                btnRegistrar.setEnabled(registerFormState.isDataValid() && checkAcepto.isChecked());
                if (registerFormState.getEmailError() != null) {
                    // TODO: Comprobar cómo aparece el error en el ET
                    inputNombre.setError(getString(registerFormState.getEmailError()));
                }
                if (registerFormState.getUsernameError() != null) {
                    textErrorNombre.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    textErrorPasses.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getPasswordConfError() != null) {
                    if(registerFormState.getPasswordConfError()) {
                        // Las contraseñas no coinciden
                        if(!passConfError) {
                            // TODO: Mostrar imagen error
                            passConfError = true;
                        }
                    } else {
                        // Las contraseñas coinciden
                        // TODO: Mostrar imagen correcto
                    }
                }

                // TODO Comprobar que la confirmación es igual, ¿de este modo?
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(
                    inputEmail.getText().toString(), inputNombre.getText().toString(),
                    inputPassword.getText().toString(), inputPasswordConf.getText().toString());
            }
        };
        inputEmail.addTextChangedListener(afterTextChangedListener);
        inputNombre.addTextChangedListener(afterTextChangedListener);
        inputPassword.addTextChangedListener(afterTextChangedListener);
        inputPasswordConf.addTextChangedListener(afterTextChangedListener);

        // Indicador de fuerza de la contraseña (TODO)
        TextWatcher passwordStrengthChecker = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: Cambia el indicador de fuerza de la contraseña en base a su largo y uso de diferentes tipos de caracteres.
            }
        };
        inputPasswordConf.addTextChangedListener(passwordStrengthChecker);

        // Comprobación de email existente
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // TODO: comprobar si el email ya está asociado a una cuenta
                }
            }
        });

        // Comprobación de nombre de usuario existente
        inputNombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // TODO: comprobar si el nombre ya está asociado a una cuenta
                }
            }
        });

        // Checkbox de aceptación de políticas
        checkAcepto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Comprueba indirectamente el cambio del botón mediante un falso cambio en los datos
                registerViewModel.registerDataChanged(
                        inputEmail.getText().toString(), inputNombre.getText().toString(),
                        inputPassword.getText().toString(), inputPasswordConf.getText().toString());
            }
        });

        // Botón de registro de cuenta
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                Toast.makeText(RegisterActivity.this, "[TODO] Registrando cuenta...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
