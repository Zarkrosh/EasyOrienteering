package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.repository.RegistroRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistroService implements IRegistroService {

    @Autowired
    private RegistroRepository repoRegistro;
    
    @Override
    public List<Registro> findAll() {
        return (List<Registro>) repoRegistro.findAll();
    }

    @Override
    public Registro registraPasoControl(Registro registro) {
        if(registro != null) {
            return repoRegistro.save(registro);
        } else {
            throw new IllegalArgumentException("El registro a guardar no puede ser null");
        }
    }

}
