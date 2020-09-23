package com.hergomsoft.easyorienteering.ui.miscarreras;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.repositories.CarreraRepository;
import com.hergomsoft.easyorienteering.util.Constants;
import com.hergomsoft.easyorienteering.util.Resource;

import java.util.List;

public class MisCarrerasViewModel extends AndroidViewModel {

    private CarreraRepository carreraRepository;

    public MisCarrerasViewModel(Application app) {
        super(app);
        carreraRepository = CarreraRepository.getInstance(app);
    }

    public LiveData<Resource<List<Carrera>>> getCarrerasParticipadasUsuario() {
        // Obtener ID usuario conectado
        return carreraRepository.getCarrerasParticipadasUsuario();
    }

    public LiveData<Resource<List<Carrera>>> getCarrerasOrganizadasUsuario() {
        // Obtener ID usuario conectado
        return carreraRepository.getCarrerasOrganizadasUsuario();
    }

}
