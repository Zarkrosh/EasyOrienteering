package com.hergomsoft.easyorienteering.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.repositories.CarreraRepository;
import com.hergomsoft.easyorienteering.ui.scan.ScanActivity;
import com.hergomsoft.easyorienteering.ui.splash.SplashActivity;
import com.hergomsoft.easyorienteering.util.CircleTransform;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout layoutPerfil;
    private TextView textUsername;
    private ImageButton btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        layoutPerfil = findViewById(R.id.home_layoutPerfil);
        textUsername = findViewById(R.id.home_textUsername);
        btnPerfil = findViewById(R.id.home_btnPerfil);

        // TEST
        final TextView respCarrera = findViewById(R.id.respCarrera);
        Button btnScan = findViewById(R.id.btnScan);
        Button btnReq = findViewById(R.id.btnReq);
        CarreraRepository repository = new CarreraRepository();

        // Nombre de usuario
        textUsername.setText("Nombre Usuario");
        // Carga imagen circular
        Picasso.with(this).load(R.drawable.sample_user).transform(new CircleTransform()).into(btnPerfil);

        // Al pulsar el texto o botón se muestra la pantalla de perfil de usuario
        layoutPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "[TODO] Pantalla de perfil", Toast.LENGTH_SHORT).show();
                // TODO
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ScanActivity.class));
            }
        });

        btnReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repository.buscaCarreraPorId(1);
            }
        });

        repository.getCarreraResponse().observe(this, new Observer<Carrera>() {
            @Override
            public void onChanged(Carrera carrera) {
                if(carrera != null) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    respCarrera.setText(gson.toJson(carrera));
                } else {
                    respCarrera.setText("Error");
                }
            }
        });

    }
}
