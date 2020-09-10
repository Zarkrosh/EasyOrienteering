package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repositories.UsuarioRepository;
import com.hergomsoft.easyoapi.security.services.UserDetailsImpl;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }
    
    @Override
    public Usuario getUsuario(long id) {
        try {
            Optional<Usuario> res = usuarioRepository.findById(id);
            return (res.isPresent()) ? res.get() : null;
        } catch(Exception e) {
            System.out.println("[!] Error al obtener el usuario con id " + id);
            return null;
        }
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        if(usuario != null) return usuarioRepository.save(usuario);
        else {
            throw new IllegalArgumentException("El usuario a guardar no puede ser null");
        }
    }
    
    @Override
    public void deleteUsuario(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }
    
    @Override
    public boolean existeNombreUsuario(String nombre) {
        return usuarioRepository.existsByNombre(nombre);
    }
    
    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    @Override
    public Usuario getUsuarioPeticion(Authentication auth) throws ResponseStatusException {
        //return getUsuario(3); // TEST ANDROID
        
        Usuario res = null;
        if(auth != null && auth.isAuthenticated()) {
            UserDetailsImpl d = (UserDetailsImpl) auth.getPrincipal();
            if(d.getId() != null) res = getUsuario(d.getId());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay autenticación");
        }
        
        return res;
    }

}
