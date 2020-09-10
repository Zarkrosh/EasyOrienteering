package com.hergomsoft.easyoapi.security.services;

import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repositories.UsuarioRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService  {
    @Autowired
    UsuarioRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByNombre(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre: " + username));

        return UserDetailsImpl.build(usuario);
    }
}
