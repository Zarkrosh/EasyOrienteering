package com.hergomsoft.easyoapi.controllers;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.PeticionRegistro;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.RegistroService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/registros")
public class RegistrosController {
    
    @Autowired
    private CarreraService carrerasService;
    
    @Autowired
    private RegistroService registrosService;
    
    @Autowired
    private UsuarioService usuariosService;
    
    // DEBUG //
    @GetMapping("")
    public List<Registro> getRegistros() {
        return registrosService.findAll();
    }
    
    // DEBUG //
    @GetMapping("/{id}")
    public Carrera getRegistrosCarrera(@PathVariable long id) {
        Carrera res = carrerasService.getCarrera(id);
        if(res == null) {
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la carrera");
        } else {
            // TODO
            return res;
        }
    }
    
    
    @PostMapping("/{id}")
    public Registro registrarPasoControl(@PathVariable long id, @Valid @RequestBody PeticionRegistro peticion) {
        Carrera carrera = carrerasService.getCarrera(id);
        if(carrera != null) {
            Control control = carrera.getControles().get(peticion.getCodigo());
            if(control != null) {
                boolean valido = carrerasService.checkSecretoControl(
                    peticion.getSecreto(), control);

                if(valido) {
                    // Registro válido
                    
                    // Obtiene el usuario corredor
                    // TODO: extraer del método de autenticación
                    Usuario corredor = usuariosService.getUsuario(1); // TEST
                    
                    // Comprueba si merece la pena registrar el paso:
                    // - ¿El usuario está corriendo un recorrido de la carrera?
                    // - Según la modalidad:
                    //     Línea: si no es el siguiente control
                    //     Score: si no ha registrado ya el control
                    Recorrido recorrido = carrera.getRecorridos().get(0); // TEST
                    // TODO
                    
                    // Guarda registro en BD
                    Registro registro = new Registro();
                    registro.setCorredor(corredor);
                    registro.setRecorrido(recorrido);
                    registro.setCodigoControl(control.getCodigo());
                    Registro resultado = registrosService.registraPasoControl(registro);
                    
                    if(resultado != null) {
                        // 200
                        return resultado;
                    } else {
                        // No se pudo crear el registro -> 500
                        throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el registro.");
                    }
                } else {
                    // Registro inválido -> 422 (Unprocessable Entity)
                    throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "El secreto no es correcto.");
                }
            } else {
                // No existe el control -> 422 (Unprocessable Entity)
                throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "El código de control no se corresponde con ningún control de la carrera.");
            }
        } else {
            // No existe la carrera -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la carrera.");
        }
    }

}
