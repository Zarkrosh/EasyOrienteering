package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
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
    public Carrera newCarrera(Carrera carrera) {
        if(carrera != null) {
            return guardaCarrera(carrera);
        } else {
            throw new IllegalArgumentException("La carrera a guardar no puede ser null");
        }
    }

    @Override
    public void editCarrera(Carrera carrera) {
        if(carrera != null && carrera.getId() != null) {
            Carrera prev = getCarrera(carrera.getId());
            if(prev != null) {
                // Comprueba datos válidos
                // TODO
                
                // Borra la carrera anterior y sus datos asociados
                repoCarrera.deleteById(carrera.getId());
                // TODO: analizar optimización de destrucción de los recorridos
                // Guarda la carrera actualizada
                guardaCarrera(carrera);
            } else {
                // No existe ninguna carrera con ese ID
            }
        } else {
            throw new IllegalArgumentException("La carrera a guardar no puede ser null");
        }
    }
    
    @Override
    public void deleteCarrera(Carrera carrera) {
        repoCarrera.delete(carrera);
    }
    
    @Override
    public boolean deleteCarrera(long id) {
        if(existeCarrera(id)) {
            repoCarrera.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean existeCarrera(long id) {
        return repoCarrera.existsById(id);
    }
    
    private Carrera guardaCarrera(Carrera carrera) {
        // Primero guarda la carrera general
        Carrera res = repoCarrera.save(carrera);
        // Luego guarda los controles que no son guardados debido al problema
        // asociado al uso de la clave foránea del ID de la carrera.
        for(Control c : carrera.getControles()) {
            repoCarrera.insertaControl(c.getCodigo(), res.getId(), c.getTipo().name());
        }
        res.setControles(carrera.getControles());
        
        return res;
    }
    
}
