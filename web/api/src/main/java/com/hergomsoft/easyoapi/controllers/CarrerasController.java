package com.hergomsoft.easyoapi.controllers;


import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.repository.CarreraRepository;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/carreras")
public class CarrerasController {
    
    @Autowired
    private CarreraRepository carrerasRepository;
    
    @GetMapping("")
    public List<Carrera> getCarreras() {
        return carrerasRepository.findAll();
    }

    @PostMapping("")
    public ResponseEntity<Object> nuevaCarrera(@RequestBody Carrera carrera) {
        // TODO Comprobar datos válidos
        Carrera guardada = carrerasRepository.save(carrera);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(guardada.getId()).toUri();
        
        return ResponseEntity.created(location).build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> editarCarrera(@RequestBody Carrera carrera, @PathVariable long id) {
        Optional<Carrera> opCarrera = carrerasRepository.findById(id);
        if(opCarrera.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // TODO Comprobar datos válidos
        carrera.setId(id);
        carrerasRepository.save(carrera);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    public void borrarCarrera(@PathVariable long id) {
        carrerasRepository.deleteById(id);
    }
}