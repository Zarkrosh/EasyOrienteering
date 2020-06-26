package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import java.util.List;

public interface ICarreraService {
    /**
     * Devuelve una lista con todas las carreras existentes.
     * @return Lista de carreras
     */
    List<Carrera> findAll();
    
      /**
     * Devuelve la carrera con el ID especificado.
     * @param id Identificador de la carrera
     * @return Carrera
     */
    Carrera getCarrera(long id);
    
    /**
     * Guarda la carrera en la base de datos.
     * @param carrera Carrera a guardar
     * @return Carrera guardada
     */
    Carrera saveCarrera(Carrera carrera);
    
    /**
     * Borra la carrera especificada de la base de datos.
     * @param carrera Carrera a borrar
     */
    void deleteCarrera(Carrera carrera);
    
    /**
     * Borra la carrera con el identificador especificado de la base de datos.
     * @param id Identificador de la carrera
     * @return True si ha sido borrada, false si no
     */
    boolean deleteCarrera(long id);
    
    /**
     * Devuelve true si existe una carrera con el identificador especificado.
     * @param id Identificador de la carrera
     * @return True si existe
     */
    boolean existeCarrera(long id);
}
