package com.hergomsoft.easyoapi.repositories;

import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, Long> {
    
    /**
     * Devuelve las participaciones en un recorrido.
     * @param recorrido Recorrido
     * @return Participaciones del recorrido
     */
    List<Participacion> findByRecorrido(Recorrido recorrido);
    
    /**
     * Devuelve todas las participaciones de un corredor, en orden temporal decreciente.
     * @param corredor Corredor
     * @return Participaciones del usuario
     */
    List<Participacion> findByCorredorOrderByFechaInicioDesc(Usuario corredor);
    
    /**
     * Devuelve el recorrido que tenga pendiente un corredor.
     * @param idCorredor ID del corredor
     * @return ID del recorrido pendiente
     */
    @Query(value = "SELECT recorrido_id FROM participaciones WHERE corredor_id = :idCorredor AND pendiente IS TRUE LIMIT 1", nativeQuery = true)
    public Optional<Long> getRecorridoPendienteCorredor(@Param("idCorredor") long idCorredor);
    
    /**
     * Devuelve la participación de un usuario en un recorrido, si lat iene.
     * @param idCorredor ID del corredor
     * @param idRecorrido ID del recorrido
     * @return Participación del corredor en el recorrido
     */
    @Query(value = "SELECT * FROM participaciones WHERE corredor_id = :idCorredor AND recorrido_id = :idRecorrido", nativeQuery = true)
    public Optional<Participacion> getParticipacionUsuarioRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
    
    /**
     * Marca un recorrido como abandonado y finalizado.
     * @param idCorredor ID del corredor
     * @param idRecorrido ID del recorrido
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE participaciones SET abandonado = TRUE, pendiente = FALSE WHERE corredor_id = :idCorredor AND recorrido_id = :idRecorrido", nativeQuery = true)
    public void abandonaRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
    
    
    /**
     * Devuelve true si un corredor ha participado en un recorrido, false si no.
     * @param idCorredor ID del usuario corredor
     * @param idRecorrido ID del recorrido
     * @return True si ha participado, false si no
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM participaciones WHERE corredor_id = :idCorredor AND recorrido_id = :idRecorrido", 
        nativeQuery = true)
    public boolean haParticipadoEnRecorrido(@Param("idCorredor") long idCorredor, @Param("idRecorrido") long idRecorrido);
    
    /**
     * Devuelve true si un corredor ha participado en una carrera, false si no.
     * @param idCorredor ID del usuario corredor
     * @param idCarrera ID de la carrera
     * @return True si ha participado, false si no
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM participaciones p INNER JOIN recorridos r ON p.recorrido_id = r.id "
            + "WHERE corredor_id = :idCorredor AND carrera_id = :idCarrera", 
        nativeQuery = true)
    public boolean haParticipadoEnCarrera(@Param("idCorredor") long idCorredor, @Param("idCarrera") long idCarrera);
}
