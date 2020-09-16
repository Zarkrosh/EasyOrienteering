package com.hergomsoft.easyorienteering.ui.scan;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.api.responses.AbandonoResponse;
import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Control;
import com.hergomsoft.easyorienteering.data.model.Recorrido;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Registro;
import com.hergomsoft.easyorienteering.components.DialogoCarga;
import com.hergomsoft.easyorienteering.ui.resultados.ResultadosActivity;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;
import com.hergomsoft.easyorienteering.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;

public class ScanActivity extends AppCompatActivity {

    private ViewSwitcher viewSwitcher;
    private Animation slide_in_left, slide_in_right, slide_out_left, slide_out_right;

    // Escáner QR
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;

    // Diálogos
    private AlertDialog dialogConfirmacion;
    private AlertDialog dialogRecorridoPendiente;
    private AlertDialog dialogAbandono;
    private DialogoCarga dialogoCarga;

    // Sonidos
    private SoundPool soundPool;
    private int beepSound;

    // Común
    private ImageButton btnSwitch;

    // Vista de escaneo
    private TextView mensajeScan1;
    private TextView mensajeScan2;

    // Vista del mapa
    private SubsamplingScaleImageView vistaMapa;
    private TextView mensajeMapa;
    private ProgressBar progressMapa;

    private ScanViewModel viewModel;
    private Toast toastPermisos;

    // TEST
    private MutableLiveData<Integer> contadorEscaneos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewModel = ViewModelProviders.of(this).get(ScanViewModel.class);

        // Enlaza vistas
        viewSwitcher = findViewById(R.id.scan_switcher);
        cameraView = findViewById(R.id.scan_cameraView);
        mensajeScan1 = findViewById(R.id.scan_mensaje_1);
        mensajeScan2 = findViewById(R.id.scan_mensaje_2);
        btnSwitch = findViewById(R.id.scan_btn_switch);
        vistaMapa = findViewById(R.id.scan_vista_mapa);
        progressMapa = findViewById(R.id.scan_progress_mapa);
        mensajeMapa = findViewById(R.id.scan_error_mapa);

        cameraView.setVisibility(View.GONE); // Evita error por petición de permisos
        // Color del spinner circular
        progressMapa.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        // Animaciones
        slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slide_out_right = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        // Slide rápido
        slide_in_left.setDuration(getResources().getInteger(R.integer.duracion_slide_fast));
        slide_in_right.setDuration(getResources().getInteger(R.integer.duracion_slide_fast));
        slide_out_left.setDuration(getResources().getInteger(R.integer.duracion_slide_fast));
        slide_out_right.setDuration(getResources().getInteger(R.integer.duracion_slide_fast));

