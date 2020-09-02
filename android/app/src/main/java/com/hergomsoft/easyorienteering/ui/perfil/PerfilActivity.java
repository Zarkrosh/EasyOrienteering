package com.hergomsoft.easyorienteering.ui.perfil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.ui.configuracion.ConfiguracionActivity;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.CircleTransform;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends BackableActivity {

    PerfilViewModel viewModel;

    ImageButton btnFotoPerfil;
    TextView tvNombre;
    TextView tvClub;
    TextView tvMiembroDesde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Perfil de usuario");
        setContentView(R.layout.activity_perfil);

        viewModel = ViewModelProviders.of(this).get(PerfilViewModel.class);

        btnFotoPerfil = findViewById(R.id.perfil_foto_perfil);
        tvNombre = findViewById(R.id.perfil_nombre_usuario);
        tvClub = findViewById(R.id.perfil_club_usuario);
        tvMiembroDesde = findViewById(R.id.perfil_miembro_desde);

        setupListeners();
        setupObservers();
    }

    private void setupListeners() {
        btnFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "TODO Ver foto ampliada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupObservers() {
        // Carga de datos de usuario
        long idUsuario = getIntent().getLongExtra(Constants.EXTRA_ID_USUARIO, -1);
        if(idUsuario < 0) {
            Toast.makeText(this, "Error al cargar los detalles del usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
        viewModel.cargaDatosUsuario(idUsuario).observe(this, new Observer<Resource<Usuario>>() {
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
                                Usuario usuario = usuarioResource.data;
                                viewModel.ocultaDialogoCarga();
                                tvNombre.setText(usuario.getNombre());
                                if(usuario.getClub().isEmpty()) {
                                    tvClub.setVisibility(View.GONE);
                                } else {
                                    tvClub.setVisibility(View.VISIBLE);
                                    tvClub.setText(String.format("(%s)", usuario.getClub()));
                                }
                                tvMiembroDesde.setText(getString(R.string.perfil_miembro_desde, usuario.getFechaRegistro().toString()));

                                // Carga imagen circular
                                // TODO Cargar imagen de perfil
                                Picasso.with(PerfilActivity.this).load(R.drawable.img_sample_user).transform(new CircleTransform()).into(btnFotoPerfil);
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


}