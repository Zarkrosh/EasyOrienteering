package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Registro;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
     * Devuelve el código del siguiente control a registrar en un recorrido 
     * por parte de un corredor. No devuelve nada si no le quedan controles pendientes. 
     * @param idCorredor ID del corredor
     * @param idRecorrido ID del recorrido
     * @return Código del siguiente control, o vacío
     */
    @Query(value = "SELECT cr.control_codigo FROM registros AS r INNER JOIN controles AS c ON (r.control_id = c.id AND r.corredor_id = :idCorredor) "
            + "RIGHT OUTER JOIN controles_recorrido AS cr ON (c.codigo = cr.control_codigo AND r.recorrido_id = cr.recorrido_id) "
            + "WHERE cr.recorrido_id = :idRecorrido AND fecha IS NULL ORDER BY orden ASC LIMIT 1", 
        nativeQuery = true)
    public Optional<String> getCodigoSiguienteControlRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
    
    
    /**
     * Devuelve los IDs de los controles registrados por un corredor en un recorrido, en orden.
     * @param idCorredor ID del corredor
     * @param idRecorrido ID del recorrido
     * @return Lista ordenada de IDs de controles registrados
     */
    @Query(value = "SELECT control_id FROM registros WHERE corredor_id = :idCorredor AND recorrido_id = :idRecorrido ORDER BY fecha ASC", 
        nativeQuery = true)
    public List<Long> getIDControlesRegistradosRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
    
    
    /**
     * Devuelve true si un corredor tiene algún registro en un recorrido, false si no.
     * @param idCorredor ID del usuario corredor
     * @param idRecorrido ID del recorrido
     * @return True si hay registros, false si no
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM registros WHERE corredor_id = :idCorredor AND recorrido_id = :idRecorrido", 
        nativeQuery = true)
    public boolean haCorridoRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);

    /**
     * Devuelve los registros de todos los corredores en un recorrido.
     * @param idRecorrido ID del recorrido
     * @return Registros del recorrido
     */
    @Query(value="SELECT * FROM registros WHERE recorrido_id = :idRecorrido", nativeQuery = true)
    public List<Registro> getRegistrosRecorrido(@Param("idRecorrido") long idRecorrido);
    
    /**
     * Devuelve los registros de un corredor en un recorrido, ordenados de forma creciente en el tiempo.
     * @param idCorredor ID del usuario corredor
     * @param idRecorrido ID del recorrido
     * @return Registros del usuario en el recorrido
     */
    @Query(value="SELECT * FROM registros WHERE corredor_id = :idCorredor AND recorrido_id = :idRecorrido ORDER BY fecha ASC", nativeQuery = true)
    public Registro[] getRegistrosUsuarioRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
    
    /**
     * Abandona un recorrido pendiente de un corredor.
     * @param idCorredor ID del usuario corredor
     * @param idRecorrido ID del recorrido
     * @param idControlMeta ID del control de meta del recorrido
     */
    @Modifying
    @Transactional
    @Query(value="INSERT INTO REGISTROS(CORREDOR_ID, CONTROL_ID, RECORRIDO_ID, FECHA) VALUES (:idCorredor, :idControl, :idRecorrido, NULL)", nativeQuery = true)
    public void abandonaRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido, @Param("idControl") long idControlMeta);
}