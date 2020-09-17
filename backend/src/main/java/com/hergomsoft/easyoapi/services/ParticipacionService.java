package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repositories.ParticipacionRepository;
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
    private ParticipacionRepository participacionRepository;

    @Autowired
    private RecorridoService recorridoService;

    
    @Override
    public List<Participacion> findAll() {
        return (List<Participacion>) participacionRepository.findAll();
    }

    @Override
    public List<Participacion> getParticipacionesRecorrido(Recorrido recorrido) {
        return participacionRepository.findByRecorrido(recorrido);
    }

    @Override
    public List<Participacion> getParticipacionesUsuario(Usuario corredor) {
        return participacionRepository.findByCorredorOrderByFechaInicioDesc(corredor);
    }

    @Override
    public Participacion getParticipacionUsuarioRecorrido(Usuario corredor, Recorrido recorrido) {
        Optional<Participacion> participacion = participacionRepository.getParticipacionUsuarioRecorrido(corredor.getId(), recorrido.getId());
        return (participacion.isPresent()) ? participacion.get() : null;
    }

    @Override
    public Recorrido getRecorridoPendiente(Usuario corredor) {
        Recorrido res = null;
        Optional<Long> idPendiente = participacionRepository.getRecorridoPendienteCorredor(corredor.getId());
        if(idPendiente.isPresent()) {
            res = recorridoService.getRecorrido(idPendiente.get());
        }
        
        return res;
    }

    @Override
    public void abandonaRecorridoUsuario(Usuario corredor, Recorrido recorrido) throws MessageException {
        Recorrido pendiente = getRecorridoPendiente(corredor);
        if(Objects.equals(pendiente.getId(), recorrido.getId())) {
            participacionRepository.abandonaRecorrido(corredor.getId(), recorrido.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No puedes abandonar un recorrido que no tienes pendiente de acabar.");
        }
    }
    
    @Override
    public Participacion guardaParticipacion(Participacion participacion) {
        if(participacion != null) {
            return participacionRepository.save(participacion);
        } else {
            throw new IllegalArgumentException("La participación a guardar no puede ser null");
        }
    }
    
    @Override
    public boolean haParticipadoEnRecorrido(Usuario usuario, Recorrido recorrido) {
        return participacionRepository.haParticipadoEnRecorrido(usuario.getId(), recorrido.getId());
    }
    
    @Override
    public boolean haParticipadoEnCarrera(Usuario usuario, Carrera carrera) {
        return participacionRepository.haParticipadoEnCarrera(usuario.getId(), carrera.getId());
    }

}
