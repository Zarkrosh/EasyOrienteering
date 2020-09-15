package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Token;

public interface ITokenService {
    
    /**
     * Devuelve el token especificado.
     * @param token Token (string)
     * @return Token (objeto)
     */
    Token getToken(String token);
    
    /**
     * Guarda el token en la BD.
     * @param token Token
     * @return Token guardado o null si no pudo guardarse
     */
    Token guardaToken(Token token);
    
    /**
     * Borra el token de la BD.
     * @param token Token
     */
    void borraToken(Token token);
    
    
}
