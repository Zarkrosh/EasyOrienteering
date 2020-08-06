package com.hergomsoft.easyorienteering.util;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hergomsoft.easyorienteering.components.DialogoCarga;

public abstract class AndroidViewModelConCarga extends AndroidViewModel {

    private MutableLiveData<Integer> estadoDialogo;
    private MutableLiveData<String> tituloDialogo;
    private MutableLiveData<String> mensajeDialogo;

    public AndroidViewModelConCarga(Application app) {
        super(app);
        estadoDialogo = new MutableLiveData<>(DialogoCarga.ESTADO_OCULTO);
        tituloDialogo = new MutableLiveData<>("");
        mensajeDialogo = new MutableLiveData<>("");
    }

    public LiveData<String> getTituloDialogo() { return tituloDialogo; }
    public LiveData<String> getMensajeDialogo() { return mensajeDialogo; }
    public LiveData<Integer> getEstadoDialogo() { return estadoDialogo; }

    /**
     * Oculta el diálogo de carga.
     */
    public void ocultaDialogoCarga() {
        if(estadoDialogo.getValue() == DialogoCarga.ESTADO_CARGANDO) {
            estadoDialogo.postValue(DialogoCarga.ESTADO_OCULTO);
        }

        //
    }

    public void ocultaResultadoDialogo() {
        estadoDialogo.postValue(DialogoCarga.ESTADO_OCULTO);
    }

    /**
     * Asigna un estado al diálogo de carga de la vista con el título y mensaje especificados.
     * @param estado Estado
     * @param titulo Titulo
     * @param mensaje Mensaje
     */
    public void actualizaDialogoCarga(int estado, String titulo, String mensaje) {
        estadoDialogo.postValue(estado);
        tituloDialogo.postValue(titulo);
        mensajeDialogo.postValue(mensaje);
    }
}
