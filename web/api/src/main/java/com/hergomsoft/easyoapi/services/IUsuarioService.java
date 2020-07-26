package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Usuario;
import java.util.List;

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
     * Borra el usuario con el identificador especificado de la base de datos.
     * @param id Identificador del usuario
     */
    void deleteUsuario(long id);
    
    /**
     * Devuelve el usuario que ha realizado la petición.
     * @return Usuario
     */
    Usuario getUsuarioPeticion();
    
}
