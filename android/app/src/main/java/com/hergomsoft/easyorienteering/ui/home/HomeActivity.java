package com.hergomsoft.easyorienteering.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.ui.configuracion.ConfiguracionActivity;
import com.hergomsoft.easyorienteering.ui.explorar.ExplorarActivity;
import com.hergomsoft.easyorienteering.ui.miscarreras.MisCarrerasActivity;
import com.hergomsoft.easyorienteering.ui.scan.ScanActivity;
import com.hergomsoft.easyorienteering.util.CircleTransform;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.util.Resource;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel viewModel;

    private LinearLayout layoutPerfil;
    private TextView textUsername;
    //private ImageButton btnPerfil;
    private Button btnMisCarreras;
    private Button btnExplorar;
    private Button btnUnirme;

    private DialogoCarga dialogoCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        layoutPerfil = findViewById(R.id.home_layoutPerfil);
        textUsername = findViewById(R.id.home_textUsername);
        //btnPerfil = findViewById(R.id.home_btnPerfil);
        btnMisCarreras = findViewById(R.id.btnMisCarreras);
        btnExplorar = findViewById(R.id.btnExplorar);
        btnUnirme = findViewById(R.id.btnUnirme);

        setupListeners();
        setupDialogoCarga();
        setupObservadores();
    }

    private void setupListeners() {
        // Al pulsar el texto o botón se muestra la pantalla de perfil de usuario
        layoutPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ConfiguracionActivity.class));
            }
        });
        /*
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ConfiguracionActivity.class));
            }
        });*/

        // Al pulsar el botón, se muestra la lista de carreras en las que ha participado el usuario
        btnMisCarreras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MisCarrerasActivity.class));
            }
        });

        // Al pulsar el botón, se muestra una pantalla de exploración carreras
        btnExplorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ExplorarActivity.class));
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

    /**
     * Configura el diálogo de uso general para operaciones de carga y notificaciones de éxito/error.
     */
    private void setupDialogoCarga() {
        dialogoCarga = new DialogoCarga(this);
        dialogoCarga.setObservadorEstado(this, viewModel.getEstadoDialogo());
        dialogoCarga.setObservadorTitulo(this, viewModel.getTituloDialogo());
        dialogoCarga.setObservadorMensaje(this, viewModel.getMensajeDialogo());
    }

    private void setupObservadores() {
        // Carga datos del usuario
        viewModel.cargaDatosUsuario().observe(this, new Observer<Resource<Usuario>>() {
            @Override
            public void onChanged(Resource<Usuario> usuarioResource) {
                if(usuarioResource != null) {
                    switch(usuarioResource.status) {
                        case LOADING:
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO,
                                    "", getString(R.string.cargando_datos));
                            break;
                        case SUCCESS:
                            if(usuarioResource.data != null) {
                                viewModel.ocultaDialogoCarga();
                                textUsername.setText(usuarioResource.data.getNombre());
                                // Carga imagen de perfil (circular)
                                //Picasso.with(HomeActivity.this).load(R.drawable.img_sample_user).transform(new CircleTransform()).into(btnPerfil);
                            } else {
                                viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR,
                                        getString(R.string.error_inesperado), usuarioResource.message);
                            }
                            break;
                        case ERROR:
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR,
                                    getString(R.string.error), usuarioResource.message);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialogoCarga != null) {
            dialogoCarga.dismiss();
            dialogoCarga = null;
        }
    }
}
