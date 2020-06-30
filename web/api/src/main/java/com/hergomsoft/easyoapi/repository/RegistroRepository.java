package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Registro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    
}