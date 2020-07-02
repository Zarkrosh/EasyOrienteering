package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    
    /**
     * Devuelve el recorrido que tenga pendiente un corredor, o null si no tiene ninguno.
     * @param idCorredor ID del corredor
     * @return ID del recorrido pendiente
     */
    @Query(value = "SELECT recorrido_id FROM registros WHERE corredor_id = :idCorredor GROUP BY recorrido_id EXCEPT " +
        "SELECT recorrido_id FROM registros AS r INNER JOIN controles AS c ON r.control_id = c.id WHERE tipo = 'META' AND corredor_id = :idCorredor", 
        nativeQuery = true)
    public Optional<Long> getRecorridoPendienteCorredor(@Param("idCorredor") long idCorredor);
    
    
    /**
     * Devuelve el identificador del siguiente control a registrar en un recorrido 
     * por parte de un corredor. No devuelve nada si no le quedan controles pendientes. 
     * @param idCorredor ID del corredor
     * @param idRecorrido ID del recorrido
     * @return ID del siguiente control, o vacío
     */
    @Query(value = "SELECT cr.control_id FROM registros AS r RIGHT OUTER JOIN controles_recorrido AS cr ON (r.control_id = cr.control_id "
        + "AND r.recorrido_id = cr.recorrido_id AND r.corredor_id = :idCorredor) WHERE cr.recorrido_id = :idRecorrido AND fecha IS NULL ORDER BY orden ASC LIMIT 1", 
        nativeQuery = true)
    public Optional<Long> getIDSiguienteControlRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
    
    
    /**
     * Devuelve los IDs de los controles registrados por un corredor en un recorrido, en orden.
     * @param idCorredor ID del corredor
     * @param idRecorrido ID del recorrido
     * @return Lista ordenada de IDs de controles registrados
     */
    @Query(value = "SELECT control_id FROM registros WHERE corredor_id = :idCorredor AND recorrido_id = :idRecorrido ORDER BY fecha ASC", 
        nativeQuery = true)
    public List<Long> getIDControlesRegistradosRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
}