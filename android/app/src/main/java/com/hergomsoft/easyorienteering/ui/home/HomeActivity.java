package com.hergomsoft.easyorienteering.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.ui.configuracion.ConfiguracionActivity;
import com.hergomsoft.easyorienteering.ui.miscarreras.MisCarrerasActivity;
import com.hergomsoft.easyorienteering.ui.scan.ScanActivity;
import com.hergomsoft.easyorienteering.util.CircleTransform;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout layoutPerfil;
    private TextView textUsername;
    private ImageButton btnPerfil;
    private Button btnMisCarreras;
    private Button btnExplorar;
    private Button btnUnirme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        layoutPerfil = findViewById(R.id.home_layoutPerfil);
        textUsername = findViewById(R.id.home_textUsername);
        btnPerfil = findViewById(R.id.home_btnPerfil);
        btnMisCarreras = findViewById(R.id.btnMisCarreras);
        btnExplorar = findViewById(R.id.btnExplorar);
        btnUnirme = findViewById(R.id.btnUnirme);

        // Comprueba si está en una carrera pendiente
        // TODO Si lo está, inicia la actividad de carrera



        // Carga datos del usuario
        // TODO
        textUsername.setText("Nombre Usuario");
        // Carga imagen circular
        Picasso.with(this).load(R.drawable.sample_user).transform(new CircleTransform()).into(btnPerfil);

        // Al pulsar el texto o botón se muestra la pantalla de perfil de usuario
        layoutPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ConfiguracionActivity.class));
            }
        });
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ConfiguracionActivity.class));
            }
        });

        // Al pulsar el botón, se muestra la lista de carreras en las que ha participado el usuario
        btnMisCarreras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MisCarrerasActivity.class));
            }
        });

        // Al pulsar el botón, se muestra una pantalla de exploración de circuitos y carreras
        btnExplorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                Toast.makeText(HomeActivity.this, "TODO Explorar", Toast.LENGTH_SHORT).show();
            }
        });

        // Al pulsar el botón, se muestra una pantalla de escaneo de QR de comienzo de recorrido
        btnUnirme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ScanActivity.class));
            }
        });

    }
}
