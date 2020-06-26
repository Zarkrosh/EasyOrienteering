package com.hergomsoft.easyoapi.controllers;


import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import java.net.URI;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/carreras")
public class CarrerasController {
    
    @Autowired
    private CarreraService carrerasService;
    
    @Autowired
    private UsuarioService usuariosService;
    
    @GetMapping("")
    public List<Carrera> getCarreras() {
        return carrerasService.findAll();
    }
    
    @GetMapping("/{id}")
    public Carrera getCarrera(@PathVariable long id) {
        Carrera res = carrerasService.getCarrera(id);
        if(res == null) {
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la carrera que quieres obtener");
        } else {
            return res;
        }
    }

    @PostMapping("")
    public ResponseEntity<Object> nuevaCarrera(@RequestBody Carrera carrera) {
        // El creador de la carrera es el usuario que realiza la petición
        // TODO
        Usuario org = usuariosService.getUsuario(1L); // Usuario de prueba
        carrera.setOrganizador(org);
        // TODO Comprobar datos válidos
        Carrera guardada = carrerasService.saveCarrera(carrera);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(guardada.getId()).toUri();
        
        return ResponseEntity.created(location).build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> editarCarrera(@RequestBody Carrera carrera, @PathVariable long id) {
        Carrera res = carrerasService.getCarrera(id);
        if(res != null) {
            return ResponseEntity.notFound().build();
        }

        // TODO Comprobar datos válidos
        carrera.setId(id);
        carrerasService.saveCarrera(carrera);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public void borrarCarrera(@PathVariable long id) {
        carrerasService.deleteCarrera(id);
    }
}