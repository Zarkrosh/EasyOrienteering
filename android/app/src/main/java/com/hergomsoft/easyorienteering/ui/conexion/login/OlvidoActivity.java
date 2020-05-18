package com.hergomsoft.easyorienteering.ui.conexion.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.model.Utils;
import com.hergomsoft.easyorienteering.ui.BackableActivity;
import com.hergomsoft.easyorienteering.ui.VisualUtils;

public class OlvidoActivity extends BackableActivity {

    private EditText inputEmailNombre;
    private ProgressBar progress;
    private TextView textResultado;
    private Button btnEnviar;

    private Activity activity; // Referencia para la ocultación de teclado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.olvido_titulo));
        setContentView(R.layout.activity_olvido);

        activity = this;
        inputEmailNombre = findViewById(R.id.olvidoEmailNombre);
        progress = findViewById(R.id.olvidoLoading);
        textResultado = findViewById(R.id.olvidoResultado);
        btnEnviar = findViewById(R.id.olvidoEnviar);

        // Carga valor del login
        String emailNombre = getIntent().getStringExtra(LoginActivity.ARG_EMAIL_NOMBRE);
        if(emailNombre != null) {
            inputEmailNombre.setText(emailNombre.trim());
            String value = inputEmailNombre.getText().toString().trim();
            btnEnviar.setEnabled(Utils.emailValido(value) || Utils.nombreUsuarioValido(value));
        }

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String value = inputEmailNombre.getText().toString().trim();
                btnEnviar.setEnabled(Utils.emailValido(value) || Utils.nombreUsuarioValido(value));
            }
        };
        inputEmailNombre.addTextChangedListener(afterTextChangedListener);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Oculta el teclado y el botón de envío
                VisualUtils.hideKeyboard(activity);
                btnEnviar.setVisibility(View.GONE);
                // Muestra el indicador de carga
                progress.setVisibility(View.VISIBLE);

                // Envia la petición de restablecimiento de contraseña
                // TODO
                Toast.makeText(OlvidoActivity.this, "[TODO] Petición de restablecimiento", Toast.LENGTH_SHORT).show();

                // Espera al resultado
                // TODO
                //progress.setVisibility(View.GONE);
                //textResultado.setText(...);
            }
        });
    }

}
