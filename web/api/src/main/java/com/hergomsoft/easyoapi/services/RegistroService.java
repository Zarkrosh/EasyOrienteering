package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repository.RecorridoRepository;
import com.hergomsoft.easyoapi.repository.RegistroRepository;
import com.hergomsoft.easyoapi.utils.MessageException;
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
    public String getCodigoSiguienteControlRecorrido(Usuario corredor, Recorrido recorrido) {
        Control res = null;
        
        Optional<String> idControl = repoRegistro.getCodigoSiguienteControlRecorrido(corredor.getId(), recorrido.getId());
        return (idControl.isPresent()) ? idControl.get() : null;
    }
    
    @Override
    public boolean haRegistradoControl(Usuario corredor, Control control, Recorrido recorrido) {
        List<Long> idControles = repoRegistro.getIDControlesRegistradosRecorrido(corredor.getId(),recorrido.getId());
        return idControles.contains(control.getId());
    }
    
    @Override
    public boolean haCorridoRecorrido(Usuario corredor, Recorrido recorrido) {
        return repoRegistro.haCorridoRecorrido(corredor.getId(), recorrido.getId());
    }
    
    @Override
    public List<Registro> getRegistrosRecorrido(Recorrido recorrido) {
        return repoRegistro.getRegistrosRecorrido(recorrido.getId());
    }
    
    @Override
    public Registro[] getRegistrosUsuarioRecorrido(Usuario corredor, Recorrido recorrido) {
        return repoRegistro.getRegistrosUsuarioRecorrido(corredor.getId(), recorrido.getId());
    }
    
    @Override
    public void abandonaRecorrido(Usuario corredor, Recorrido recorrido) throws MessageException {
        Control meta = recorrido.getCarrera().getControles().get(recorrido.getTrazado().get(recorrido.getTrazado().size() - 1));
        if(meta == null) {
            throw new MessageException("Error al abandonar. No se ha encontrado la meta.");
        }
        
        repoRegistro.abandonaRecorrido(corredor.getId(), recorrido.getId(), meta.getId());
    }

}
