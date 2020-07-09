package com.hergomsoft.easyorienteering.ui.conexion.login;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.ConexionState;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.VisualUtils;
import com.hergomsoft.easyorienteering.ui.home.HomeActivity;

public class LoginActivity extends BackableActivity {

    public final static String ARG_EMAIL_NOMBRE = "ARG_EMAIL_NOMBRE";

    private EditText inputEmailNombre;
    private EditText inputPassword;
    private TextView textError;
    private Button btnConectar;
    private ProgressBar loadingProgressBar;
    private TextView olvidoPass;

    private LoginViewModel loginViewModel;

    private Activity activity; // Referencia para la ocultación de teclado

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.login_titulo));
        setContentView(R.layout.activity_conectarse);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        activity = this;

        inputEmailNombre = findViewById(R.id.conectarseEmailNombre);
        inputPassword = findViewById(R.id.conectarsePassword);
        textError = findViewById(R.id.conectarseError);
        btnConectar = findViewById(R.id.btnConectar);
        loadingProgressBar = findViewById(R.id.loading);
        olvidoPass = findViewById(R.id.conectarseOlvido);
        olvidoPass.setPaintFlags(olvidoPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // Subrayado

        // Color del spinner circular
        loadingProgressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }

                btnConectar.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    inputEmailNombre.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    inputPassword.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginState().observe(this, new Observer<ConexionState>() {
            @Override
            public void onChanged(ConexionState loginState) {
                switch (loginState.getEstado()) {
                    case ConexionState.ESTADO_CARGANDO:
                        // Oculta los botones de olvido y conexión
                        olvidoPass.setVisibility(View.GONE);
                        btnConectar.setVisibility(View.GONE);
                        // Muestra el indicador de carga
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case ConexionState.ESTADO_EXITO_FIN:
                        // Tras login exitoso redirige a la pantalla principal
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                        break;
                    case ConexionState.ESTADO_ERROR:
                        Integer idMensaje = loginState.getMensaje();
                        String mensaje = "";
                        if(idMensaje != null) mensaje = getString(idMensaje);
                        textError.setText(mensaje);
                        // break; // No se hace break para ejecutar lo del default
                    default:
                        // Muestra los botones de olvido y conexión
                        olvidoPass.setVisibility(View.VISIBLE);
                        btnConectar.setVisibility(View.VISIBLE);
                        // Oculta el indicador de carga
                        loadingProgressBar.setVisibility(View.GONE);
                }
            }

        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(inputEmailNombre.getText().toString(),
                        inputPassword.getText().toString());
            }
        };
        inputEmailNombre.addTextChangedListener(afterTextChangedListener);
        inputPassword.addTextChangedListener(afterTextChangedListener);
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Al pulsar enviar desde el input de la contraseña, se realiza el login
                    doLogin(inputEmailNombre.getText().toString(),
                            inputPassword.getText().toString());
                }
                return false;
            }
        });

        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                doLogin(inputEmailNombre.getText().toString(),
                        inputPassword.getText().toString());
            }
        });

        olvidoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OlvidoActivity.class);
                intent.putExtra(ARG_EMAIL_NOMBRE, inputEmailNombre.getText().toString().trim());
                startActivity(intent);
            }
        });
    }

    private void doLogin(String username, String password) {
        // Oculta teclado
        VisualUtils.hideKeyboard(activity);

        textError.setText("");
        loginViewModel.login(username, password);
    }

}
