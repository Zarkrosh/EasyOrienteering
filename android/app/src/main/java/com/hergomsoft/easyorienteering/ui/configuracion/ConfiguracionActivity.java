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
    AlertDialog dialogoCambioPassword;
    AlertDialog dialogoConfCerrarSesion;
    AlertDialog dialogoConfBorrarCuenta;

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
        //Button btnCompartirApp = findViewById(R.id.conf_btn_compartir_app);
        Button btnBorrarCuenta = findViewById(R.id.conf_btn_borrar_cuenta);
        Button btnCerrarSesion = findViewById(R.id.conf_btn_cerrar_sesion);

        btnCambiarNombre.setOnClickListener(v -> dialogoCambioNombre.show());
        btnCambiarClub.setOnClickListener(v -> dialogoCambioClub.show());
        btnCambiarContrasena.setOnClickListener(v -> dialogoCambioPassword.show());
        btnResumen.setOnClickListener(v -> {
            Intent i = new Intent(ConfiguracionActivity.this, ResumenActivity.class);
            i.putExtra(Constants.EXTRA_VOLUNTARIO, true);
            startActivity(i);
        });
        btnBorrarCuenta.setOnClickListener(v -> dialogoConfBorrarCuenta.show());
        btnCerrarSesion.setOnClickListener(v -> dialogoConfCerrarSesion.show());

        setupDialogos();
        setupObservadores();
    }

    private void setupDialogos() {
        setupDialogoCarga();            // Diálogo de carga de peticiones
        setupDialogoCambioNombre();     // Diálogo de cambio de nombre de usuario
        setupDialogoCambioClub();       // Diálogo de cambio de club
        setupDialogoCambioPassword();   // Diálogo de cambio de contraseña
        setupDialogoConfCerrarSesion(); // Diálogo de confirmación de cerrado de sesión
        setupDialogoConfBorrarCuenta(); // Diálogo de confirmación de borrado de cuenta
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

    private void setupDialogoCambioPassword() {
        dialogoCambioPassword = new AlertDialog.Builder(this).create();
        final View view = getLayoutInflater().inflate(R.layout.dialogo_cambio_pass, null);
        final EditText inputPrev = view.findViewById(R.id.conf_input_cambio_pass_actual);
        final EditText inputNueva = view.findViewById(R.id.conf_input_cambio_pass_nueva);
        final EditText inputNuevaConf = view.findViewById(R.id.conf_input_cambio_pass_conf);
        final Button btnCancelar = view.findViewById(R.id.conf_btn_cancelar_cambio_pass);
        final Button btnGuardar = view.findViewById(R.id.conf_btn_guardar_cambio_pass);
        inputNuevaConf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String conf = inputNuevaConf.getText().toString();
                if(conf.contentEquals(inputNueva.getText().toString()) && conf.length() >= Constants.MIN_PASSWORD_LENGTH) {
                    btnGuardar.setEnabled(true);
                } else {
                    // FEATURE: Indicador visual
                    btnGuardar.setEnabled(false);
                }
            }
        });
        btnCancelar.setOnClickListener(v -> dialogoCambioPassword.dismiss());
        btnGuardar.setOnClickListener(v -> {
            viewModel.realizaCambioPassword(inputPrev.getText().toString(), inputNueva.getText().toString());
            dialogoCambioPassword.dismiss();
        });
        dialogoCambioPassword.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                inputPrev.setText("");
                inputNueva.setText("");
                inputNuevaConf.setText("");
            }
        });
        dialogoCambioPassword.setView(view);
    }

    private void setupDialogoConfCerrarSesion() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.conf_cerrar_sesion);
        alertBuilder.setMessage(R.string.conf_cerrar_sesion_mensaje);
        alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            // Cierra la sesión
            viewModel.cerrarSesion();
        });
        alertBuilder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        dialogoConfCerrarSesion = alertBuilder.create();
    }

    private void setupDialogoConfBorrarCuenta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.conf_borrar_cuenta);
        alertBuilder.setMessage(R.string.conf_borrar_cuenta_mensaje);
        alertBuilder.setPositiveButton(R.string.conf_borrar, (dialog, which) -> {
            // Borra la cuenta
            viewModel.confirmaBorradoCuenta();
        });
        alertBuilder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        dialogoConfBorrarCuenta = alertBuilder.create();
    }

    private void setupObservadores() {
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

        // Respuesta a la petición de cambio de contraseña
        viewModel.getCambioPasswordResponse().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> cambiado) {
                switch (cambiado.status) {
                    case SUCCESS:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_EXITO, "", getString(R.string.conf_resp_password_cambiada));
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
                // Redirige a la pantalla de conexión
                viewModel.ocultaDialogoCarga();
                Intent i = new Intent(ConfiguracionActivity.this, ConexionActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Borra histórico
                startActivity(i);
                finish();
            }
        });

        // Estado de borrado de cuenta
        viewModel.getEstadoBorradoCuenta().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> resource) {
                switch(resource.status) {
                    case SUCCESS:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_EXITO, "", getString(R.string.conf_resp_cuenta_borrada));
                        // Tras una pequeña espera...
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // ... redirige a la pantalla de conexión
                                viewModel.ocultaDialogoCarga();
                                Intent i = new Intent(ConfiguracionActivity.this, ConexionActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Borra histórico
                                startActivity(i);
                                finish();
                            }
                        }, 1500);
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), resource.message);
                        break;
                }
            }
        });
    }

}