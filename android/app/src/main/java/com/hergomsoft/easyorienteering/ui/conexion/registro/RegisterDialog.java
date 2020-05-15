package com.hergomsoft.easyorienteering.ui.conexion.registro;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hergomsoft.easyorienteering.R;

/**
 * Diálogo que muestra el estado y resultado de la petición de registro de cuenta.
 */
public class RegisterDialog extends AlertDialog {

    private TextView titulo;
    private ImageView imagen;
    private ProgressBar progress;
    private TextView mensaje;

    private RegisterDialog(Context context) {
        super(context);
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_registro, null);
        setView(content);

        titulo = content.findViewById(R.id.dialog_registro_titulo);
        progress = content.findViewById(R.id.dialog_registro_progress);
        imagen = content.findViewById(R.id.dialog_registro_imagen);
        mensaje = content.findViewById(R.id.dialog_registro_mensaje);

        // Color del spinner circular
        progress.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        muestraMensajeRegistrando();
        super.onCreate(savedInstanceState);
    }

    /**
     * Muestra el mensaje de estado "Registrando" con un indicador de carga.
     */
    public void muestraMensajeRegistrando() {
        titulo.setText(R.string.registro_registrando);
        progress.setVisibility(View.VISIBLE);
        imagen.setVisibility(View.INVISIBLE);
        setMensaje("");
        mensaje.setTextColor(Color.BLACK);
    }

    /**
     * Muestra un mensaje indicando que se ha registrado la cuenta con éxito.
     */
    public void muestraMensajeExito() {
        titulo.setText(R.string.registro_registrada);
        imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.success));
        imagen.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
        setMensaje("");
        // Colores de éxito
        titulo.setTextColor(getContext().getResources().getColor(R.color.exito));
        mensaje.setTextColor(getContext().getResources().getColor(R.color.exito));
    }

    /**
     * Muestra un mensaje indicando que se ha producido un error.
     * @param error Mensaje de error
     */
    public void muestraMensajeError(String error) {
        titulo.setText(R.string.registro_error);
        imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.error));
        imagen.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
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
    public void setMensaje(String sMensaje) {
        mensaje.setText(sMensaje);
        if(sMensaje.isEmpty()) {
            // Si no se usa, desaparece
            mensaje.setVisibility(View.GONE);
        } else {
            mensaje.setVisibility(View.VISIBLE);
        }
    }

}
