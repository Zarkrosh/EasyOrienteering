package com.hergomsoft.easyorienteering.ui.scan_inicial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.chrisbanes.photoview.PhotoView;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.data.model.pagers.RegistroControl;
import com.hergomsoft.easyorienteering.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class ScanActivity extends AppCompatActivity {

    private ViewSwitcher switcher;

    // Escáner QR
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;

    // Diálogos
    private AlertDialog dialogErrorScan;
    private AlertDialog dialogConfirmacion;
    private ScanDialog dialogPeticion;

    // Sonidos
    private SoundPool soundPool;
    private int beepSound;

    // Botones
    private Button btnSwitch;

    // Texto
    private TextView mensajeScan;

    // Mapa
    private PhotoView vistaMapa;
    //private BigImageView vistaMapa;


    private ScanViewModel viewModel;
    private String ultimoEscaneado;
    private Toast toastPermisos;

    // TEST
    private MutableLiveData<Integer> contadorEscaneos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //BigImageViewer.initialize(FrescoImageLoader.with(ScanActivity.this));
        setContentView(R.layout.activity_scan);

        viewModel = new ViewModelProvider(this).get(ScanViewModel.class);

        // Enlaza vistas
        switcher = findViewById(R.id.scan_switcher);
        cameraView = findViewById(R.id.scan_cameraView);
        mensajeScan = findViewById(R.id.scan_mensaje);
        btnSwitch = findViewById(R.id.scan_btn_switch);
        vistaMapa = findViewById(R.id.scan_vista_mapa);

        cameraView.setVisibility(View.GONE); // Evita error por petición de permisos

        // TEST

        // or load with glide
        //BigImageViewer.initialize(GlideImageLoader.with(appContext));


        contadorEscaneos = new MutableLiveData<>(0);
        TextView testContador = findViewById(R.id.testContador);
        contadorEscaneos.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                testContador.setText("" + integer);
            }
        });


        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        Bitmap d = BitmapFactory.decodeResource(getResources(), R.drawable.sample_map);
        int nh = (int) ( d.getHeight() * (maxTextureSize[0] / (float) d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, maxTextureSize[0], nh, true);
        //vistaMapa.setImageBitmap(scaled);


        Resources resources = getResources();
        int resourceId = R.drawable.sample_map;
        Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build();

        //vistaMapa.showImage(Uri.parse("https://www.mvoc.routegadget.co.uk/kartat/69.jpg"));
        Picasso.with(this).load(R.drawable.sample_map).resize(maxTextureSize[0], nh).into(vistaMapa);

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
        setupDialogoErrorEscaneo(); // Diálogo de error
        setupDialogoConfirmacion(); // Diálogo de confirmación de inicio de recorrido
        setupSonidos();             // Carga los sonidos utilizados
        setupDetectorQR();          // Inicia el detector de QR
        setupCamara();              // Inicia los elementos de la cámara
        setupBotones();             // Eventos de botones

        // Diálogo de estado de la petición
        dialogPeticion = new ScanDialog(ScanActivity.this);
        // Respuesta de registro de inicio
        viewModel.getResultadoConfirmacion().observe(this, new Observer<RegistroControl>() {
            @Override
            public void onChanged(RegistroControl registroControl) {
                // Guarda el registro y marca la carrera como pendiente
                // TODO

                // Lanza la actividad de carrera
                //startActivity(new Intent(ScanActivity.this, CarreraActivity.class));
            }
        });
        // Error en la petición de registro
        viewModel.getResultadoConfirmacionError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                dialogPeticion.muestraMensajeError(error);
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

        mostrarVistaEscaneo();
    }

    private void mostrarVistaEscaneo() {
        switcher.showNext();
        btnSwitch.setText(getString(R.string.scan_btn_vista_mapa));
        iniciaCapturaCamara();
    }

    private void mostrarVistaMapa() {
        btnSwitch.setText(getString(R.string.scan_btn_vista_scan));
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
                // Se inicia la captura al mostrar la pantalla de escaneo
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

                        // Comprueba si está corriendo una carrera (de forma local)
                        // TODO
                        boolean corriendo = false;
                        if (corriendo) {
                            // Ya está corriendo una carrera -> redirige a la actividad de carrera
                            Toast.makeText(ScanActivity.this, "Tienes un recorrido pendiente de acabar (dar datos)", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(ScanActivity.this, CarreraActivity.class));
                        } else {
                            // Si no tiene ninguna carrera activa, comprueba que se trata de un triángulo
                            if (Utils.esEscaneoTriangulo(escaneado)) {
                                // Ha escaneado un triángulo: válido.
                                // Comprueba si ya ha corrido este recorrido (de forma local)
                                // TODO
                                boolean yaCorrido = false;
                                if (yaCorrido) {
                                    muestraDialogoErrorScan(getResources().getString(R.string.scan_error_ya_corrido));
                                } else {
                                    // Muestra confirmación de inicio de recorrido
                                    muestraConfirmacionInicioRecorrido(escaneado);
                                }
                            } else if (Utils.esEscaneoControl(escaneado)) {
                                // Ha escaneado un control
                                muestraDialogoErrorScan(getResources().getString(R.string.scan_error_es_control));
                            } else if (Utils.esEscaneoMeta(escaneado)) {
                                // Ha escaneado una meta
                                muestraDialogoErrorScan(getResources().getString(R.string.scan_error_es_meta));
                            } else {
                                // Ha escaneado un QR ajeno a la aplicación
                                muestraDialogoErrorScan(getResources().getString(R.string.scan_error_es_desconocido));
                            }
                        }

                        /*
                        if(corriendo) {
                            // Carrera activa, puede ser un control o una meta
                            if(Utils.esEscaneoControl(escaneado) || Utils.esEscaneoMeta(escaneado)) {
                                // ¿Carrera en línea o score?
                                // TODO
                                String tipo = "LINEA";
                                if(tipo.contentEquals("LINEA")) {
                                    // Carrera en línea, ¿es su siguiente control?
                                    // TODO
                                    String siguiente = "31";
                                    String codigoControl = escaneado.split("-")[0];
                                    if(!codigoControl.contentEquals(siguiente)) {
                                        // Registro inválido
                                        // TODO: "Este no es tu control"
                                        Toast.makeText(ScanInicialActivity.this, "Este no es el control que buscas", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else {
                                    // Carrera score, como puede ser un control o la meta, es válido
                                }

                                // Registra el control
                                // TODO
                            } else {
                                // ¿Ha escaneado un triángulo?
                                if(Utils.esEscaneoTriangulo(escaneado)) {
                                    // Muestra mensaje de error
                                    // TODO: "Has escaneado un triángulo, pero ya estás corriendo un recorrido"
                                    Toast.makeText(ScanInicialActivity.this, "Has escaneado un triángulo, pero ya estás corriendo un recorrido", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Ha escaneado un QR ajeno a la aplicación
                                    // TODO: "QR desconocido"
                                    Toast.makeText(ScanInicialActivity.this, "QR desconocido", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // Si no tiene ninguna carrera activa, comprueba que se trata de un triángulo
                            if(Utils.esEscaneoTriangulo(escaneado)) {
                                // Registro válido. Obtiene información de la carrera
                                // TODO
                            } else {
                                // Ha escaneado un QR ajeno a la aplicación
                                // TODO: "QR desconocido"
                                Toast.makeText(ScanInicialActivity.this, "QR desconocido", Toast.LENGTH_SHORT).show();
                            }
                        }
                        */
                    }
                }
            }
        });
    }

    /**
     * Configura el diálogo utilizado para mostrar mensajes de error al usuario.
     */
    private void setupDialogoErrorEscaneo() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(R.string.error);
        alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) { iniciaCapturaCamara(); }
        });
        dialogErrorScan = alertBuilder.create();
    }

    /**
     * Muestra un diálogo que indica un error al escanear el código QR.
     * @param mensaje Mensaje de error
     */
    private void muestraDialogoErrorScan(String mensaje) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!dialogErrorScan.isShowing()) {
                    // Pausa la captura de la cámara
                    cameraSource.stop();
                    // Muestra el diálogo con el mensaje
                    dialogErrorScan.setMessage(mensaje);
                    dialogErrorScan.show();
                }
            }
        });
    }

    private void setupDialogoConfirmacion() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(R.string.scan_conf_titulo);
        alertBuilder.setMessage(R.string.scan_conf_mensaje);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Muestra diálogo de carga
                // TEST TODO Poner detrás de la confirmación
                dialogPeticion.muestraMensajeRegistrando();

                // TEST
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Nueva carrera, envía petición de inicio
                        viewModel.confirmaInicioRecorrido();
                    }
                }, 2000);
            }
        });
        alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No desea empezar nueva carrera, vuelve a escanear
                dialog.dismiss();
            }
        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!dialogPeticion.isShowing()) iniciaCapturaCamara();
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
        if(viewModel.actualizaDatosEscaneado(escaneado)) {
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
            muestraDialogoErrorScan(getResources().getString(R.string.scan_error_inesperado));
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

            iniciar();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}