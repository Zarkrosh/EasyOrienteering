package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.repository.CarreraRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarreraService implements ICarreraService {

    @Autowired
    private CarreraRepository repoCarrera;

    @Override
    public List<Carrera> findAll() {
        return (List<Carrera>) repoCarrera.findAll();
    }
    
    @Override
    public Carrera getCarrera(long id) {
        try {
            Optional<Carrera> res = repoCarrera.findById(id);
            return (res.isPresent()) ? res.get() : null;
        } catch(Exception e) {
            System.out.println("[!] Error al obtener la carrera con id " + id);
            return null;
        }
    }

    @Override
    public void saveCarrera(Carrera carrera) {
        if(carrera != null) repoCarrera.save(carrera);
        else {
            throw new IllegalArgumentException("La carrera a guardar no puede ser null");
        }
    }

    @Override
    public void deleteCarrera(Carrera carrera) {
        repoCarrera.delete(carrera);
    }

    @Override
    public boolean existeCarrera(long id) {
        return repoCarrera.existsById(id);
    }
    
}
