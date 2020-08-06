package com.hergomsoft.easyorienteering.ui.miscarreras;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hergomsoft.easyorienteering.data.api.responses.CarrerasUsuarioResponse;
import com.hergomsoft.easyorienteering.data.model.Recurso;
import com.hergomsoft.easyorienteering.data.repositories.CarreraRepository;

public class MisCarrerasViewModel extends AndroidViewModel {

    private CarreraRepository carreraRepository;

    private LiveData<Recurso<CarrerasUsuarioResponse>> carrerasResponse;
    private MutableLiveData<Recurso<Boolean>> estadocargaCarreras;

    public MisCarrerasViewModel(Application app) {
        super(app);
        carreraRepository = CarreraRepository.getInstance(app);
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

    public void mostrarErrorCarga(String error) { estadocargaCarreras.postValue(new Recurso<>(error)); }

    public void actualizaBusqueda(String busqueda) {
        // TODO
    }

}
