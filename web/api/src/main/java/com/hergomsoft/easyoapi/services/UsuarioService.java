package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository repoUsuarios;

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) repoUsuarios.findAll();
    }
    
    @Override
    public Usuario getUsuario(long id) {
        try {
            Optional<Usuario> res = repoUsuarios.findById(id);
            return (res.isPresent()) ? res.get() : null;
        } catch(Exception e) {
            System.out.println("[!] Error al obtener la usuario con id " + id);
            return null;
        }
    }

    @Override
    public Usuario saveUsuario(Usuario usuario) {
        if(usuario != null) return repoUsuarios.save(usuario);
        else {
            throw new IllegalArgumentException("La usuario a guardar no puede ser null");
        }
    }
    
    @Override
    public void deleteUsuario(long id) {
        repoUsuarios.deleteById(id);
    }

}
