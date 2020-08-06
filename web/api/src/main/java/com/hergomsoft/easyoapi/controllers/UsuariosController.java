package com.hergomsoft.easyoapi.controllers;

import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.requests.CambioRequest;
import com.hergomsoft.easyoapi.services.UsuarioService;
import com.hergomsoft.easyoapi.utils.Validators;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {
    
    @Autowired
    private UsuarioService usuariosService;
    
    /**
     * Devuelve los datos de un usuario.
     * @param id ID del usuario
     */
    @GetMapping("{id}")
    public Usuario getUsuario(@PathVariable long id) {
        //Usuario peticion = usuariosService.getUsuarioPeticion(); // ¿Necesario?
        Usuario usuario = usuariosService.getUsuario(id);
        if(usuario != null) {
            return usuario;
        } else {
            // No existe la carrera -> 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario");
        }
    }
    
    /**
     * Cambia el nombre de un usuario.
     * @param cambio Cambio con el nuevo nombre
     */
    @PostMapping("cambionombre")
    public Usuario cambiaNombreUsuario(@Valid @RequestBody CambioRequest cambio) {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        if(usuario != null) {
            String nombre = cambio.getCambio().trim();
            if(Validators.checkCaracteresNombreUsuario(nombre)) {
                if(nombre.length() >= Usuario.MINLEN_NOMBRE && nombre.length() <= Usuario.MAXLEN_NOMBRE) {
                    // Realiza el cambio
                    usuario.setNombre(nombre);
                    return usuariosService.saveUsuario(usuario);
                } else {
                    // Largo no válido
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                            String.format("El nombre de usuario debe tener entre %d y %d caracteres.", 
                                Usuario.MINLEN_NOMBRE, Usuario.MAXLEN_NOMBRE));
                }
            } else {
                // Nombre de usuario no válido
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de usuario no válido");
            }
        } else {
            // Error de autenticación
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
    }
    
    /**
     * Cambia el club de un usuario.
     * @param cambio Cambio con el nuevo club
     */
    @PostMapping("cambioclub")
    public Usuario cambiaClubUsuario(@Valid @RequestBody CambioRequest cambio) {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        if(usuario != null) {
            String club = cambio.getCambio().trim();
            if(Validators.checkCaracteresClub(club)) {
                if(club.length() >= Usuario.MINLEN_CLUB && club.length() <= Usuario.MAXLEN_CLUB) {
                    // Realiza el cambio
                    usuario.setClub(club);
                    return usuariosService.saveUsuario(usuario);
                } else {
                    // Largo no válido
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                            String.format("El nombre del club debe tener entre %d y %d caracteres.", 
                                Usuario.MINLEN_CLUB, Usuario.MAXLEN_CLUB));
                }
            } else {
                // Nombre de usuario no válido
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de club no válido");
            }
        } else {
            // Error de autenticación
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }        
    }
    
}
