package com.hergomsoft.easyorienteering.ui.scan;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import android.widget.ImageButton;
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
import androidx.lifecycle.ViewModelProvider;

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
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.model.Registro;
import com.hergomsoft.easyorienteering.util.CustomLoadDialog;
import com.hergomsoft.easyorienteering.util.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    private ViewSwitcher switcher;

    // Escáner QR
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;

    // Diálogos
    private AlertDialog dialogConfirmacion;
    private AlertDialog dialogRecorridoPendiente;
    private CustomLoadDialog dialogEscaneo;

    // Sonidos
    private SoundPool soundPool;
    private int beepSound;

    // Botones
    private ImageButton btnSwitch;

    // Texto
    private TextView mensajeScan;

    // Vista del mapa
    private SubsamplingScaleImageView vistaMapa;

    private ScanViewModel viewModel;
    private String ultimoEscaneado;
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

        viewModel = new ViewModelProvider(this,
                new ScanViewModelFactory(getApplication()))
                .get(ScanViewModel.class);

        // Enlaza vistas
        switcher = findViewById(R.id.scan_switcher);
        cameraView = findViewById(R.id.scan_cameraView);
        mensajeScan = findViewById(R.id.scan_mensaje);
        btnSwitch = findViewById(R.id.scan_btn_switch);
        vistaMapa = findViewById(R.id.scan_vista_mapa);

        cameraView.setVisibility(View.GONE); // Evita error por petición de permisos

        // TEST
        contadorEscaneos = new MutableLiveData<>(0);
        TextView testContador = findViewById(R.id.testContador);
        contadorEscaneos.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                testContador.setText("" + integer);
            }
        });
        vistaMapa.setImage(ImageSource.resource(R.drawable.sample_map));

        // Comprueba permisos
        if (compruebaPermisos()) {
            iniciar();
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

    /**
     * Inicializa todos los elementos de la actividad.
     */
    private void iniciar() {
        setupDialogoGeneral();      // Diálogo de uso general (cargas/errores/exitos)
        setupDialogoConfirmacion(); // Diálogo de confirmación de inicio de recorridos
        setupDialogoPendiente();    // Diálogo de recorrido pendiente
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
        // Diálogos de carga y error
        viewModel.getEstadoDialogoCarga().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch(integer) {
                    case ScanViewModel.ESTADO_CARGANDO:
                        dialogEscaneo.muestraMensajeCarga(viewModel.getTituloDialogo(),
                                                          viewModel.getMensajeDialogo());
                        break;
                    case ScanViewModel.ESTADO_ERROR:
                        dialogEscaneo.muestraMensajeError(viewModel.getTituloDialogo(),
                                                          viewModel.getMensajeDialogo());
                        break;
                    case ScanViewModel.ESTADO_EXITO:
                        dialogEscaneo.muestraMensajeExito(viewModel.getTituloDialogo(),
                                                          viewModel.getMensajeDialogo());
                        break;
                    case ScanViewModel.ESTADO_ELECCION_PENDIENTE:
                        dialogEscaneo.dismiss();
                        muestraDialogoRecorridoPendiente();
                        break;
                    case ScanViewModel.ESTADO_OCULTO:
                    default:
                        dialogEscaneo.dismiss();
                }
            }
        });

        // Respuesta de carrera pendiente
        viewModel.getCarreraPendienteResponse().observe(this, new Observer<Recurso<Boolean>>() {
            @Override
            public void onChanged(Recurso<Boolean> respuesta) {
                if(respuesta.hayError()) {
                    // Ha ocurrido un error
                    viewModel.setEstadoDialogoError(getString(R.string.error), respuesta.getError());
                } else {
                    Boolean res = respuesta.getRecurso();
                    if(res == null) {
                        // Error inesperado
                        viewModel.setEstadoDialogoError(getString(R.string.error), "Error inesperado en la respuesta de carrera pendiente");
                    } else if(res) {
                        // Tiene una carrera pendiente
                        viewModel.pideEleccionPendiente();
                    } else {
                        ocultaDialogoCarga();
                    }
                }
            }
        });

        // Respuesta de registro de control
        viewModel.getRegistroResponse().observe(this, new Observer<Recurso<Registro>>() {
            @Override
            public void onChanged(Recurso<Registro> registroControl) {
                if(registroControl.hayError()) {
                    // Error al registrar
                    dialogEscaneo.muestraMensajeError(getString(R.string.registro_error), registroControl.getError());
                } else {
                    // Registro con éxito
                    ocultaDialogoCarga();
                    Control control = registroControl.getRecurso().getControl();
                    Control.Tipo tipo = control.getTipo();
                    if(tipo.equals(Control.Tipo.SALIDA)) {
                        viewModel.pasaAModoCarrera();
                        animacionRegistroControl();
                    } else if(tipo.equals(Control.Tipo.CONTROL)) {
                        animacionRegistroControl();
                    } else if(tipo.equals(Control.Tipo.META)) {
                        // Finaliza la carrera y muestra los resultados
                        // TODO
                        animacionRegistroControl();
                        Toast.makeText(ScanActivity.this, "Carrera finalizada", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Error inesperado
                        viewModel.setEstadoDialogoError(getString(R.string.error_inesperado), "El tipo de control es incorrecto: " + tipo);
                    }
                }
            }
        });

        // Respuesta de abandono de recorrido
        viewModel.getAbandonoResponse().observe(this, new Observer<Recurso<AbandonoResponse>>() {
            @Override
            public void onChanged(Recurso<AbandonoResponse> response) {
                setupModoInicioRecorrido();
                if(response.hayError()) {
                    viewModel.setEstadoDialogoError(getString(R.string.error), response.getError());
                } else {
                    AbandonoResponse r = response.getRecurso();
                    if(r.isAbandonado()) {
                        viewModel.setEstadoDialogoExito("", getString(R.string.scan_carga_abandonado));
                        // Tras unos segundos, oculta el diálogo automáticamente
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ocultaDialogoCarga();
                            }
                        }, 2000);
                    } else {
                        viewModel.setEstadoDialogoError(getString(R.string.error), r.getError());
                    }
                }
            }
        });

        // Cambio de control siguiente
        viewModel.getSiguienteControl().observe(this, new Observer<Control>() {
            @Override
            public void onChanged(Control control) {
                if(control != null) {
                    mensajeScan.setText("Siguiente control: " + control.getCodigo());
                } else {
                    mensajeScan.setText("Escanea un control");
                }
            }
        });

        // Elementos dependientes del modo de la vista
        viewModel.getModoVista().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch(integer) {
                    case ScanViewModel.MODO_INICIO_RECORRIDO:
                        setupModoInicioRecorrido();
                        break;
                    case ScanViewModel.MODO_CARRERA:
                        setupModoCarrera();
                        break;
                    default:
                        viewModel.setEstadoDialogoError(getString(R.string.error), "Error inesperado del modo de la vista");
                        break;
                }
            }
        });

        // Cambios entre vistas
        viewModel.getAlternadoVistas().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean pantallaScan) {
                if(pantallaScan) {
                    // Muestra la vista de escaneo de QR
                    mostrarVistaEscaneo();
                } else {
                    // Muestra la vista del mapa
                    mostrarVistaMapa();
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
        mensajeScan.setText(R.string.scan_escanea_triangulo);
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
        btnSwitch.setImageResource(R.drawable.map_icon);
        switcher.showNext();
        iniciaCapturaCamara();
    }

    /**
     * Muestra la vista de visualizado de mapa.
     */
    private void mostrarVistaMapa() {
        btnSwitch.setImageResource(R.drawable.qr_icon);
        switcher.showPrevious();
        cameraSource.stop();
    }


    /**
     * Crea los manejadores de eventos para los botones.
     */
    private void setupBotones() {
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.alternarVistas();
            }
        });
    }

    /**
     * Inicializa el sistema de sonidos y varga los sonidos utilizados.
     */
    private void setupSonidos() {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        beepSound = soundPool.load(this, R.raw.beep, 1); // Pitido de registro
        // TODO Añadir sonidos
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
                cameraSource.stop();
            }
        });
    }

    /**
     * Inicializa los elementos relacionados con el escaneo de QR.
     */
    private void setupDetectorQR() {
        ultimoEscaneado = ""; // Para evitar escaneos duplicados
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
                    if(!escaneado.contentEquals(ultimoEscaneado)) {
                        // Ignora escaneos repetidos
                        ultimoEscaneado = escaneado;

                        contadorEscaneos.postValue(contadorEscaneos.getValue() + 1); // TEST

                        // Obtiene los datos locales de la carrera y recorrido actuales
                        // TODO
                        Carrera carrera = viewModel.getCarreraActual();
                        if(carrera == null) {
                            // No está realizando ninguna carrera, solo puede escanear un triángulo
                            if (Utils.esEscaneoTriangulo(escaneado)) {
                                // Ha escaneado un triángulo
                                // Comprueba si ya ha corrido este recorrido (de forma local)
                                // TODO
                                boolean yaCorrido = false;
                                if (yaCorrido) {
                                    viewModel.setEstadoDialogoError(getString(R.string.error), getString(R.string.scan_error_ya_corrido));
                                } else {
                                    // Muestra confirmación de inicio de recorrido
                                    muestraConfirmacionInicioRecorrido(escaneado);
                                }
                            } else if (Utils.esEscaneoControl(escaneado)) {
                                // Ha escaneado un control: inválido
                                viewModel.setEstadoDialogoError(getString(R.string.error), getString(R.string.scan_error_es_control));
                            } else if (Utils.esEscaneoMeta(escaneado)) {
                                // Ha escaneado una meta: inválido
                                viewModel.setEstadoDialogoError(getString(R.string.error), getString(R.string.scan_error_es_meta));
                            } else {
                                // Ha escaneado un QR ajeno a la aplicación
                                viewModel.setEstadoDialogoError(getString(R.string.error), getString(R.string.scan_error_es_desconocido));
                            }
                        } else {
                            // Está corriendo una carrera, obtiene cuál es su siguiente control
                            // null si es score, valor del codigo si es en línea
                            if(carrera.getModalidad().equals(Carrera.Modalidad.SCORE)) {
                                // Carrera en score
                                if(Utils.esEscaneoControl(escaneado) || Utils.esEscaneoMeta(escaneado)) {
                                    // Comprueba que no ha registrado ya el control (de forma local)
                                    // TODO
                                    boolean yaRegistrado = false;
                                    if(yaRegistrado) {
                                        // En un score solo se puede registrar una vez un control
                                        viewModel.setEstadoDialogoError(getString(R.string.error), getString(R.string.scan_error_ya_registrado));
                                    } else {
                                        // Registro válido
                                        viewModel.registraControl(escaneado);
                                    }
                                } else if(Utils.esEscaneoTriangulo(escaneado)){
                                    // Ha escaneado un triángulo mientras está en carrera
                                    viewModel.setEstadoDialogoError(getString(R.string.error), getString(R.string.scan_error_corriendo));
                                }
                            } else {
                                // Carrera en línea
                                Control siguienteControl = viewModel.getSiguienteControl().getValue();
                                String codigo = Utils.getCodigoControlEscaneado(escaneado);
                                if(siguienteControl == null || codigo.contentEquals(siguienteControl.getCodigo())) {
                                    // Si hay error al obtener el siguiente control se envía la petición de todas formas
                                    if(siguienteControl == null) Toast.makeText(ScanActivity.this, "Error al obtener el siguiente control", Toast.LENGTH_SHORT).show();
                                    // El código coincide, registro válido
                                    viewModel.registraControl(escaneado);
                                } else {
                                    // Ha escaneado otro control
                                    viewModel.setEstadoDialogoError(getString(R.string.error), "Debes escanear el control " + siguienteControl.getCodigo() + ". Este es el " + codigo);
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
    private void setupDialogoGeneral() {
        dialogEscaneo = new CustomLoadDialog(ScanActivity.this);
        dialogEscaneo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // Pausa la captura de la cámara
                cameraSource.stop();
            }
        });
        dialogEscaneo.setOnDismissListener(new DialogInterface.OnDismissListener() {
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
                if(!dialogEscaneo.isShowing()) iniciaCapturaCamara();
                ultimoEscaneado = "";
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
        ocultaDialogoCarga();
        if(viewModel.setDatosEscaneadoInicio(escaneado)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!dialogConfirmacion.isShowing()) {
                        // Pausa la captura de la cámara
                        cameraSource.stop();
                        // Muestra el diálogo
                        dialogConfirmacion.show();
                    }
                }
            });
        } else {
            // Los datos no son correctos por algún motivo
            viewModel.setEstadoDialogoError(getString(R.string.error_inesperado), getString(R.string.scan_error_inesperado));
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
                setupModoCarrera();
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
                cameraSource.stop();
            }
        });
    }

    /**
     * Muestra un diálogo que notifica al usuario de que tiene un recorrido pendiente por acabar.
     * Puede elegir entre reanudar el recorrido o abandonarlo.
     */
    private void muestraDialogoRecorridoPendiente() {
        ocultaDialogoCarga();
        // Obtiene los datos de las preferencias
        String nombreCarrera = viewModel.getCarreraActual().getNombre();
        String nombreRecorrido = viewModel.getRecorridoActual().getNombre();
        if(nombreRecorrido == null || nombreCarrera == null) {
            // ERROR
            viewModel.setEstadoDialogoError(getString(R.string.error_inesperado), "El nombre de la carrera o el recorrido es nulo");
        } else {
            dialogRecorridoPendiente.setMessage(Html.fromHtml(getString(R.string.scan_pendiente_mensaje, nombreRecorrido, nombreCarrera)));
            dialogRecorridoPendiente.show();
        }
    }

    /**
     * Oculta el diálogo de carga utilizado para múltiples estados de carga o espera.
     */
    private void ocultaDialogoCarga() {
        viewModel.setEstadoDialogo(ScanViewModel.ESTADO_OCULTO);
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

            iniciar();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}