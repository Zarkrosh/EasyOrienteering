package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Recorrido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecorridoRepository extends JpaRepository<Recorrido, Long> {
    
}