package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.repositories.RecorridoRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecorridoService implements IRecorridoService {
    
    @Autowired
    private RecorridoRepository recorridoRepository;
    
    
    @Override
    public Recorrido getRecorrido(long id) {
        try {
            Optional<Recorrido> res = recorridoRepository.findById(id);
            return (res.isPresent()) ? res.get() : null;
        } catch(Exception e) {
            System.out.println("[!] Error al obtener el recorrido con id " + id);
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener el recorrido");
        }
    }
    
    @Override
    public Recorrido guardaRecorrido(Recorrido recorrido) {
        if(recorrido != null) {
            return recorridoRepository.save(recorrido);
        } else {
            throw new IllegalArgumentException("La participación a guardar no puede ser null");
        }
    }

}
