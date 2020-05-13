package com.hergomsoft.easyorienteering.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Utils;
import com.hergomsoft.easyorienteering.ui.BackableActivity;

public class OlvidoActivity extends BackableActivity {

    private EditText inputEmailNombre;
    private ProgressBar progress;
    private TextView textResultado;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.olvido_titulo));
        setContentView(R.layout.activity_olvido);

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
                // Envia la petición de restablecimiento de contraseña
                // TODO
                Toast.makeText(OlvidoActivity.this, "[TODO] Petición de restablecimiento", Toast.LENGTH_SHORT).show();

                progress.setVisibility(View.VISIBLE);

                // Espera al resultado
                // TODO
                //progress.setVisibility(View.GONE);
                //textResultado.setText(...);
            }
        });
    }
}
