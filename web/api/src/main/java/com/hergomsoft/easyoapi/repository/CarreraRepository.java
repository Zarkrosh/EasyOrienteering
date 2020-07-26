package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Carrera;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    
    /**
     * Inserta el control especificado para una carrera.
     * @param codigo Código del control
     * @param idCarrera ID de la carrera a la que pertenece
     * @param tipo Tipo de control
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO CONTROLES(CODIGO, CARRERA_ID, TIPO) VALUES (:codigo, :idCarrera, :tipo)", nativeQuery = true)
    public void insertaControl(@Param("codigo") String codigo, @Param("idCarrera") long idCarrera, @Param("tipo") String tipo);
    
    /**
     * Devuelve las carreras que ha corrido el usuario especificado.
     * @param idUsuario ID del usuario
     * @return Carreras corridas por el usuario
     */
    @Query(value = "SELECT * FROM carreras WHERE id IN (SELECT carrera_id FROM registros r INNER JOIN controles c ON r.control_id = c.id WHERE corredor_id = :idUsuario and tipo = 'META')", nativeQuery = true)
    public List<Carrera> getCarrerasCorridasUsuario(@Param("idUsuario") long idUsuario);
    
    /**
     * Devuelve las carreras que ha organizado el usuario especificado.
     * @param idUsuario ID del usuario
     * @return Carreras organizadas por el usuario
     */
    public List<Carrera> findByOrganizadorId(long idUsuario);
    
}