package com.hergomsoft.easyorienteering.ui.miscarreras;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.data.api.responses.CarrerasUsuarioResponse;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.repositories.CarreraRepository;

public class MisCarrerasViewModel extends ViewModel {

    private CarreraRepository carreraRepository;

    private LiveData<Recurso<CarrerasUsuarioResponse>> carrerasResponse;
    private MutableLiveData<Recurso<Boolean>> estadocargaCarreras;

    public MisCarrerasViewModel() {
        super();
        carreraRepository = new CarreraRepository();
        carrerasResponse = carreraRepository.getCarrerasUsuarioResponse();
        estadocargaCarreras = new MutableLiveData<>();
    }

    public LiveData<Recurso<CarrerasUsuarioResponse>> getCarrerasResponse() { return carrerasResponse; }
    public LiveData<Recurso<Boolean>> getEstadoCargaCarreras() { return estadocargaCarreras; }

    public void cargaCarreras() {
        estadocargaCarreras.postValue(new Recurso<>(true));
        carreraRepository.getCarrerasUsuario();
    }

    public void cargaCarrerasFinalizada() {
        estadocargaCarreras.postValue(new Recurso<>(false));
    }

    public void mostrarErrorCarga(String error) {
        estadocargaCarreras.postValue(new Recurso<>(error));
    }

}
