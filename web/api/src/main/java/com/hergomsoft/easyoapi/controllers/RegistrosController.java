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
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
        Registro resultado = null;
        Carrera carrera = carrerasService.getCarrera(id);
        if(carrera != null) {
            Control control = carrera.getControles().get(peticion.getCodigo());
            if(control != null) {
                // Obtiene el usuario corredor
                // TODO: extraer del método de autenticación
                Usuario corredor = usuariosService.getUsuario(2); // TEST
                Recorrido recorrido = registrosService.getRecorridoPendiente(corredor);
                if(recorrido == null) {
                    // El usuario no tiene ningún recorrido pendiente
                    // Comprueba que el control es una salida
                    if(control.getTipo() != Control.TIPO.SALIDA) {
                        // Control inválido -> 422 (Unprocessable Entity)
                        throw new ResponseStatusException(
                            HttpStatus.UNPROCESSABLE_ENTITY, "Debe escanear una salida primero");
                    }
                    
                    // Comprueba que se indica recorrido
                    if(peticion.getIdRecorrido() != null) {
                       // Comprueba que el control pertenece al recorrido indicado
                       recorrido = carrera.getRecorridoPorID(peticion.getIdRecorrido());
                       if(recorrido == null) {
                           // El recorrido no pertenece a esa carrera -> 422 (Unprocessable Entity)
                            throw new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY, "El recorrido indicado no pertenece a la carrera");
                       } else {
                           // Comprueba que no ha comenzado dicho recorrido ya 
                           if(registrosService.haCorridoRecorrido(corredor, recorrido)) {
                               // Ya ha empezado el recorrido -> 422 (Unprocessable Entity)
                                throw new ResponseStatusException(
                                    HttpStatus.UNPROCESSABLE_ENTITY, "Ya ha corrido este recorrido");
                           }
                       }
                    } else {
                        // No se indica recorrido a empezar -> 422 (Unprocessable Entity)
                        throw new ResponseStatusException(
                            HttpStatus.UNPROCESSABLE_ENTITY, "Debe indicar recorrido");
                    }
                } else {
                    // Corredor realizando recorrido
                    if(carrera.getRecorridos().contains(recorrido)) {
                        // Está corriendo recorrido de la carrera
                        if(carrerasService.checkSecretoControl(peticion.getSecreto(), control)) {
                            // Control válido, comprueba si el registro es válido
                            if(carrera.getModalidad() == Carrera.MODALIDAD.LINEA) {
                                // Comprueba si el control registrado es el siguiente que le toca
                                String siguiente = registrosService.getCodigoSiguienteControlRecorrido(corredor, recorrido);
                                if(siguiente != null) {
                                    if(!siguiente.equals(control.getCodigo())) {
                                        // Registro inválido
                                        throw new ResponseStatusException(
                                            HttpStatus.UNPROCESSABLE_ENTITY, "Este no es el control que buscas");
                                    }
                                } else {
                                    // Ya ha finalizado el recorrido
                                    // Esto no debería ocurrir debido a que no estaría pendiente el recorrido
                                    throw new ResponseStatusException(
                                        HttpStatus.UNPROCESSABLE_ENTITY, "No quedan más controles por registrar");
                                }
                            } else {
                                // SCORE
                                // Comprueba si no ha registrado ya el control
                                if(registrosService.haRegistradoControl(corredor, control, recorrido)) {
                                    // Ya ha registrado este control -> 422
                                    throw new ResponseStatusException(
                                        HttpStatus.UNPROCESSABLE_ENTITY, "Control ya registrado");
                                }
                            }
                        } else {
                            // Control inválido -> 422 (Unprocessable Entity)
                            throw new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY, "El secreto no es correcto");
                        }
                    } else {
                        // El usuario no está corriendo esta carrera -> 422
                        throw new ResponseStatusException(
                            HttpStatus.UNPROCESSABLE_ENTITY, "El usuario no está corriendo esta carrera");
                    }
                }
                
                // Registro válido
                Registro registro = new Registro();
                registro.setCorredor(corredor);
                registro.setRecorrido(recorrido);
                registro.setControl(control);
                resultado = registrosService.registraPasoControl(registro);

                if(resultado == null) {
                    // No se pudo crear el registro -> 500
                    throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el registro");
                }
            } else {
                // No existe el control -> 422 (Unprocessable Entity)
                throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "El código de control no se corresponde con ningún control de la carrera");
            }
        } else {
            // No existe la carrera -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe la carrera");
        }
        
        return resultado;
    }

}
