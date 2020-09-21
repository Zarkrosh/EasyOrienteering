package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.CarreraSimplificada;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface ICarreraService {
    /**
     * Devuelve una lista con todas las carreras existentes.
     * @return Lista de carreras
     */
    List<Carrera> findAll();
    
    /**
     * Devuelve una lista con todas las carreras resultantes de la búsqueda.
     * @param idUsuario ID del usuario que realiza la petición
     * @param nombre Nombre
     * @param tipo Tipo
     * @param modalidad Modalidad
     * @return Lista de carreras
     */
    List<CarreraSimplificada> buscaCarreras(long idUsuario, String nombre, String tipo, String modalidad, Pageable pageable);
    
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
     * @param anterior Carrera anterior
     * @param nueva Carrera editada
     */
    void editCarrera(Carrera anterior, Carrera nueva);
    
    /**
     * Borra la carrera con el identificador especificado de la base de datos.
     * @param id Identificador de la carrera
     * @return True si ha sido borrada, false si no
     */
    boolean deleteCarrera(long id);
    
    
    /**
     * Devuelve true si el secreto proporcionado coincide con el calculado para el control.
     * @param secreto Secreto proporcionado
     * @param control Control de una carrera
     * @param carrera Carrera
     * @return True si los secretos coinciden, false si no
     */
    boolean checkSecretoControl(String secreto, Control control, Carrera carrera);
    
    /**
     * Devuelve true si el secreto proporcionado coincide con el calculado para el recorrido.
     * @param secreto Secreto proporcionado
     * @param recorrido Recorrido
     * @return True si los secretos coinciden, false si no
     */
    boolean checkSecretoRecorrido(String secreto, Recorrido recorrido);
    
    /**
     * Devuelve un mapa con los secretos de los controles de una casrrera, los cuales 
     * dependen del secreto de la carrera a la que pertenece. Su generación es el 
     * MD5 de la concatenación del código de control con el secreto de la carrera.
     * @param carrera Carrera de la que obtener los secretos
     * @return Mapa de secretos
     */
    Map<String, String> getControlesConSecretosCarrera(Carrera carrera);
    
    /**
     * Devuelve el secreto de un recorrido, el cual depende del secreto de la carrera
     * a la que pertenece. Su generación es el MD5 de la concatenación del nombre del
     * recorrido con el secreto de la carrera.
     * @param recorrido Recorrido del cual obtener el secreto
     * @return Secreto del recorrido
     */
    String getSecretoRecorrido(Recorrido recorrido);
    
    /**
     * Devuelve el secreto de un control, el cual depende del secreto de la carrera
     * a la que pertenece. Su generación es el MD5 de la concatenación del código del
     * control con el secreto de la carrera.
     * @param control Control del cual obtener el secreto
     * @param carrera Carrera
     * @return Secreto del control
     */
    String getSecretoControl(Control control, Carrera carrera);
    
    /**
     * Devuelve una lista con las carreras que ha corrido el usuario especificado.
     * @param usuario Usuario
     * @return Lista de carreras corridas por el usuario
     */
    List<Carrera> getCarrerasParticipadasUsuario(Usuario usuario);
    
    /**
     * Devuelve una lista con las carreras que ha organizado el usuario especificado.
     * @param usuario Usuario
     * @return Lista de carreras organizadas por el usuario
     */
    List<Carrera> getCarrerasOrganizadasUsuario(Usuario usuario);
    
}
