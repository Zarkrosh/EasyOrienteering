package com.hergomsoft.easyorienteering.ui.login.olvido;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hergomsoft.easyorienteering.R;

public class OlvidoActivity extends AppCompatActivity {

    private EditText inputEmailNombre;
    private ProgressBar progress;
    private TextView textResultado;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvido);

        inputEmailNombre = findViewById(R.id.olvidoEmailNombre);
        progress = findViewById(R.id.olvidoLoading);
        textResultado = findViewById(R.id.olvidoResultado);
        btnEnviar = findViewById(R.id.olvidoEnviar);

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
                btnEnviar.setEnabled(inputEmailNombre.getText().toString().trim().isEmpty());
            }
        };
        inputEmailNombre.addTextChangedListener(afterTextChangedListener);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Envia la petición de restablecimiento de contraseña
                // TODO

                progress.setVisibility(View.VISIBLE);

                // Espera al resultado
                // TODO
                //progress.setVisibility(View.GONE);
                //textResultado.setText(...);
            }
        });
    }
}
