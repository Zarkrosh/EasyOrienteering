package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Carrera;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    
    
}