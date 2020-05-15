package com.hergomsoft.easyorienteering.ui.conexion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.ui.conexion.login.LoginActivity;
import com.hergomsoft.easyorienteering.ui.conexion.registro.RegisterActivity;

public class ConexionActivity extends AppCompatActivity {

    private LinearLayout btnGoogle;
    private Button btnRegistrar;
    private Button btnConectar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_conexion);

        btnGoogle = findViewById(R.id.llGoogle);
        btnRegistrar = findViewById(R.id.conexionBtnRegistrar);
        btnConectar = findViewById(R.id.conexionBtnConectar);

        btnGoogle.setEnabled(true);
        btnRegistrar.setEnabled(true);
        btnConectar.setEnabled(true);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: buscar info sobre la sincronización con Google
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConexionActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConexionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
