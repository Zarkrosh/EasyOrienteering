package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Carrera;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    /**
     * Devuelve las carreras que ha organizado el usuario especificado.
     * @param idUsuario ID del usuario
     * @return Carreras organizadas por el usuario
     */
    public List<Carrera> findByOrganizadorId(long idUsuario);
    
    /**
     * Devuelve las carreras que validan los campos de búsqueda.Para realizar una 
     * búsqueda independiente de la capitalización, se deben pasar los parámetros en mayúsculas.
     * @param idUsuario ID del usuario que realiza la petición
     * @param nombre Nombre de la carrera 
     * @param tipo Tipo de la carrera 
     * @param modalidad Modalidad de la carrera
     * @param offset Desplazamiento de paginación
     * @param numero Número de resultados
     * @return Carreras resultantes
     */
    @Query(value = "SELECT * FROM carreras WHERE UPPER(nombre) LIKE %:nombre% AND tipo\\:\\:text LIKE %:tipo% AND modalidad\\:\\:text LIKE %:modalidad% "
            + "AND (privada IS FALSE OR organizador_id = :idUsuario) ORDER BY fecha DESC, id DESC OFFSET :offset LIMIT :numero",
           nativeQuery = true)
    public List<Carrera> buscaCarreras(@Param("idUsuario") long idUsuario, @Param("nombre") String nombre, @Param("tipo") String tipo, 
            @Param("modalidad") String modalidad, @Param("offset") int offset, @Param("numero") int numero);
    
}