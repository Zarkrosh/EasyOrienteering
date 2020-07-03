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
    Carrera saveCarrera(Carrera carrera);
    
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
     * Devuelve true si el secreto proporcionado coincide con el calculado para el control.
     * @param secreto Secreto proporcionado
     * @param control Control de una carrera
     * @return True si los secretos coinciden, false si no
     */
    boolean checkSecretoControl(String secreto, Control control);
    
    /**
     * Devuelve un mapa con los secretos de los controles de una casrrera, los cuales 
     * dependen del secreto de la carrera a la que pertenece. Su generación es el 
     * MD5 de la concatenación del código de control con el secreto de la carrera.
     * @param carrera Carrera de la que obtener los secretos
     * @return Mapa de secretos
     */
    Map<String, String> getSecretosCarrera(Carrera carrera);
    
    /**
     * Devuelve el secreto de un control, el cual depende del secreto de la carrera
     * a la que pertenece. Su generación es el MD5 de la concatenación del código de
     * control con el secreto de la carrera.
     * @param control Control del cual obtener el secreto
     * @return Secreto del control
     */
    String getSecretoControl(Control control);
}
