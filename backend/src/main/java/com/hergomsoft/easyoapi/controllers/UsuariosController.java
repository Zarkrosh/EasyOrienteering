package com.hergomsoft.easyoapi.controllers;

import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.requests.CambioRequest;
import com.hergomsoft.easyoapi.models.responses.MessageResponse;
import com.hergomsoft.easyoapi.services.UsuarioService;
import com.hergomsoft.easyoapi.utils.Validators;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/usuarios")
public class UsuariosController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    /**
     * Devuelve los datos del usuario que realiza la petición.
     * @param authentication Autenticación
     * @return Datos del usuario
     */
    @GetMapping("")
    public Usuario getUsuario(Authentication authentication) {
        return usuarioService.getUsuarioPeticion(authentication);
    }
    
    /**
     * Borra el usuario que realiza la petición.
     * Importante: no borra las carreras que ha organizado.
     * 
     * @param authentication 
     * @return Mensaje con el resultado
     */
    @DeleteMapping("")
    public MessageResponse borraUsuario(Authentication authentication) {
        Usuario usuario = getUsuario(authentication);
        usuarioService.deleteUsuario(usuario);
        return new MessageResponse("Cuenta borrada", false);
    }
    
    /**
     * Devuelve los datos de un usuario.
     * @param id ID del usuario
     */
    @GetMapping("{id}")
    public Usuario getUsuario(@PathVariable long id) {
        Usuario usuario = usuarioService.getUsuario(id);
        if(usuario != null) {
            return usuario;
        } else {
            // No existe la carrera -> 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario");
        }
    }
    
    /**
     * Cambia el nombre de un usuario.
     * @param authentication Autenticación
     * @param cambio Cambio con el nuevo nombre
     */
    @PostMapping("cambionombre")
    public ResponseEntity<?> cambiaNombreUsuario(Authentication authentication,
            @Valid @RequestBody CambioRequest cambio) {
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        if(usuario != null) {
            String nombre = cambio.getCambio().trim();
            if(Validators.checkCaracteresNombreUsuario(nombre)) {
                if(nombre.length() >= Usuario.MINLEN_NOMBRE && nombre.length() <= Usuario.MAXLEN_NOMBRE) {
                    if(!usuarioService.existeNombreUsuario(nombre)) {
                        // Realiza el cambio
                        usuario.setNombre(nombre);
                        return ResponseEntity.ok(usuarioService.saveUsuario(usuario));
                    } else {
                        return ResponseEntity.badRequest().body(new MessageResponse("Nombre de usuario ya utilizado", true));
                    }
                } else {
                    // Largo no válido
                    return ResponseEntity.badRequest().body(
                            new MessageResponse(
                                    String.format("El nombre de usuario debe tener entre %d y %d caracteres.", 
                                        Usuario.MINLEN_NOMBRE, Usuario.MAXLEN_NOMBRE), true)
                    );
                }
            } else {
                // Nombre de usuario no válido
                return ResponseEntity.badRequest().body(new MessageResponse("Nombre de usuario no válido", true));
            }
        } else {
            // Error de autenticación
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
    }
    
    /**
     * Cambia el club de un usuario.
     * @param cambio Cambio con el nuevo club
     */
    @PostMapping("cambioclub")
    public Usuario cambiaClubUsuario(Authentication authentication,
            @Valid @RequestBody CambioRequest cambio) {
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        if(usuario != null) {
            String club = cambio.getCambio().trim();
            if(Validators.checkCaracteresClub(club)) {
                if(club.length() >= Usuario.MINLEN_CLUB && club.length() <= Usuario.MAXLEN_CLUB) {
                    // Realiza el cambio
                    usuario.setClub(club);
                    return usuarioService.saveUsuario(usuario);
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
