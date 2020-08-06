package com.hergomsoft.easyorienteering.ui.conexion.registro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.hergomsoft.easyorienteering.data.model.ConexionState;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.VisualUtils;
import com.hergomsoft.easyorienteering.ui.home.HomeActivity;

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
    private RegisterDialog dialog;

    private RegisterViewModel viewModel;

    private boolean passConfError;

    private Activity activity; // Referencia para la ocultación de teclado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.registro_titulo));
        setContentView(R.layout.activity_registrarse);
        viewModel = ViewModelProviders.of(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);
        activity = this;

        inputEmail = findViewById(R.id.registrarseEmail);
        inputNombre = findViewById(R.id.registrarseNombre);
        inputPassword = findViewById(R.id.registrarsePassword);
        fuerzaPass = findViewById(R.id.registrarseIndicadorPass);
        inputPasswordConf = findViewById(R.id.registrarsePasswordConf);
        confirmPass = findViewById(R.id.registrarseIndicadorConf);
        checkAcepto = findViewById(R.id.registrarseAcepto);
        btnPoliticas = findViewById(R.id.registrarsePoliticas);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        dialog = new RegisterDialog(RegisterActivity.this);
        // Fondo transparente para los bordes redondeados
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnRegistrar.setEnabled(false); // Deshabilitado por defecto
        passConfError = false;

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
                    // TODO: Mejorar la lógica de esto
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
        viewModel.getRegisterState().observe(this, new Observer<ConexionState>() {
            @Override
            public void onChanged(ConexionState registerResult) {
                switch (registerResult.getEstado()) {
                    case ConexionState.ESTADO_CARGANDO:
                        dialog.muestraMensajeRegistrando();
                        break;
                    case ConexionState.ESTADO_EXITO_PRE:
                        // Muestra que el registro ha sido exitoso
                        dialog.muestraMensajeExito();
                        break;
                    case ConexionState.ESTADO_EXITO_FIN:
                        // Tras registro exitoso redirige a la pantalla principal
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();
                        break;
                    case ConexionState.ESTADO_ERROR:
                        Integer idMensaje = registerResult.getMensaje();
                        String mensaje = "";
                        if(idMensaje != null) mensaje = getString(idMensaje);
                        dialog.muestraMensajeError(mensaje);
                        break;
                    case ConexionState.ESTADO_OCULTO:
                    default:
                        dialog.dismiss();
                }
            }
        });

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
                // TODO: Cambia el indicador de fuerza de la contraseña en base a su largo y uso de diferentes tipos de caracteres.
            }
        };
        inputPasswordConf.addTextChangedListener(passwordStrengthChecker);

        // Comprueba si el email está asociado a otra cuenta al perder el focus (TODO No funciona correctamente)
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    viewModel.checkEmailOcupado(inputEmail.getText().toString());
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
                // Muestra la vista de políticas
                startActivity(new Intent(RegisterActivity.this, PoliticasActivity.class));
            }
        });

        // Botón de registro de cuenta
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Oculta teclado
                VisualUtils.hideKeyboard(activity);

                Toast.makeText(RegisterActivity.this, "[TODO] Registrando cuenta...", Toast.LENGTH_SHORT).show();
                viewModel.register(inputEmail.getText().toString(),
                    inputNombre.getText().toString(), inputPassword.getText().toString());
            }
        });
    }

    private void refreshForm() {
        viewModel.registerDataChanged(
            inputEmail.getText().toString(), inputNombre.getText().toString(),
            inputPassword.getText().toString(), inputPasswordConf.getText().toString());
    }

}
