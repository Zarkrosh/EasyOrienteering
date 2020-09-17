package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Usuario;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

public interface IUsuarioService {
    /**
     * Devuelve una lista con todos los usuarios existentes.
     * @return Lista de usuarios
     */
    List<Usuario> findAll();
    
    /**
     * Devuelve el usuario con el ID especificado.
     * @param id Identificador del usuario
     * @return Usuario
     */
    Usuario getUsuario(long id);
    
    /**
     * Guarda el usuario en la base de datos.
     * @param usuario Usuario a guardar
     * @return Usuario guardado
     */
    Usuario saveUsuario(Usuario usuario);
    
    /**
     * Borra el usuario de la base de datos.
     * @param usuario Usuario
     */
    void deleteUsuario(Usuario usuario);
    
    /**
     * Devuelve true si el nombre de usuario especificado ya está usado por otro usuario.
     * @param nombre Nombre de usuario
     * @return True si ya está utilizado
     */
    boolean existeNombreUsuario(String nombre);
    
    /**
     * Devuelve true si el email especificado ya está usado por otro usuario.
     * @param email Email de usuario
     * @return True si ya está utilizado
     */
    boolean existeEmail(String email);
    
    /**
     * Devuelve el usuario que ha realizado la petición.
     * @param auth Autenticación de usuario (puede ser null)
     * @return Usuario
     */
    Usuario getUsuarioPeticion(Authentication auth) throws ResponseStatusException;
    
}
