package com.hergomsoft.easyoapi.controllers;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.requests.RegistroRequest;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.responses.PendienteResponse;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.AbandonoResponse;
import com.hergomsoft.easyoapi.models.responses.InicioResponse;
import com.hergomsoft.easyoapi.models.responses.RegistrosRecorridoResponse;
import com.hergomsoft.easyoapi.models.responses.RegistrosUsuario;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.RegistroService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import com.hergomsoft.easyoapi.utils.MessageException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
        
        Usuario corredor = usuariosService.getUsuarioPeticion();
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
    
    @PostMapping("{idCarrera}/iniciar/{idRecorrido}")
    public InicioResponse iniciarRecorrido(@PathVariable long idCarrera, 
            @PathVariable long idRecorrido, @Valid @RequestBody RegistroRequest peticion) {
        Usuario corredor = usuariosService.getUsuarioPeticion();
        Carrera carrera = carrerasService.getCarrera(idCarrera);
        Registro registro = null;
        Recorrido recorrido = null;
        if(carrera != null) {
            recorrido = registrosService.getRecorridoPendiente(corredor);
            if(recorrido == null) {
                // El usuario no tiene ningún recorrido pendiente
                recorrido = carrera.getRecorridoPorID(idRecorrido);
                if(recorrido != null) {
                    // Comprueba que no ha corrido dicho recorrido ya 
                    if(!registrosService.haCorridoRecorrido(corredor, recorrido)) {
                        // Comprueba que el control es una salida
                        Control control = carrera.getControles().get(peticion.getCodigo());
                        if(control.getTipo() == Control.Tipo.SALIDA) {
                            // Comprueba que el control es la salida del recorrido
                            if(control.getCodigo().contentEquals(recorrido.getTrazado().get(0))) {
                                // Genera el registro
                                registro = registrosService.registraPasoControl(new Registro(corredor, control, recorrido));
                                if(registro == null) {
                                    // No se pudo crear el registro -> 500
                                    throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el registro");
                                }
                            } else {
                                // No es la salida de este recorrido
                                lanzaError422(RegistroRequest.ERROR_SALIDA_RECORRIDO);
                            }
                        } else {
                            // Control inválido
                            lanzaError422(RegistroRequest.ERROR_ESCANEA_SALIDA);
                        }
                    } else {
                        // Ya ha empezado el recorrido
                        lanzaError422(RegistroRequest.ERROR_YA_CORRIDO);
                    }
                } else {
                    // El recorrido no pertenece a esta carrera
                    lanzaError422(RegistroRequest.ERROR_RECORRIDO_AJENO);
                }
            } else {
                // El usuario ya está corriendo otra carrera
                lanzaError422(RegistroRequest.ERROR_OTRA_CARRERA);
            }   
        } else {
            // No existe la carrera -> 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la carrera");
        }
        
        return new InicioResponse(registro, carrera, recorrido);
    }
    
    @PostMapping("/abandonar/{idRecorrido}")
    public AbandonoResponse abandonarRecorrido(@PathVariable long idRecorrido) {
        AbandonoResponse res = new AbandonoResponse(false, "");
        
        Usuario corredor = usuariosService.getUsuarioPeticion();
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
    
    @GetMapping("/{idRecorrido}")
    public RegistrosRecorridoResponse getRegistrosRecorrido(@PathVariable long idRecorrido) {
        Recorrido recorrido = carrerasService.getRecorrido(idRecorrido);
        if(recorrido != null) {
            List<Registro> registros = registrosService.getRegistrosRecorrido(recorrido);
            List<RegistrosUsuario> registrosUsuarios = new ArrayList<>();
            Map<Usuario, List<Registro>> tempRegistros = new HashMap<>();
            // Agrupa los registros por usuario
            for(Registro r : registros) {
                Usuario u = r.getCorredor();
                List<Registro> reg = tempRegistros.putIfAbsent(u, new ArrayList<>());
                if(reg == null) reg = tempRegistros.get(u);
                reg.add(r);
            }
            for(Entry<Usuario, List<Registro>> e : tempRegistros.entrySet()) {
                registrosUsuarios.add(new RegistrosUsuario(e.getKey(), e.getValue()));
            }
            return new RegistrosRecorridoResponse(recorrido, registrosUsuarios);
        } else {
            // No existe el recorrido
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el recorrido");
        }
    }

    
    @PostMapping("/{idCarrera}")
    public Registro registrarPasoControl(@PathVariable long idCarrera, @Valid @RequestBody RegistroRequest peticion) {
        Registro resultado = null;
        Carrera carrera = carrerasService.getCarrera(idCarrera);
        if(carrera != null) {
            Control control = carrera.getControles().get(peticion.getCodigo());
            if(control != null) {
                // Obtiene el usuario corredor
                Usuario corredor = usuariosService.getUsuarioPeticion();
                Recorrido recorrido = registrosService.getRecorridoPendiente(corredor);
                if(recorrido != null) {
                    // Corredor realizando recorrido
                    if(carrera.getRecorridos().contains(recorrido)) {
                        // Está corriendo recorrido de la carrera
                        if(carrerasService.checkSecretoControl(peticion.getSecreto(), control)) {
                            // Control válido, comprueba si el registro es válido
                            if(carrera.getModalidad() == Carrera.Modalidad.TRAZADO) {
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
                            
                            // Registro válido
                            resultado = registrosService.registraPasoControl(new Registro(corredor, control, recorrido));
                            if(resultado == null) {
                                // No se pudo crear el registro -> 500
                                throw new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el registro");
                            }
                        } else {
                            // El secreto no se corresponde con el control
                            lanzaError422(RegistroRequest.ERROR_SECRETO);
                        }
                    } else {
                        // El usuario no está corriendo esta carrera
                        lanzaError422(RegistroRequest.ERROR_OTRA_CARRERA);
                    }
                } else {
                    // El corredor debe iniciar un recorrido antes
                    lanzaError422(RegistroRequest.ERROR_ESCANEA_SALIDA);
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
    private void lanzaError422(String mensaje) throws ResponseStatusException {
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, mensaje);
    }

}
