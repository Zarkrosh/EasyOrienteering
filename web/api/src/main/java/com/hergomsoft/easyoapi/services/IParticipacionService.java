package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
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
    
    /**
     * Devuelve el código del siguiente que tiene que registrar un corredor
     * en un recorrido en línea. Devuelve null si no quedan más controles por registrar.
     * @param corredor Usuario corredor
     * @param recorrido Recorrido
     * @return Código del control o null
     
    String getCodigoSiguienteControlRecorrido(Usuario corredor, Recorrido recorrido);*/
    
    /**
     * Devuelve true si el corredor ha registrado ya el control en una carrera score.
     * @param corredor Usuario corredor
     * @param control Control
     * @param recorrido Recorrido
     * @return True si ya lo ha registrado, false si no
    
    boolean haRegistradoControl(Usuario corredor, Control control, Recorrido recorrido); */
    
    /**
     * Devuelve true si el corredor ha registrado ya alguno de los controles de
     * un recorrido, y false en caso contrario.
     * @param corredor Usuario corredor
     * @param recorrido Recorrido 
     * @return True o false
   
    boolean haCorridoRecorrido(Usuario corredor, Recorrido recorrido);  */
    
    
   
}
