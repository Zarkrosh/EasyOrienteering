package com.hergomsoft.easyoapi.controllers;


import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.CarrerasUsuarioResponse;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/carreras")
public class CarrerasController {
    
    @Autowired
    private CarreraService carrerasService;
    
    @Autowired
    private UsuarioService usuariosService;
    
    @GetMapping("")
    public CarrerasUsuarioResponse getCarrerasUsuario() {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        List<Carrera> corridas = carrerasService.getCarrerasCorridasUsuario(usuario);
        List<Carrera> organizadas = carrerasService.getCarrerasOrganizadasUsuario(usuario);
        return new CarrerasUsuarioResponse(corridas, organizadas);
    }
    
    @GetMapping("/{id}")
    public Carrera getCarrera(@PathVariable long id) {
        Carrera res = carrerasService.getCarrera(id);
        if(res == null) {
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la carrera con ese ID");
        } else {
            return res;
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Carrera nuevaCarrera(@RequestBody Carrera carrera) {
        // El creador de la carrera es el usuario que realiza la petición
        // TODO
        Usuario org = usuariosService.getUsuario(2L); // Usuario de prueba
        carrera.setOrganizador(org);
        // TODO Comprobar datos válidos
        
        // Crea la carrera
        return carrerasService.saveCarrera(carrera);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> editarCarrera(@RequestBody Carrera carrera, @PathVariable long id) {
        Carrera res = carrerasService.getCarrera(id);
        if(res == null) {
            return ResponseEntity.notFound().build();
        }

        // TODO Comprobar datos válidos
        carrera.setId(id);
        carrerasService.editCarrera(carrera);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public void borrarCarrera(@PathVariable long id) {
        carrerasService.deleteCarrera(id);
    }
    
    @GetMapping("/{id}/secretos")
    public Map<String, String> getSecretosControlesCarrera(@PathVariable long id) {
        // Solo es accesible por el organizador de la carrera
        // TODO 
        
        Map<String, String> res = new HashMap<>();
        Carrera carrera = carrerasService.getCarrera(id);
        if(carrera != null) {
            res = carrerasService.getSecretosCarrera(carrera);
        } else {
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la carrera con ese ID");
        }
        
        return res;
    }
    
}
