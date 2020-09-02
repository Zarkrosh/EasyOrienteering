package com.hergomsoft.easyoapi.repository;

import com.hergomsoft.easyoapi.models.Control;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlRepository extends JpaRepository<Control, Long> {
    
}
