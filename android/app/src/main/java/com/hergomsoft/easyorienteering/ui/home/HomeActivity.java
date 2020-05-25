package com.hergomsoft.easyorienteering.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
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

    }
}
