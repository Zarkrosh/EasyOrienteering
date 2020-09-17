package com.hergomsoft.easyoapi.repositories;

import com.hergomsoft.easyoapi.models.Token;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    
    Optional<Token> findByToken(String cookie);
}