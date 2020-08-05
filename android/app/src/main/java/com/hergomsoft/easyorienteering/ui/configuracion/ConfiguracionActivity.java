package com.hergomsoft.easyorienteering.ui.configuracion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.util.BackableActivity;

public class ConfiguracionActivity extends BackableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.conf_configuracion));
        setContentView(R.layout.activity_configuracion);

        ImageButton btnFotoPerfil = findViewById(R.id.conf_foto_perfil);
        TextView tvNombre = findViewById(R.id.conf_nombre_usuario);
        TextView tvClub = findViewById(R.id.conf_club_usuario);
        Button btnVerPerfil = findViewById(R.id.conf_btn_ver_perfil);
        Button btnCambiarNombre = findViewById(R.id.conf_btn_cambiar_nombre);
        Button btnCambiarClub = findViewById(R.id.conf_btn_cambiar_club);
        Button btnCambiarContrasena = findViewById(R.id.conf_btn_cambiar_contrasena);
        Button btnResumen = findViewById(R.id.conf_btn_resumen);
        Button btnCompartirApp = findViewById(R.id.conf_btn_compartir_app);
        Button btnBorrarCuenta = findViewById(R.id.conf_btn_borrar_cuenta);
        Button btnCerrarSesion = findViewById(R.id.conf_btn_cerrar_sesion);

        // DEBUG
        View.OnClickListener todoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConfiguracionActivity.this, "TODO", Toast.LENGTH_SHORT).show();
            }
        };

        btnFotoPerfil.setOnClickListener(todoListener);
        btnVerPerfil.setOnClickListener(todoListener);
        btnCambiarNombre.setOnClickListener(todoListener);
        btnCambiarClub.setOnClickListener(todoListener);
        btnCambiarContrasena.setOnClickListener(todoListener);
        btnResumen.setOnClickListener(todoListener);
        btnCompartirApp.setOnClickListener(todoListener);
        btnBorrarCuenta.setOnClickListener(todoListener);
        btnCerrarSesion.setOnClickListener(todoListener);

        // TODO Cargar datos usuario

    }
}