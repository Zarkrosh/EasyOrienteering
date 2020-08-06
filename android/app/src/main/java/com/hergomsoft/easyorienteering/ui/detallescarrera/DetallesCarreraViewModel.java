package com.hergomsoft.easyorienteering.ui.detallescarrera;

import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.repositories.CarreraRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.components.DialogoCarga;

public class DetallesCarreraViewModel extends AndroidViewModelConCarga {
    private CarreraRepository carreraRepository;

    private LiveData<Recurso<Carrera>> carreraResponse;

    private Carrera carrera;

    public DetallesCarreraViewModel(Application app) {
        super(app);
        carreraRepository = CarreraRepository.getInstance(app);
        carreraResponse = carreraRepository.getCarreraResponse();
    }

    public LiveData<Recurso<Carrera>> getCarreraResponse() { return carreraResponse; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public void cargaDatosCarrera(Intent i, String tituloDialogo, String mensajeDialogo) {
        actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, tituloDialogo, mensajeDialogo);
        if(i.hasExtra(Constants.EXTRA_ID_CARRERA)){
            long idCarrera = i.getLongExtra(Constants.EXTRA_ID_CARRERA, -1);
            if(idCarrera == -1) {
                actualizaDialogoCarga(DialogoCarga.ESTADO_ERROR, "Error", "ID de carrera inválido");
            }

            carreraRepository.getCarrera(idCarrera);
        }
    }

}
