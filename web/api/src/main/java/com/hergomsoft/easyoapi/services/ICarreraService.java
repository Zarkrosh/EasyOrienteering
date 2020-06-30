package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import java.util.List;
import java.util.Map;

public interface ICarreraService {
    /**
     * Devuelve una lista con todas las carreras existentes.
     * @return Lista de carreras
     */
    List<Carrera> findAll();
    
      /**
     * Devuelve la carrera con el ID especificado, o null si no existe.
     * @param id Identificador de la carrera
     * @return Carrera o null
     */
    Carrera getCarrera(long id);
    
    /**
     * Crea una nueva carrera en la base de datos.
     * @param carrera Carrera a crear
     * @return Carrera creada
     */
    Carrera newCarrera(Carrera carrera);
    
    /**
     * Actualiza los datos de una carrera en la base de datos.
     * @param carrera Carrera a editar
     */
    void editCarrera(Carrera carrera);
    
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
    
    /**
     * Devuelve un mapa de asociaciones de los controles de una carrera y sus secretos.
     * @param id ID de la carrera
     * @return Secretos de los controles de la carrera
     */
    Map<String, String> getSecretosControles(long id);
    
    /**
     * Devuelve true si el secreto proporcionado coincide con el calculado para el control.
     * @param secreto Secreto proporcionado
     * @param control Control de una carrera
     * @return True si los secretos coinciden, false si no
     */
    boolean checkSecretoControl(String secreto, Control control);
}
