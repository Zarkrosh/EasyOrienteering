package com.hergomsoft.easyorienteering.ui.explorar;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.hergomsoft.easyorienteering.data.model.Carrera;
import com.hergomsoft.easyorienteering.data.repositories.CarreraRepository;
import com.hergomsoft.easyorienteering.util.AndroidViewModelConCarga;
import com.hergomsoft.easyorienteering.util.Resource;

import java.util.List;

public class ExplorarViewModel extends AndroidViewModelConCarga {

    private CarreraRepository carreraRepository;
    private MediatorLiveData<Resource<List<Carrera>>> carreras;
    private MediatorLiveData<Resource<List<Carrera>>> circuitos;

    // Petición
    private boolean realizandoPeticion;
    private boolean noMasResultados;
    private int numeroPagina;
    private int numCarrerasPrev;
    private String nombre;
    private String tipo;
    private String modalidad;

    public ExplorarViewModel(Application app) {
        super(app);
        carreraRepository = CarreraRepository.getInstance(app);
        carreras = new MediatorLiveData<>();
        circuitos = new MediatorLiveData<>();
        nombre = "";
        tipo = "";
        modalidad = "";
    }

    public int getNumeroPagina() { return numeroPagina; }
    public LiveData<Resource<List<Carrera>>> getCarreras(){
        return carreras;
    }

    public void buscaCarreras(String busqueda, int numeroPagina) {
        if(!realizandoPeticion) {
            this.nombre = busqueda;
            this.numeroPagina = numeroPagina;
            noMasResultados = false;
            realizaBusqueda();
        }
    }

    public void buscaSiguientePagina() {
        if(!noMasResultados && !realizandoPeticion && carreras.getValue() != null
                && carreras.getValue().data != null && carreras.getValue().data.size() > 0) {
            numeroPagina++;
            realizaBusqueda();
        }
    }

    public void cargaMarcadoresCircuitos() {
        final LiveData<Resource<List<Carrera>>> repositorySource = carreraRepository.buscaCarreras("", Carrera.Tipo.CIRCUITO.name(), "", Integer.MAX_VALUE);
        circuitos.addSource(repositorySource, new Observer<Resource<List<Carrera>>>() {
            @Override
            public void onChanged(Resource<List<Carrera>> listResource) {
                if(listResource != null) {
                    switch (listResource.status) {
                        case LOADING:
                            // TODO Mensaje
                            break;
                        case SUCCESS:
                            // TODO Cargar overlays
                            break;
                        case ERROR:
                            // TODO ?
                            break;
                    }

                    circuitos.setValue(listResource);
                } else {
                    circuitos.removeSource(repositorySource);
                }
            }
        });
    }

    private void realizaBusqueda() {
        if(carreras.getValue() != null && carreras.getValue().data != null)
            numCarrerasPrev = carreras.getValue().data.size();
        else
            numCarrerasPrev = 0;
        realizandoPeticion = true;
        final LiveData<Resource<List<Carrera>>> repositorySource = carreraRepository.buscaCarreras(nombre, tipo, modalidad, numeroPagina);
        carreras.addSource(repositorySource, new Observer<Resource<List<Carrera>>>() {
            @Override
            public void onChanged(Resource<List<Carrera>> listResource) {
                if(listResource != null) {
                    switch (listResource.status) {
                        case SUCCESS:
                            if(listResource.data != null && listResource.data.size() == numCarrerasPrev) {
                                noMasResultados = true;
                            }
                            // No se hace break
                        case ERROR:
                            realizandoPeticion = false;
                            carreras.removeSource(repositorySource);
                            break;
                    }

                    carreras.setValue(listResource);
                } else {
                    carreras.removeSource(repositorySource);
                }
            }
        });
    }
}
