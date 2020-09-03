package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repository.ParticipacionRepository;
import com.hergomsoft.easyoapi.repository.RecorridoRepository;
import com.hergomsoft.easyoapi.utils.MessageException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ParticipacionService implements IParticipacionService {

    @Autowired
    private ParticipacionRepository repoParticipacion;

    @Autowired
    private RecorridoRepository repoRecorrido;

    
    @Override
    public List<Participacion> findAll() {
        return (List<Participacion>) repoParticipacion.findAll();
    }

    @Override
    public List<Participacion> getParticipacionesRecorrido(Recorrido recorrido) {
        return repoParticipacion.findByRecorrido(recorrido);
    }

    @Override
    public List<Participacion> getParticipacionesUsuario(Usuario corredor) {
        return repoParticipacion.findByCorredorOrderByFechaInicioDesc(corredor);
    }

    @Override
    public Participacion getParticipacionUsuarioRecorrido(Usuario corredor, Recorrido recorrido) {
        Optional<Participacion> participacion = repoParticipacion.getParticipacionUsuarioRecorrido(corredor.getId(), recorrido.getId());
        return (participacion.isPresent()) ? participacion.get() : null;
    }

    @Override
    public Recorrido getRecorridoPendiente(Usuario corredor) {
        Recorrido res = null;
        Optional<Long> idPendiente = repoParticipacion.getRecorridoPendienteCorredor(corredor.getId());
        if(idPendiente.isPresent()) {
            Optional<Recorrido> rec = repoRecorrido.findById(idPendiente.get());
            res = (rec.isPresent()) ? rec.get() : null;
        }
        
        return res;
    }

    @Override
    public void abandonaRecorridoUsuario(Usuario corredor, Recorrido recorrido) throws MessageException {
        Recorrido pendiente = getRecorridoPendiente(corredor);
        if(Objects.equals(pendiente.getId(), recorrido.getId())) {
            repoParticipacion.abandonaRecorrido(corredor.getId(), recorrido.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No puedes abandonar un recorrido que no tienes pendiente de acabar.");
        }
    }
    
    @Override
    public Participacion guardaParticipacion(Participacion participacion) {
        if(participacion != null) {
            return repoParticipacion.save(participacion);
        } else {
            throw new IllegalArgumentException("La participación a guardar no puede ser null");
        }
    }
    
    @Override
    public boolean haParticipadoEnRecorrido(Usuario usuario, Recorrido recorrido) {
        return repoParticipacion.haParticipadoEnRecorrido(usuario.getId(), recorrido.getId());
    }
    
    @Override
    public boolean haParticipadoEnCarrera(Usuario usuario, Carrera carrera) {
        return repoParticipacion.haParticipadoEnCarrera(usuario.getId(), carrera.getId());
    }

}
