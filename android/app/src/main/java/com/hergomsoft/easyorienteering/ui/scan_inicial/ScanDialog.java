package com.hergomsoft.easyorienteering.ui.scan_inicial;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hergomsoft.easyorienteering.R;

/**
 * Diálogo que muestra el estado y resultado de la petición de inicio de recorrido.
 */
public class ScanDialog extends AlertDialog {

    private TextView titulo;
    private ImageView imagen;
    private ProgressBar progress;
    private TextView mensaje;
    private TextView btnDismiss;

    protected ScanDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_scan, null);
        setView(content);

        titulo = content.findViewById(R.id.dialog_scan_inicial_titulo);
        progress = content.findViewById(R.id.dialog_scan_inicial_progress);
        imagen = content.findViewById(R.id.dialog_scan_inicial_imagen);
        mensaje = content.findViewById(R.id.dialog_scan_inicial_mensaje);
        btnDismiss = content.findViewById(R.id.dialog_scan_inicial_dismiss);

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setCancelable(false);

        // Fondo transparente para los bordes redondeados
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Color del spinner circular
        progress.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        super.onCreate(savedInstanceState);
    }

    /**
     * Muestra el mensaje de estado "Registrando" con un indicador de carga.
     */
    public void muestraMensajeRegistrando() {
        show();
        titulo.setText(R.string.registro_registrando);
        progress.setVisibility(View.VISIBLE);
        imagen.setVisibility(View.INVISIBLE);
        btnDismiss.setVisibility(View.INVISIBLE);
        setMensaje("");
        // Colores por defecto
        titulo.setTextColor(Color.BLACK);
        mensaje.setTextColor(Color.BLACK);
    }

    /**
     * Muestra el mensaje de estado "Cargando datos de la carrera" con un indicador de carga.
     */
    public void muestraMensajeCargandoDatosCarrera() {
        show();
        titulo.setText(R.string.registro_registrando);
        progress.setVisibility(View.VISIBLE);
        imagen.setVisibility(View.INVISIBLE);
        btnDismiss.setVisibility(View.INVISIBLE);
        setMensaje("");
        // Colores por defecto
        titulo.setTextColor(Color.BLACK);
        mensaje.setTextColor(Color.BLACK);
    }


    /**
     * Muestra un mensaje indicando que se ha producido un error.
     * @param error Mensaje de error
     */
    public void muestraMensajeError(String error) {
        show();
        titulo.setText(R.string.registro_error);
        imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.error));
        imagen.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
        btnDismiss.setVisibility(View.VISIBLE);
        setMensaje(error);
        // Colores de error
        titulo.setTextColor(getContext().getResources().getColor(R.color.error));
        mensaje.setTextColor(getContext().getResources().getColor(R.color.error));
    }

    /**
     * Asigna un valor al mensaje del diálogo. Si es un mensaje vacío, se oculta por completo
     * el campo de texto.
     * @param sMensaje Mensaje
     */
    private void setMensaje(String sMensaje) {
        if(sMensaje == null) sMensaje = "";
        mensaje.setText(sMensaje);
        if(sMensaje.isEmpty()) {
            // Si no se usa, desaparece
            mensaje.setVisibility(View.GONE);
        } else {
            mensaje.setVisibility(View.VISIBLE);
        }
    }

}
