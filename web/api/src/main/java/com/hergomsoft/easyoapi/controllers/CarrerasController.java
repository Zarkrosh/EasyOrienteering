package com.hergomsoft.easyoapi.controllers;


import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.services.ICarreraService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carreras")
public class CarrerasController {
    
    @Autowired
    private ICarreraService carrerasService;
    
    @GetMapping("")
    public List<Carrera> getCarreras() {
        return carrerasService.findAll();
    }

    @PostMapping("")
    public String nuevaCarrera() {
        return "TODO Nueva carrera";
    }
    
    @PutMapping("")
    public String editarCarrera() {
        return "TODO Editar carrera";
    }
    
    @DeleteMapping("")
    public String borrarCarrera() {
        return "TODO Borrar carrera";
    }
}