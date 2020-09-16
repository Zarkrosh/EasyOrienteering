package com.hergomsoft.easyorienteering.ui.configuracion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.data.model.Usuario;
import com.hergomsoft.easyorienteering.ui.conexion.ConexionActivity;
import com.hergomsoft.easyorienteering.ui.resumen.ResumenActivity;
import com.hergomsoft.easyorienteering.util.BackableActivity;
import com.hergomsoft.easyorienteering.util.CircleTransform;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;
import com.squareup.picasso.Picasso;

public class ConfiguracionActivity extends BackableActivity {

    ConfiguracionViewModel viewModel;

    ImageButton btnFotoPerfil;
    TextView tvNombre;
    TextView tvClub;
    DialogoCarga dialogoCarga;
    AlertDialog dialogoCambioNombre;
    AlertDialog dialogoCambioClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.conf_configuracion));
        setContentView(R.layout.activity_configuracion);

        viewModel = ViewModelProviders.of(this).get(ConfiguracionViewModel.class);

        btnFotoPerfil = findViewById(R.id.conf_foto_perfil);
        tvNombre = findViewById(R.id.conf_nombre_usuario);
        tvClub = findViewById(R.id.conf_club_usuario);
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
        btnCambiarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCambioNombre.show();
            }
        });
        btnCambiarClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCambioClub.show();
            }
        });
        btnCambiarContrasena.setOnClickListener(todoListener);
        btnResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfiguracionActivity.this, ResumenActivity.class);
                i.putExtra(Constants.EXTRA_VOLUNTARIO, true);
                startActivity(i);
            }
        });
        btnCompartirApp.setOnClickListener(todoListener);
        btnBorrarCuenta.setOnClickListener(todoListener);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.cerrarSesion();
            }
        });

        setupDialogos();
        setupObservadores();
    }

    private void setupDialogos() {
        setupDialogoCarga();        // Diálogo de carga de peticiones
        setupDialogoCambioNombre(); // Diálogo de cambio de nombre de usuario
        setupDialogoCambioClub();   // Diálogo de cambio de club
    }

    private void setupDialogoCarga() {
        dialogoCarga = new DialogoCarga(this);
        dialogoCarga.setObservadorEstado(this, viewModel.getEstadoDialogo());
        dialogoCarga.setObservadorTitulo(this, viewModel.getTituloDialogo());
        dialogoCarga.setObservadorMensaje(this, viewModel.getMensajeDialogo());
    }

    private void setupDialogoCambioNombre() {
        dialogoCambioNombre = new AlertDialog.Builder(this).create();
        final View view = getLayoutInflater().inflate(R.layout.dialogo_cambio_nombre, null);
        final EditText input = view.findViewById(R.id.conf_input_cambio_nombre);
        final Button btnCancelar = view.findViewById(R.id.conf_btn_cancelar_cambio_nombre);
        final Button btnGuardar = view.findViewById(R.id.conf_btn_guardar_cambio_nombre);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.actualizaInputCambioNombre(input.getText().toString());
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCambioNombre.dismiss();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.realizaCambioNombreUsuario();
                dialogoCambioNombre.dismiss();
            }
        });
        viewModel.getEnvioCambioNombreHabilitado().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean habilitado) {
                if(habilitado != null) btnGuardar.setEnabled(habilitado);
            }
        });
        dialogoCambioNombre.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                input.setText("");
            }
        });
        dialogoCambioNombre.setView(view);
    }

    private void setupDialogoCambioClub() {
        dialogoCambioClub = new AlertDialog.Builder(this).create();
        final View view = getLayoutInflater().inflate(R.layout.dialogo_cambio_club, null);
        final EditText input = view.findViewById(R.id.conf_input_cambio_club);
        final Button btnCancelar = view.findViewById(R.id.conf_btn_cancelar_cambio_club);
        final Button btnGuardar = view.findViewById(R.id.conf_btn_guardar_cambio_club);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                viewModel.actualizaInputCambioClub(input.getText().toString());
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCambioClub.dismiss();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.realizaCambioClub();
                dialogoCambioClub.dismiss();
            }
        });
        dialogoCambioClub.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                input.setText("");
            }
        });
        dialogoCambioClub.setView(view);
    }

    public void setupObservadores() {
        // Carga de datos de usuario
        viewModel.cargaDatosUsuario().observe(this, new Observer<Resource<Usuario>>() {
            @Override
            public void onChanged(Resource<Usuario> usuarioResource) {
                if(usuarioResource != null) {
                    switch(usuarioResource.status) {
                        case SUCCESS:
                            if(usuarioResource.data != null) {
                                viewModel.ocultaResultadoDialogo();
                                tvNombre.setText(usuarioResource.data.getNombre());
                                if(usuarioResource.data.getClub().isEmpty()) {
                                    tvClub.setVisibility(View.GONE);
                                } else {
                                    tvClub.setVisibility(View.VISIBLE);
                                    tvClub.setText(String.format("(%s)", usuarioResource.data.getClub()));
                                }

                                // Carga imagen circular
                                // TODO Cargar imagen de perfil
                                Picasso.with(ConfiguracionActivity.this).load(R.drawable.img_sample_user).transform(new CircleTransform()).into(btnFotoPerfil);
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

        // Respuesta a la petición de cambio de nombre
        viewModel.getCambioNombreResponse().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> cambiado) {
                switch (cambiado.status) {
                    case LOADING:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_CARGANDO, "", getString(R.string.conf_cambiando_nombre));
                        break;
                    case SUCCESS:
                        // El TextView ya se actualiza automáticamente con el observador de los datos del usuario
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_EXITO, "", getString(R.string.conf_resp_nombre_cambiado));
                        // Tras una pequeña espera oculta el diálogo
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewModel.ocultaResultadoDialogo();
                            }
                        }, 1500);
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), cambiado.message);
                        break;
                    default:
                        viewModel.ocultaDialogoCarga();
                }
            }
        });

        // Respuesta a la petición de cambio de club
        viewModel.getCambioClubResponse().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> cambiado) {
                switch (cambiado.status) {
                    case SUCCESS:
                        // El TextView ya se actualiza automáticamente con el observador de los datos del usuario
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_EXITO, "", getString(R.string.conf_resp_club_cambiado));
                        // Tras una pequeña espera oculta el diálogo
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewModel.ocultaResultadoDialogo();
                            }
                        }, 1500);
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), cambiado.message);
                        break;
                    default:
                        viewModel.ocultaDialogoCarga();
                }
            }
        });

        // Estado de logout
        viewModel.getEstadoLogout().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> resource) {
                switch(resource.status) {
                    case SUCCESS:
                        // Redirige a la pantalla de conexión
                        viewModel.ocultaDialogoCarga();
                        Intent i = new Intent(ConfiguracionActivity.this, ConexionActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Borra histórico
                        startActivity(i);
                        finish();
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), resource.message);
                        break;
                }
            }
        });
    }

}