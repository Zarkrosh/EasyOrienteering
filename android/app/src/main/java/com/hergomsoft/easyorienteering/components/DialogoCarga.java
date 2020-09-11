package com.hergomsoft.easyorienteering.components;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.hergomsoft.easyorienteering.R;

/**
 * Diálogo de uso general para estados de carga, éxito y error.
 */
public class DialogoCarga extends AlertDialog {
    public static final int ESTADO_OCULTO = 0;
    public static final int ESTADO_ERROR = 1;
    public static final int ESTADO_EXITO = 2;
    public static final int ESTADO_CARGANDO = 3;

    private TextView titulo;
    private ImageView imagen;
    private ProgressBar progress;
    private TextView mensaje;
    private TextView btnDismiss;

    public DialogoCarga(Context context) {
        super(context);
        init();
    }

    private void init() {
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
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    }

    public void setObservadorEstado(LifecycleOwner owner, LiveData<Integer> estado) {
        estado.observe(owner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case ESTADO_CARGANDO:
                        progress.setVisibility(View.VISIBLE);
                        imagen.setVisibility(View.INVISIBLE);
                        btnDismiss.setVisibility(View.INVISIBLE);
                        // Colores normales
                        titulo.setTextColor(Color.BLACK);
                        mensaje.setTextColor(Color.BLACK);
                        show();
                        break;
                    case ESTADO_EXITO:
                        progress.setVisibility(View.INVISIBLE);
                        imagen.setVisibility(View.VISIBLE);
                        btnDismiss.setVisibility(View.INVISIBLE);
                        imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.img_exito));
                        // Colores de éxito
                        titulo.setTextColor(getContext().getResources().getColor(R.color.exito));
                        mensaje.setTextColor(getContext().getResources().getColor(R.color.exito));
                        show();
                        break;
                    case ESTADO_ERROR:
                        progress.setVisibility(View.INVISIBLE);
                        imagen.setVisibility(View.VISIBLE);
                        btnDismiss.setVisibility(View.VISIBLE);
                        imagen.setImageDrawable(getContext().getResources().getDrawable(R.drawable.img_error));
                        // Colores de error
                        titulo.setTextColor(getContext().getResources().getColor(R.color.error));
                        mensaje.setTextColor(getContext().getResources().getColor(R.color.error));
                        show();
                        break;
                    case ESTADO_OCULTO:
                    default:
                        dismiss();
                        break;
                }
            }
        });
    }

    public void setObservadorTitulo(LifecycleOwner owner, LiveData<String> titulo) {
        titulo.observe(owner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setTitulo(s);
            }
        });
    }

    public void setObservadorMensaje(LifecycleOwner owner, LiveData<String> mensaje) {
        mensaje.observe(owner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setMensaje(s);
            }
        });
    }

    /**
     * Asigna un valor al mensaje del diálogo. Si es un mensaje vacío, se oculta por completo
     * el campo de texto.
     * @param sMensaje Mensaje
     */
    public void setMensaje(String sMensaje) {
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
    public void setTitulo(String sTitulo) {
        if(sTitulo == null) sTitulo = "";
        titulo.setText(sTitulo);
    }

}
