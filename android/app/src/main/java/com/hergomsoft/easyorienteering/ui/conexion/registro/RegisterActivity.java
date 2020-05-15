package com.hergomsoft.easyorienteering.ui.conexion.registro;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.ui.BackableActivity;

public class RegisterActivity extends BackableActivity {

    private EditText inputEmail;
    private EditText inputNombre;
    private EditText inputPassword;
    private ImageView fuerzaPass;
    private EditText inputPasswordConf;
    private ImageView confirmPass;
    private CheckBox checkAcepto;
    private ImageButton btnPoliticas;
    private Button btnRegistrar;

    private RegisterViewModel registerViewModel;

    private boolean passConfError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.registro_titulo));
        setContentView(R.layout.activity_registrarse);
        registerViewModel = ViewModelProviders.of(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        inputEmail = findViewById(R.id.registrarseEmail);
        inputNombre = findViewById(R.id.registrarseNombre);
        inputPassword = findViewById(R.id.registrarsePassword);
        fuerzaPass = findViewById(R.id.registrarseIndicadorPass);
        inputPasswordConf = findViewById(R.id.registrarsePasswordConf);
        confirmPass = findViewById(R.id.registrarseIndicadorConf);
        checkAcepto = findViewById(R.id.registrarseAcepto);
        btnPoliticas = findViewById(R.id.registrarsePoliticas);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setEnabled(false); // Deshabilitado por defecto
        passConfError = false;

        // Habilita el envío de los datos si estos son válidos
        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }

                btnRegistrar.setEnabled(registerFormState.isDataValid() && checkAcepto.isChecked());
                if (registerFormState.getEmailError() != null && !inputEmail.isFocused()) {
                    // TODO: Comprobar cómo aparece el error en el ET
                    inputEmail.setError(getString(registerFormState.getEmailError()));
                }
                if (registerFormState.getUsernameError() != null) {
                    inputNombre.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    inputPasswordConf.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getPasswordConfError() != null) {
                    if(registerFormState.getPasswordConfError()) {
                        // Las contraseñas no coinciden
                        if(!passConfError) {
                            // Mostrar imagen error
                            // TODO
                            inputPasswordConf.setError(getString(R.string.registro_pass_no_coinciden));

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
                refreshForm();
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
                refreshForm();
            }
        });

        // Botón de visión de políticas de uso y datos
        btnPoliticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, PoliticasActivity.class));
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

    public void refreshForm() {
        registerViewModel.registerDataChanged(
                inputEmail.getText().toString(), inputNombre.getText().toString(),
                inputPassword.getText().toString(), inputPasswordConf.getText().toString());
    }

}
