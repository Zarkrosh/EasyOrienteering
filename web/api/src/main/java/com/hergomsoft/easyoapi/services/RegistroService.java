package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repository.ControlRepository;
import com.hergomsoft.easyoapi.repository.RecorridoRepository;
import com.hergomsoft.easyoapi.repository.RegistroRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistroService implements IRegistroService {

    @Autowired
    private RegistroRepository repoRegistro;

    @Autowired
    private RecorridoRepository repoRecorrido;

    @Autowired
    private ControlRepository repoControl;
    
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
    
    @Override
    public Recorrido getRecorridoPendiente(Usuario corredor) {
        Recorrido res = null;
        
        Optional<Long> idRec = repoRegistro.getRecorridoPendienteCorredor(corredor.getId());
        if(idRec.isPresent()) {
            Optional<Recorrido> rec = repoRecorrido.findById(idRec.get());
            if(rec.isPresent()) res = rec.get();
        }
        
        return res;
    }
    
    @Override
    public Control getSiguienteControlRecorrido(Usuario corredor, Recorrido recorrido) {
        Control res = null;
        
        Optional<Long> idControl = repoRegistro.getIDSiguienteControlRecorrido(corredor.getId(), recorrido.getId());
        if(idControl.isPresent()) {
            Optional<Control> opt = repoControl.findById(idControl.get());
            res = (opt.isPresent()) ? opt.get() : null;
            // ¿Lanzar excepción?
        }

        return res;
    }
    
    @Override
    public boolean haRegistradoControl(Usuario corredor, Control control, Recorrido recorrido) {
        List<Long> idControles = repoRegistro.getIDControlesRegistradosRecorrido(corredor.getId(),recorrido.getId());
        return idControles.contains(control.getId());
    }

}
