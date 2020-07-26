package com.hergomsoft.easyorienteering.ui.detallescarrera;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.repositories.CarreraRepository;
import com.hergomsoft.easyorienteering.util.Constants;

public class DetallesCarreraViewModel extends ViewModel {
    private CarreraRepository carreraRepository;

    private LiveData<Recurso<Carrera>> carreraResponse;

    // Diálogo
    public static final int ESTADO_OCULTO = 0;
    public static final int ESTADO_ERROR = 1;
    public static final int ESTADO_CARGANDO = 2;
    private MutableLiveData<Integer> estadoDialogo;
    private String tituloDialogo;
    private String mensajeDialogo;

    private Carrera carrera;

    public DetallesCarreraViewModel() {
        super();
        carreraRepository = new CarreraRepository();
        carreraResponse = carreraRepository.getCarreraResponse();
        estadoDialogo = new MutableLiveData<>();
    }

    public LiveData<Recurso<Carrera>> getCarreraResponse() { return carreraResponse; }


    public MutableLiveData<Integer> getEstadoDialogo() { return estadoDialogo; }
    public String getTituloDialogo() { return tituloDialogo; }
    public String getMensajeDialogo() { return mensajeDialogo; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public void cargaDatosCarrera(Intent i, String tituloDialogo, String mensajeDialogo) {
        muestraCarga(tituloDialogo, mensajeDialogo);
        if(i.hasExtra(Constants.EXTRA_ID_CARRERA)){
            long idCarrera = i.getLongExtra(Constants.EXTRA_ID_CARRERA, -1);
            if(idCarrera == -1) {
                muestraErrorCarga("Error", "ID de carrera inválido");
            }

            carreraRepository.getCarrera(idCarrera);
        }
    }

    public void muestraCarga(String tituloCarga, String mensajeCarga) {
        tituloDialogo = tituloCarga;
        mensajeDialogo = mensajeCarga;
        estadoDialogo.postValue(ESTADO_CARGANDO);
    }

    public void muestraErrorCarga(String tituloError, String mensajeError) {
        tituloDialogo = tituloError;
        mensajeDialogo = mensajeError;
        estadoDialogo.postValue(ESTADO_ERROR);
    }

    public void ocultaDialogo() {
        estadoDialogo.postValue(ESTADO_OCULTO);
    }
}