        // TEST
        contadorEscaneos = new MutableLiveData<>(0);
        TextView testContador = findViewById(R.id.testContador);
        contadorEscaneos.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                testContador.setText("" + integer);
            }
        });

        // Comprueba permisos
        if (compruebaPermisos()) {
            init();
        }

    }

    @SuppressLint("MissingPermission")
    private void iniciaCapturaCamara() {
        try {
            cameraSource.start(cameraView.getHolder());
        } catch (IOException e) {
            Toast.makeText(ScanActivity.this, "Error al reanudar la captura de la cámara", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void detieneCapturaCamara() {
        cameraSource.stop();
    }

    /**
     * Inicializa todos los elementos de la actividad.
     */
    private void init() {
        setupDialogoCarga();        // Diálogo de uso general (cargas/errores/exitos)
        setupDialogoConfirmacion(); // Diálogo de confirmación de inicio de recorridos
        setupDialogoPendiente();    // Diálogo de recorrido pendiente
        setupDialogoAbandono();     // Diálogo de confirmación de abandono de carrera
        setupSonidos();             // Carga los sonidos utilizados
        setupDetectorQR();          // Inicia el detector de QR
        setupCamara();              // Inicia los elementos de la cámara
        setupBotones();             // Eventos de botones
        setupObservadores();        // Observadores del ViewModel

        viewModel.compruebaRecorridoPendiente();
    }

    /**
     * Configura los observadores del ViewModel para realizar los cambios en la vista.
     */
    private void setupObservadores() {
        // Respuesta de carrera pendiente
        viewModel.getCarreraPendienteResponse().observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> respuesta) {
                switch (respuesta.status) {
                    case SUCCESS:
                        Boolean res = respuesta.data;
                        if(res == null) {
                            // Error inesperado
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), "Error inesperado en la respuesta de carrera pendiente");
                        } else if(res) {
                            // Tiene una carrera pendiente
                            muestraDialogoRecorridoPendiente();
                        } else {
                            viewModel.ocultaDialogoCarga();
                        }
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), respuesta.message);
                        break;
                    default:
                        viewModel.ocultaDialogoCarga();
                }
            }
        });

        // Respuesta de registro de control
        viewModel.getRegistroResponse().observe(this, new Observer<Resource<Registro>>() {
            @Override
            public void onChanged(Resource<Registro> registroControl) {
                switch (registroControl.status) {
                    case SUCCESS:
                        viewModel.ocultaDialogoCarga();
                        String codigo = registroControl.data.getControl();
                        if(codigo.contentEquals(Constants.CODIGO_SALIDA)) {
                            viewModel.pasaAModoCarrera();
                            animacionRegistroControl();
                        } else if(codigo.contentEquals(Constants.CODIGO_META)) {
                            animacionRegistroControl();
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_EXITO, "", getString(R.string.scan_recorrido_acabado));
                            // Tras un corto tiempo redirige a los resultados del recorrido
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewModel.ocultaResultadoDialogo();
                                    viewModel.resetDatos();
                                    Intent i = new Intent(ScanActivity.this, ResultadosActivity.class);
                                    i.putExtra(Constants.EXTRA_ID_RECORRIDO, viewModel.getRecorridoActual().getId());
                                    startActivity(i);
                                }
                            }, 2000);
                        } else {
                            animacionRegistroControl();
                        }
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.registro_error), registroControl.message);
                        viewModel.clearUltimoEscaneado();
                        break;
                    default:
                        viewModel.ocultaDialogoCarga();
                }
            }
        });

        // Respuesta de imagen de mapa
        viewModel.getMapaResponse().observe(this, new Observer<Resource<File>>() {
            @Override
            public void onChanged(Resource<File> mapaResponse) {
                if(mapaResponse != null) {
                    switch (mapaResponse.status) {
                        case LOADING:
                            progressMapa.setVisibility(View.VISIBLE);
                            mensajeMapa.setVisibility(View.VISIBLE);
                            mensajeMapa.setText(R.string.scan_cargando_mapa);
                            break;
                        case SUCCESS:
                            if(mapaResponse.data != null) {
                                try {
                                    //byte[] bytes = mapaResponse.data.bytes();
                                    //Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    vistaMapa.setImage(ImageSource.uri(mapaResponse.data.getAbsolutePath()));
                                    mensajeMapa.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    mensajeMapa.setText("Error al obtener el mapa: " + e.getMessage());
                                    mensajeMapa.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mensajeMapa.setText("Error al obtener el mapa");
                                mensajeMapa.setVisibility(View.VISIBLE);
                            }
                            progressMapa.setVisibility(View.GONE);
                            break;
                        case ERROR:
                            mensajeMapa.setText("Error al obtener el mapa: " + mapaResponse.message);
                            progressMapa.setVisibility(View.GONE);
                            mensajeMapa.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });

        // Respuesta de abandono de recorrido
        viewModel.getAbandonoResponse().observe(this, new Observer<Resource<AbandonoResponse>>() {
            @Override
            public void onChanged(Resource<AbandonoResponse> response) {
                setupModoInicioRecorrido();
                switch (response.status) {
                    case SUCCESS:
                        viewModel.ocultaDialogoCarga();
                        AbandonoResponse r = response.data;
                        if(r.isAbandonado()) {
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_EXITO,"", getString(R.string.scan_carga_abandonado));
                            // Tras unos segundos, oculta el diálogo automáticamente
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewModel.ocultaDialogoCarga();
                                }
                            }, 2000);
                        } else {
                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), r.getError());
                        }
                        break;
                    case ERROR:
                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), response.message);
                        break;
                    default:
                        viewModel.ocultaDialogoCarga();
                }
            }
        });

        // Cambio de control siguiente
        viewModel.getSiguienteControl().observe(this, new Observer<Control>() {
            @Override
            public void onChanged(Control control) {
                if(control != null) {
                    mensajeScan1.setText("Siguiente control");
                    mensajeScan2.setVisibility(View.VISIBLE);
                    mensajeScan2.setText(control.getCodigo());
                } else {
                    mensajeScan1.setText("Escanea un control");
                    mensajeScan2.setVisibility(View.GONE);
                }
            }
        });

        // Elementos dependientes del modo de la vista
        viewModel.getModoEscaneo().observe(this, new Observer<ScanViewModel.ModoEscaneo>() {
            @Override
            public void onChanged(ScanViewModel.ModoEscaneo modo) {
                if(modo != null) {
                    switch(modo) {
                        case INICIO_RECORRIDO:
                            setupModoInicioRecorrido();
                            break;
                        case CARRERA:
                            setupModoCarrera();
                            break;
                    }
                }
            }
        });

        // Cambios entre vistas
        viewModel.getAlternadoVistas().observe(this, new Observer<ScanViewModel.ModoVista>() {
            @Override
            public void onChanged(ScanViewModel.ModoVista modo) {
                if(modo != null) {
                    switch(modo) {
                        case ESCANEO:
                            mostrarVistaEscaneo();
                            break;
                        case MAPA:
                            mostrarVistaMapa();
                            break;
                    }
                }
            }
        });
    }

    /**
     * Produce una animación visual y sonora que notifica al usuario del registro exitoso de un control.
     */
    private void animacionRegistroControl() {
        // Animación visual
        // TODO
        // Reproduce sonido
        soundPool.play(beepSound, 1, 1, 1, 0 ,1);
    }

    /**
     * Configura los elementos de la vista para el modo de escaneo de inicio de recorrido.
     * En este modo no hay vista del mapa ni botón que le permita alternar, entre otras cosas.
     */
    private void setupModoInicioRecorrido() {
        btnSwitch.setVisibility(View.GONE);
        mensajeScan1.setText(R.string.scan_escanea_triangulo);
        mensajeScan2.setVisibility(View.GONE);
    }

    /**
     * Configura los elementos de la vista para el modo de transcurso de carrera.
     */
    private void setupModoCarrera() {
        btnSwitch.setVisibility(View.VISIBLE);
    }

    /**
     * Muestra la vista de escaneo de control.
     */
    private void mostrarVistaEscaneo() {
        viewSwitcher.setInAnimation(slide_in_left);
        viewSwitcher.setOutAnimation(slide_out_right);
        viewSwitcher.setDisplayedChild(0);
        btnSwitch.setImageResource(R.drawable.img_trazado);
        iniciaCapturaCamara();
    }

    /**
     * Muestra la vista de visualizado de mapa.
     */
    private void mostrarVistaMapa() {
        viewSwitcher.setInAnimation(slide_in_right);
        viewSwitcher.setOutAnimation(slide_out_left);
        viewSwitcher.setDisplayedChild(1);
        btnSwitch.setImageResource(R.drawable.img_qr);
        detieneCapturaCamara();
    }


    /**
     * Crea los manejadores de eventos para los botones.
     */
    private void setupBotones() {
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.alternarModoVista();
            }
        });
    }

    /**
     * Inicializa el sistema de sonidos y varga los sonidos utilizados.
     */
    private void setupSonidos() {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        beepSound = soundPool.load(this, R.raw.beep, 1); // Pitido de registro
    }

    /**
     * Inicializa los elementos de la cámara.
     */
    private void setupCamara() {
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(40.0f)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(400, 400).build();

        // Callback de la vista de la cámara
        cameraView.setVisibility(View.VISIBLE);
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                iniciaCapturaCamara();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                detieneCapturaCamara();
            }
        });
    }

    /**
     * Inicializa los elementos relacionados con el escaneo de QR.
     */
    private void setupDetectorQR() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        // Procesamiento de las detecciones
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    String escaneado = barcodes.valueAt(0).displayValue;
                    if(!viewModel.checkUltimoEscaneado(escaneado)) {
                        contadorEscaneos.postValue(contadorEscaneos.getValue() + 1); // DEBUG TODO BORRAR

                        // Obtiene los datos locales de la carrera y recorrido actuales
                        Carrera carrera = viewModel.getCarreraActual();
                        if(carrera == null) {
                            // No está realizando ninguna carrera, solo puede escanear un triángulo para iniciar
                            if (Utils.esEscaneoSalida(escaneado)) {
                                // Ha escaneado un triángulo
                                muestraConfirmacionInicioRecorrido(escaneado);
                            } else if (Utils.esEscaneoControl(escaneado)) {
                                // Ha escaneado un control: inválido
                                viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), getString(R.string.scan_error_es_control));
                            } else {
                                // Ha escaneado un QR ajeno a la aplicación
                                viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), getString(R.string.scan_error_es_desconocido));
                            }
                        } else {
                            // Está corriendo una carrera, obtiene cuál es su siguiente control
                            if(carrera.getModalidad().equals(Carrera.Modalidad.SCORE)) {
                                // Carrera en score
                                if(Utils.esEscaneoControl(escaneado)) {
                                    // Comprueba que no ha registrado ya el control (de forma local)
                                    // TODO
                                    boolean yaRegistrado = false;
                                    if(yaRegistrado) {
                                        // En un score solo se puede registrar una vez un control
                                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), getString(R.string.scan_error_ya_registrado));
                                    } else {
                                        // Registro válido
                                        viewModel.registraControl(escaneado);
                                    }
                                } else if(Utils.esEscaneoSalida(escaneado)){
                                    // Ha escaneado un triángulo mientras está en carrera
                                    viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), getString(R.string.scan_error_corriendo));
                                }
                            } else {
                                // Carrera en línea
                                Control siguienteControl = viewModel.getSiguienteControl().getValue();
                                String codigo = Utils.getCodigoControlEscaneado(escaneado);
                                if(siguienteControl == null || codigo.contentEquals(siguienteControl.getCodigo())) {
                                    // Si hay error al obtener el siguiente control se envía la petición de todas formas y ya lo validará el servidor
                                    if(siguienteControl == null) Toast.makeText(ScanActivity.this, "Error al obtener el siguiente control", Toast.LENGTH_SHORT).show();
                                    // El código coincide, registro válido
                                    viewModel.registraControl(escaneado);
                                } else {
                                    // Ha escaneado otro control
                                    if(Utils.esEscaneoSalida(escaneado)) {
                                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), "Esto es una salida. Debes escanear el control " + siguienteControl.getCodigo());
                                    } else if(Utils.esEscaneoControl(escaneado)){
                                        if(codigo.contentEquals(Constants.CODIGO_META)) {
                                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, "", "¡Todavía no has acabado!");
                                        } else {
                                            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), "Debes escanear el control " + siguienteControl.getCodigo() + ". Este es el " + codigo);
                                        }
                                    } else {
                                        viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error), "Este código no pertenece a la aplicación");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Configura el diálogo de uso general para operaciones de carga y notificaciones de éxito/error.
     */
    private void setupDialogoCarga() {
        dialogoCarga = new DialogoCarga(ScanActivity.this);
        dialogoCarga.setObservadorEstado(this, viewModel.getEstadoDialogo());
        dialogoCarga.setObservadorTitulo(this, viewModel.getTituloDialogo());
        dialogoCarga.setObservadorMensaje(this, viewModel.getMensajeDialogo());
        dialogoCarga.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // Pausa la captura de la cámara
                detieneCapturaCamara();
            }
        });
        dialogoCarga.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) { iniciaCapturaCamara(); }
        });
    }

    /**
     * Configura el diálogo de confirmación de inicio de recorrido y sus eventos.
     */
    private void setupDialogoConfirmacion() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(R.string.scan_conf_titulo);
        alertBuilder.setMessage(R.string.scan_conf_mensaje);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Nueva carrera, envía petición de inicio
                viewModel.confirmaInicioRecorrido();
            }
        });
        alertBuilder.setNegativeButton(android.R.string.no, null);
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!dialogoCarga.isShowing()) iniciaCapturaCamara();
                viewModel.clearUltimoEscaneado();
            }
        });
        dialogConfirmacion = alertBuilder.create();
    }


    /**
     * Muestra un diálogo de confirmación de inicio de recorrido. Si lo acepta, envía la petición
     * al servidor. Si no, vuelve a la pantalla principal.
     * @param escaneado Datos del triángulo escaneado
     */
    private void muestraConfirmacionInicioRecorrido(String escaneado) {
        viewModel.ocultaDialogoCarga();
        if(viewModel.setDatosEscaneadoInicio(escaneado)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!dialogConfirmacion.isShowing()) {
                        // Pausa la captura de la cámara
                        detieneCapturaCamara();
                        // Muestra el diálogo
                        dialogConfirmacion.show();
                    }
                }
            });
        } else {
            // Los datos no son correctos por algún motivo
            viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error_inesperado), getString(R.string.scan_error_inesperado));
        }
    }

    /**
     * Configura el diálogo de confirmación de inicio de recorrido y sus eventos.
     */
    private void setupDialogoPendiente() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(R.string.scan_pendiente_titulo);
        // El mensaje se configura en la función de mostrado
        alertBuilder.setPositiveButton(R.string.scan_pendiente_reanudar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Reanuda la carrera
                viewModel.pasaAModoCarrera();
            }
        });
        alertBuilder.setNegativeButton(R.string.scan_pendiente_abandonar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Abandona la carrera
                viewModel.confirmaAbandonoRecorrido();
            }
        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                iniciaCapturaCamara();
            }
        });
        dialogRecorridoPendiente = alertBuilder.create();
        dialogRecorridoPendiente.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                detieneCapturaCamara();
            }
        });
    }

    /**
     * Muestra un diálogo que notifica al usuario de que tiene un recorrido pendiente por acabar.
     * Puede elegir entre reanudar el recorrido o abandonarlo.
     */
    private void muestraDialogoRecorridoPendiente() {
        viewModel.ocultaDialogoCarga();
        Carrera cActual = viewModel.getCarreraActual();
        Recorrido rActual = viewModel.getRecorridoActual();
        if(cActual != null && rActual != null) {
            String nombreCarrera = cActual.getNombre();
            String nombreRecorrido = rActual.getNombre();
            if(nombreRecorrido == null || nombreCarrera == null) {
                // ERROR
                viewModel.actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, getString(R.string.error_inesperado), "El nombre de la carrera o el recorrido es nulo");
            } else {
                dialogRecorridoPendiente.setMessage(Html.fromHtml(getString(R.string.scan_pendiente_mensaje, nombreRecorrido, nombreCarrera)));
                dialogRecorridoPendiente.show();
            }
        }
    }

    private void setupDialogoAbandono() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.scan_abandono_titulo);
        // El mensaje se configura en la función de mostrado
        alertBuilder.setPositiveButton(R.string.scan_abandono_seguir, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.setNegativeButton(R.string.scan_pendiente_abandonar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Abandona la carrera
                viewModel.confirmaAbandonoRecorrido();
            }
        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                iniciaCapturaCamara();
            }
        });
        dialogAbandono = alertBuilder.create();
        dialogAbandono.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                detieneCapturaCamara();
            }
        });
    }

    private void muestraDialogoAbandono() {
        Recorrido rActual = viewModel.getRecorridoActual();
        if(rActual != null) {
            dialogAbandono.show();
        }
    }

    /**
     * Comprueba si los permisos necesarios se han habilitado. Devuelve true si ya están todos habilitados,
     * y false si alguno no lo está, en cuyo caso los pide (pudiendo mostrar diálogo).
     * @return True si están todos los permisos necesarios habilitados
     */
    private boolean compruebaPermisos() {
        // Comprueba qué permisos faltan por permitir
        ArrayList<String> noPermitidos = new ArrayList<>();
        for(String permiso : viewModel.getPermisosNecesarios())
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED)
                noPermitidos.add(permiso);

        // Pide los permisos que faltan
        if(!noPermitidos.isEmpty()) {
            boolean shouldShowAlert = false;

            for (String permission : noPermitidos) {
                shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            }

            if (shouldShowAlert) {
                muestraDialogoPermisos(noPermitidos.toArray(new String[0]));
            } else {
                pidePermisos(noPermitidos.toArray(new String[0]));
            }

            return false;
        }

        return true;
    }

    /**
     * Muestra un diálogo de petición de permisos.
     * @param permissions Permisos
     */
    private void muestraDialogoPermisos(final String[] permissions) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permiso_necesario);
        alertBuilder.setMessage(R.string.permiso_camara);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                pidePermisos(permissions);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /**
     * Realiza la petición de los permisos especificados.
     * @param permissions Permisos
     */
    private void pidePermisos(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, 1001);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    if(toastPermisos == null || toastPermisos.getView() == null) {
                        toastPermisos = Toast.makeText(this, R.string.permiso_requerido, Toast.LENGTH_LONG);
                        toastPermisos.show();
                        compruebaPermisos();
                    }
                    return;
                }
            }

            init();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if(viewModel.getRecorridoActual() != null) muestraDialogoAbandono();
        else super.onBackPressed();
    }

}