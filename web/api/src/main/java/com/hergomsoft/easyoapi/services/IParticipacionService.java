package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.utils.MessageException;
import java.util.List;

public interface IParticipacionService {

    List<Participacion> findAll();
    
    /**
     * Devuelve las participaciones de los corredores en un recorrido.
     * @param recorrido Recorrido
     * @return Participaciones del recorrido
     */
    List<Participacion> getParticipacionesRecorrido(Recorrido recorrido);   
    
    /**
     * Devuelve todas las participaciones de un usuario.
     * @param corredor Corredor
     * @return Participaciones del usuario
     */
    List<Participacion> getParticipacionesUsuario(Usuario corredor);   
    
    /**
     * Devuelve la participación de un corredor en un recorrido.
     * @param corredor Corredor
     * @param recorrido Recorrido
     * @return Participaciones del corredor en el recorrido
     */
    Participacion getParticipacionUsuarioRecorrido(Usuario corredor, Recorrido recorrido); 
    
    /**
     * Devuelve el recorrido que el usuario tiene pendiente, es decir, que ha 
     * comenzado pero todavía no ha acabado. Devuelve null si no tiene ninguno.
     * @param corredor Usuario corredor
     * @return Recorrido o null
     */
    Recorrido getRecorridoPendiente(Usuario corredor);
    
    /**
     * Abandona la realización de un recorrido por parte de un corredor que lo tenga
     * pendiente de finalizar.
     * @param corredor Corredor
     * @param recorrido Recorrido
     * @throws com.hergomsoft.easyoapi.utils.MessageException Si no se ha encontrado el control de meta.
     */
    void abandonaRecorridoUsuario(Usuario corredor, Recorrido recorrido) throws MessageException;
    
    /**
     * Crea o actualiza la participación de un corredor en un recorrido.
     * @param participacion Participación
     * @return Participación resultante
     */
    Participacion guardaParticipacion(Participacion participacion);
    
    /**
     * Devuelve true si un corredor ha participado en un recorrido, false si no.
     * @param usuario Usuario corredor
     * @param recorrido Recorrido
     * @return True si ha participado, false si no
     */
    boolean haParticipadoEnRecorrido(Usuario usuario, Recorrido recorrido);
    
    /**
     * Devuelve true si un corredor ha participado en una carrera, false si no.
     * @param usuario Usuario corredor
     * @param carrera Carrera
     * @return True si ha participado, false si no
     */
    boolean haParticipadoEnCarrera(Usuario usuario, Carrera carrera);

}
