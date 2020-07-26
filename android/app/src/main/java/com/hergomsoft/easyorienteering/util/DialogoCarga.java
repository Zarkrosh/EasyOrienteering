package com.hergomsoft.easyorienteering.util;

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
 * Diálogo de uso general para estados de carga, éxito y error.
 */
public class DialogoCarga extends AlertDialog {

    private TextView titulo;
    private ImageView imagen;
    private ProgressBar progress;
    private TextView mensaje;
    private TextView btnDismiss;

    public DialogoCarga(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialogo_carga, null);
        setView(content);

        titulo = content.findViewById(R.id.dialogo_carga_titulo);
        progress = content.findViewById(R.id.dialogo_carga_progress);
        imagen = content.findViewById(R.id.dialogo_carga_imagen);
        mensaje = content.findViewById(R.id.dialogo_carga_mensaje);
        btnDismiss = content.findViewById(R.id.dialogo_carga_dismiss);

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
     * Muestra un mensaje indicando que se está realizando una operación de carga.
     * @param tituloCarga Titulo de error
     * @param mensajeCarga Mensaje de error
     */
    public void muestraMensajeCarga(String tituloCarga, String mensajeCarga) {
        show();
        setTitulo(tituloCarga);
        setMensaje(mensajeCarga);
        configuraElementosSinError();
        // Colores por defecto
        titulo.setTextColor(Color.BLACK);
        mensaje.setTextColor(Color.BLACK);
    }

    /**
     * Muestra un mensaje indicando que se ha producido un error.
     * @param tituloExito Titulo de error
     * @param mensajeExito Mensaje de error
     */
    public void muestraMensajeExito(String tituloExito, String mensajeExito) {
        show();
        setTitulo(tituloExito);
        imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.exito));
        configuraElementosSinError();
        setMensaje(mensajeExito);
        // Colores de éxito
        titulo.setTextColor(getContext().getResources().getColor(R.color.exito));
        mensaje.setTextColor(getContext().getResources().getColor(R.color.exito));
    }


    /**
     * Muestra un mensaje indicando que se ha producido un error.
     * @param tituloError Titulo de error
     * @param mensajeError Mensaje de error
     */
    public void muestraMensajeError(String tituloError, String mensajeError) {
        show();
        setTitulo(tituloError);
        imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.error));
        configuraElementosConError();
        setMensaje(mensajeError);
        // Colores de error
        titulo.setTextColor(getContext().getResources().getColor(R.color.error));
        mensaje.setTextColor(getContext().getResources().getColor(R.color.error));
    }

    private void configuraElementosSinError() {
        progress.setVisibility(View.VISIBLE);
        imagen.setVisibility(View.INVISIBLE);
        btnDismiss.setVisibility(View.INVISIBLE);
    }

    private void configuraElementosConError() {
        imagen.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
        btnDismiss.setVisibility(View.VISIBLE);
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

    /**
     * Asigna un valor al título del diálogo.
     * @param sTitulo Mensaje
     */
    private void setTitulo(String sTitulo) {
        if(sTitulo == null) sTitulo = "";
        titulo.setText(sTitulo);
    }

}
