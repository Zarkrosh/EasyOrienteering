package com.hergomsoft.easyorienteering.ui.scan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.util.Utils;

import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;

public class ScanActivity extends AppCompatActivity {

    // Permisos que utiliza la actividad
    private String[] permisosNecesarios = new String[]{ CAMERA };

    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;

    SurfaceView cameraView;
    TextView mensajeScan;

    Toast toastPermisos;
    MutableLiveData<Integer> contadorEscaneos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        cameraView = findViewById(R.id.cameraView);
        mensajeScan = findViewById(R.id.mensajeScan);

        cameraView.setVisibility(View.GONE); // Evita error por petición de permisos

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(40.0f)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(400, 400).build();

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
            setupScanner();
        } else {
            // TODO Muestra mensaje de error
        }

    }

    private void setupScanner() {
        // Callback de la vista de la cámara
        cameraView.setVisibility(View.VISIBLE);
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressWarnings("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("[!] Camera source", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    String escaneado = barcodes.valueAt(0).displayValue;
                    contadorEscaneos.postValue(contadorEscaneos.getValue() + 1);
                    Log.d("QR-SCAN", escaneado);

                    // Comprueba si está corriendo una carrera
                    // TODO
                    boolean activa = false;
                    if(activa) {
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
                                    Toast.makeText(ScanActivity.this, "Este no es el control que buscas", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ScanActivity.this, "Has escaneado un triángulo, pero ya estás corriendo un recorrido", Toast.LENGTH_SHORT).show();
                            } else {
                                // Ha escaneado un QR ajeno a la aplicación
                                // TODO: "QR desconocido"
                                Toast.makeText(ScanActivity.this, "QR desconocido", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ScanActivity.this, "QR desconocido", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
    }

    /**
     * Muestra un diálogo de confirmación de inicio de recorrido. Si lo acepta, envía la petición
     * al servidor. Si no, vuelve a la pantalla principal.
     * @param idRecorrido ID del recorrido a iniciar
     */
    private void showConfirmacionInicioRecorrido(int idRecorrido) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permiso_necesario);
        alertBuilder.setMessage(R.string.permiso_camara);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Nueva carrera, envía petición de inicio
                // TODO
            }
        });
        alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No desea empezar nueva carrera, vuelve a la pantalla principal
                // TODO
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /**
     * Comprueba si los permisos necesarios se han habilitado. Devuelve true si ya están todos habilitados,
     * y false si alguno no lo está, en cuyo caso los pide (pudiendo mostrar diálogo).
     * @return True si están todos los permisos necesarios habilitados
     */
    private boolean compruebaPermisos() {
        // Comprueba qué permisos faltan por permitir
        ArrayList<String> noPermitidos = new ArrayList<>();
        for(String permiso : permisosNecesarios)
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED)
                noPermitidos.add(permiso);

        // Pide los permisos que faltan
        if(!noPermitidos.isEmpty()) {
            boolean shouldShowAlert = false;

            for (String permission : noPermitidos) {
                shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            }

            if (shouldShowAlert) {
                showPermissionAlert(noPermitidos.toArray(new String[0]));
            } else {
                requestPermissions(noPermitidos.toArray(new String[0]));
            }

            return false;
        }

        return true;
    }

    /**
     * Muestra un diálogo de petición de permisos.
     * @param permissions Permisos
     */
    private void showPermissionAlert(final String[] permissions) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permiso_necesario);
        alertBuilder.setMessage(R.string.permiso_camara);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(permissions);
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /**
     * Realiza la petición de los permisos especificados.
     * @param permissions Permisos
     */
    private void requestPermissions(String[] permissions) {
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

            setupScanner();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}