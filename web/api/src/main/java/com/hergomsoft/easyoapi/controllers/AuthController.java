package com.hergomsoft.easyoapi.controllers;

import com.hergomsoft.easyoapi.models.Token;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.requests.CambioPassRequest;
import com.hergomsoft.easyoapi.models.requests.LoginRequest;
import com.hergomsoft.easyoapi.models.requests.RegistroCuentaRequest;
import com.hergomsoft.easyoapi.models.responses.LoginResponse;
import com.hergomsoft.easyoapi.models.responses.MessageResponse;
import com.hergomsoft.easyoapi.security.services.UserDetailsImpl;
import com.hergomsoft.easyoapi.services.TokenService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import com.hergomsoft.easyoapi.utils.Utils;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authManager;
    
    @Autowired
    UsuarioService usuarioService;
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    PasswordEncoder encoder;
    
    
    @PostMapping("/login")
    public LoginResponse loginUsuario(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
        List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());
        
        String sToken = Utils.sha256(UUID.randomUUID().toString());
        Usuario usuario = usuarioService.getUsuario(userDetails.getId());
        if(usuario == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el usuario.");
        Token token = new Token(sToken, usuario);
        if(tokenService.guardaToken(token) == null) {
            // No se creó correctamente
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, sToken);
        }

        return new LoginResponse(sToken, 
            userDetails.getId(), 
            roles);
    }
    
    @PostMapping("/logout")
    public MessageResponse logoutUsuario(Authentication authentication,
            HttpServletRequest request) {
        MessageResponse res = new MessageResponse("No se pudo cerrar la sesión", true);
        if(authentication != null && authentication.isAuthenticated()) {
            Usuario usuPeticion = usuarioService.getUsuarioPeticion(authentication);
            String sToken = Utils.parseToken(request);
            if(sToken != null) {
                Token token = tokenService.getToken(sToken);
                // Comprueba que es su sesión
                if(Objects.equals(token.getUsuario().getId(), usuPeticion.getId())) {
                    tokenService.borraToken(token);
                    res = new MessageResponse("Sesión cerrada.", false);
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No estás autorizado");
        }
        
        return res;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroCuentaRequest signupRequest) {
        try {
            // Delay a propósito
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Comprueba de datos de entrada
        String nombre = signupRequest.getUsername().trim();
        String email = signupRequest.getEmail().trim();
        String club = signupRequest.getClub().trim();
        String password = signupRequest.getPassword().trim();
        // TODO Comprobar charsets
        if(password.length() < Usuario.MINLEN_PASS) {
            return ResponseEntity.badRequest().body(
                String.format("La contraseña debe tener al menos %d caracteres", Usuario.MINLEN_PASS)
            );
        }
        
        // Comprueba usuario existente
        if(usuarioService.existeNombreUsuario(nombre)) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese nombre");
        }
        // Comprueba email existente
        if(usuarioService.existeEmail(email)) {
            return ResponseEntity.badRequest().body("Ya existe una cuenta asociada a este email");
        }
        
        // Crea cuenta de usuario
        Date fechaRegistro = new Date();
        Set<Usuario.RolUsuario> roles = new HashSet<>();
        roles.add(Usuario.RolUsuario.USUARIO);
        Usuario usuario = new Usuario(nombre, email, club, encoder.encode(password), fechaRegistro, roles);
        Usuario u = usuarioService.saveUsuario(usuario);
        
        if(u != null) return ResponseEntity.ok(new MessageResponse("Usuario registrado", false));
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Error al crear el usuario", true));
    }
    
    @PostMapping("/change")
    public ResponseEntity<?> cambiaPassUsuario(Authentication authentication,
                                            @Valid @RequestBody CambioPassRequest request) {
        
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        
        String prevPass = request.getPrevPassword().trim();
        String nuevaPass = request.getNuevaPassword().trim();
        
        if(nuevaPass.length() < Usuario.MINLEN_PASS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(String.format("La nueva contraseña debe tener al menos %d caracteres", Usuario.MINLEN_PASS), true));
        }
            
        if(!encoder.matches(prevPass, usuario.getPassword())) 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Contraseña anterior incorrecta", true));
        
        usuario.setPassword(encoder.encode(nuevaPass));
        usuarioService.saveUsuario(usuario);
        
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada", false));
    }

}
