package com.hergomsoft.easyoapi.controllers;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.requests.RegistroRequest;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.responses.PendienteResponse;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.AbandonoResponse;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.RegistroService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import com.hergomsoft.easyoapi.utils.MessageException;
import javax.servlet.http.HttpServletResponse;
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
    
    @GetMapping("/pendiente")
    public PendienteResponse getDatosCarreraPendiente(HttpServletResponse response) {
        PendienteResponse res = null;
        
        Usuario corredor = usuariosService.getUsuario(3); // TEST ANDROID
        Recorrido recorrido = registrosService.getRecorridoPendiente(corredor);
        if(recorrido != null) {
            // Tiene un recorrido pendiente de acabar
            Registro[] registros = registrosService.getRegistrosUsuarioRecorrido(corredor, recorrido);
            res = new PendienteResponse(recorrido.getCarrera(), recorrido.getId(), registros);
        } else {
            // No tiene recorrido pendiente: 204 (No content)
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        
        return res;
    }
    
    @PostMapping("/abandonar/{idRecorrido}")
    public AbandonoResponse abandonarRecorrido(@PathVariable long idRecorrido) {
        AbandonoResponse res = new AbandonoResponse(false, "");
        
        Usuario corredor = usuariosService.getUsuario(3); // TEST ANDROID
        Recorrido recorrido = registrosService.getRecorridoPendiente(corredor);
        if(recorrido != null) {
            // Tiene un recorrido pendiente de acabar
            if(recorrido.getId().equals(idRecorrido)) {
                // Petición de abandono válida
                try {
                    registrosService.abandonaRecorrido(corredor, recorrido);
                    res.setAbandonado(true);
                } catch(MessageException e) {
                    res.setError(e.getMessage());
                }
            } else {
                // Petición de abandono inválida
                res.setError("Este no es el recorrido que tienes pendiente");
            }
        } else {
            // No tiene ningún recorrido pendiente de acabar
            res.setError("No tienes ningún recorrido pendiente por acabar");
        }
        
        return res;
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
    public Registro registrarPasoControl(@PathVariable long id, @Valid @RequestBody RegistroRequest peticion) {
        Registro resultado = null;
        Carrera carrera = carrerasService.getCarrera(id);
        if(carrera != null) {
            Control control = carrera.getControles().get(peticion.getCodigo());
            if(control != null) {
                // Obtiene el usuario corredor
                // TODO: extraer del método de autenticación
                Usuario corredor = usuariosService.getUsuario(peticion.getIdCorredor()); // TEST
                Recorrido recorrido = registrosService.getRecorridoPendiente(corredor);
                if(recorrido == null) {
                    // El usuario no tiene ningún recorrido pendiente
                    // Comprueba que el control es una salida
                    if(control.getTipo() != Control.TIPO.SALIDA) {
                        // Control inválido
                        lanzaError422(RegistroRequest.ERROR_ESCANEA_SALIDA);
                    }
                    
                    // Comprueba que se indica recorrido
                    if(peticion.getIdRecorrido() != null) {
                        // Comprueba que el control pertenece al recorrido indicado
                        recorrido = carrera.getRecorridoPorID(peticion.getIdRecorrido());
                        if(recorrido == null) {
                            // El recorrido no pertenece a esta carrera
                            lanzaError422(RegistroRequest.ERROR_RECORRIDO_AJENO);
                        } else {
                            // Comprueba que no ha comenzado dicho recorrido ya 
                            if(registrosService.haCorridoRecorrido(corredor, recorrido)) {
                                // Ya ha empezado el recorrido
                                lanzaError422(RegistroRequest.ERROR_YA_CORRIDO);
                            }
                        }
                    } else {
                        // No se indica recorrido a empezar
                        lanzaError422(RegistroRequest.ERROR_SIN_RECORRIDO);
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
                                        lanzaError422(RegistroRequest.ERROR_CONTROL_EQUIVOCADO);
                                    }
                                } else {
                                    // Ya ha finalizado el recorrido
                                    // Esto no debería ocurrir debido a que no estaría pendiente el recorrido
                                    lanzaError422(RegistroRequest.ERROR_YA_ACABADO);
                                }
                            } else {
                                // SCORE
                                // Comprueba si no ha registrado ya el control
                                if(registrosService.haRegistradoControl(corredor, control, recorrido)) {
                                    // Ya ha registrado este control
                                    lanzaError422(RegistroRequest.ERROR_YA_REGISTRADO);
                                }
                            }
                        } else {
                            // El secreto no se corresponde con el control
                            lanzaError422(RegistroRequest.ERROR_SECRETO);
                            throw new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY, "El secreto no es correcto");
                        }
                    } else {
                        // El usuario no está corriendo esta carrera
                        lanzaError422(RegistroRequest.ERROR_OTRA_CARRERA);
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
                // No existe el control
                lanzaError422(RegistroRequest.ERROR_NO_EXISTE_CONTROL);
            }
        } else {
            // No existe la carrera -> 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la carrera");
        }
        
        return resultado;
    }
    
    /**
     * Lanza el código de error 422 (Unprocessable Entity) con el mensaje especificado (ver constantes).
     * @param mensaje Mensaje de error
     */
    private void lanzaError422(String mensaje) {
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, mensaje);
    }

}
