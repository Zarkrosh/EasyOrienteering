package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.utils.MessageException;
import java.util.List;

public interface IRegistroService {

    List<Registro> findAll();
    
    /**
     * Registra el paso de un corredor por un control. Se aplican validaciones
     * para evitar añadir más registros de los necesarios.
     * @param registro Datos de registro
     * @return Registro exitoso o null si no se ha registrado
     */
    Registro registraPasoControl(Registro registro); 

    /**
     * Devuelve el ID del recorrido que el usuario tiene pendiente, es decir,
     * que ha comenzado pero todavía no ha acabado. Devuelve null si no tiene
     * ninguno.
     * @param corredor Usuario corredor
     * @return Recorrido o null
     */
    Recorrido getRecorridoPendiente(Usuario corredor);
    
    /**
     * Devuelve el código del siguiente que tiene que registrar un corredor
     * en un recorrido en línea. Devuelve null si no quedan más controles por registrar.
     * @param corredor Usuario corredor
     * @param recorrido Recorrido
     * @return Código del control o null
     */
    String getCodigoSiguienteControlRecorrido(Usuario corredor, Recorrido recorrido);
    
    /**
     * Devuelve true si el corredor ha registrado ya el control en una carrera score.
     * @param corredor Usuario corredor
     * @param control Control
     * @param recorrido Recorrido
     * @return True si ya lo ha registrado, false si no
     */
    boolean haRegistradoControl(Usuario corredor, Control control, Recorrido recorrido);
    
    /**
     * Devuelve true si el corredor ha registrado ya alguno de los controles de
     * un recorrido, y false en caso contrario.
     * @param corredor Usuario corredor
     * @param recorrido Recorrido 
     * @return True o false
     */
    boolean haCorridoRecorrido(Usuario corredor, Recorrido recorrido);
    
    /**
     * Devuelve los registros de todos los corredores participantes en un recorrido.
     * @param recorrido Recorrido
     * @return Registros del recorrido
     */
    List<Registro> getRegistrosRecorrido(Recorrido recorrido);
    
    /**
     * Devuelve los registros de un corredor en un recorrido, en orden creciente temporal.
     * @param corredor Corredor
     * @param recorrido Recorrido
     * @return Registros del corredor en el recorrido
     */
    Registro[] getRegistrosUsuarioRecorrido(Usuario corredor, Recorrido recorrido);
    
    /**
     * Abandona la realización de un recorrido por parte de un corredor que lo tenga
     * pendiente de finalizar.
     * @param corredor Corredor
     * @param recorrido Recorrido
     * @throws com.hergomsoft.easyoapi.utils.MessageException Si no se ha encontrado el control de meta.
     */
    void abandonaRecorrido(Usuario corredor, Recorrido recorrido) throws MessageException;
}
