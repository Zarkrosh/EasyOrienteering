package com.hergomsoft.easyoapi.controllers;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.requests.RegistroRequest;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.responses.PendienteResponse;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.AbandonoResponse;
import com.hergomsoft.easyoapi.models.responses.InicioResponse;
import com.hergomsoft.easyoapi.models.responses.ParticipacionesRecorridoResponse;
import com.hergomsoft.easyoapi.services.ICarreraService;
import com.hergomsoft.easyoapi.services.IParticipacionService;
import com.hergomsoft.easyoapi.services.IUsuarioService;
import com.hergomsoft.easyoapi.utils.MessageException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/recorridos")
public class RecorridosController {
    
    @Autowired
    private ICarreraService carrerasService;
    
    @Autowired
    private IParticipacionService participacionesService;
    
    @Autowired
    private IUsuarioService usuariosService;
    
    @GetMapping("/{idRecorrido}")
    public ParticipacionesRecorridoResponse getParticipacionesRecorrido(@PathVariable long idRecorrido) {
        Recorrido recorrido = carrerasService.getRecorrido(idRecorrido);
        if(recorrido != null) {
            return new ParticipacionesRecorridoResponse(recorrido, participacionesService.getParticipacionesRecorrido(recorrido));
        } else {
            // No existe el recorrido
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el recorrido");
        }
    }
      
    @PostMapping("/abandonar/{idRecorrido}")
    public AbandonoResponse abandonarRecorrido(@PathVariable long idRecorrido) {
        AbandonoResponse res = new AbandonoResponse(false, "");
        
        Usuario corredor = usuariosService.getUsuarioPeticion();
        Recorrido recorrido = participacionesService.getRecorridoPendiente(corredor);
        if(recorrido != null) {
            // Tiene un recorrido pendiente de acabar
            if(recorrido.getId().equals(idRecorrido)) {
                // Petición de abandono válida
                try {
                    participacionesService.abandonaRecorridoUsuario(corredor, recorrido);
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

    @PostMapping("/iniciar/{idRecorrido}")
    public InicioResponse iniciarRecorrido(@PathVariable long idRecorrido, @Valid @RequestBody RegistroRequest peticion) {
        Registro registro = null;
        Usuario corredor = usuariosService.getUsuarioPeticion();
        Recorrido recorrido = carrerasService.getRecorrido(idRecorrido);
        if(recorrido != null) {
            Carrera carrera = recorrido.getCarrera();
            if(participacionesService.getRecorridoPendiente(corredor) == null) {
                // El usuario no tiene ningún recorrido pendiente
                // Comprueba que no ha corrido dicho recorrido ya 
                if(!participacionesService.haParticipadoEnRecorrido(corredor, recorrido)) {
                    // Comprueba que no es el organizador de la carrera
                    if(Objects.equals(corredor.getId(), carrera.getOrganizador().getId())) {
                        // Comprueba que el control es una salida
                        Control control = carrera.getControles().get(peticion.getCodigo());
                        if(control != null) {
                            if(control.getTipo() == Control.Tipo.SALIDA) {
                                // Crea la participación en el recorrido
                                Participacion nParticipacion = new Participacion(corredor, recorrido);
                                registro = new Registro(control.getCodigo(), new Date());
                                nParticipacion.getRegistros().add(registro);

                                if(participacionesService.guardaParticipacion(nParticipacion) == null) {
                                    // No se pudo crear la participación -> 500
                                    throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al unirse al recorrido.");
                                }
                            } else {
                                // Control inválido
                                lanzaError422(RegistroRequest.ERROR_ESCANEA_SALIDA);
                            }
                        } else {
                            // No existe el control
                            lanzaError422(RegistroRequest.ERROR_NO_EXISTE_CONTROL);
                        }
                    } else {
                        // Es el organizador de la carrera
                        lanzaError422(RegistroRequest.ERROR_ES_ORGANIZADOR);
                    }
                } else {
                    // Ya ha participado en el recorrido
                    lanzaError422(RegistroRequest.ERROR_YA_CORRIDO);
                }
            } else {
                // El usuario tiene otro recorrido pendiente de acabar
                lanzaError422(RegistroRequest.ERROR_OTRO_RECORRIDO);
            }
        } else {
            // No existe el recorrido -> 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el recorrido.");
        }
        
        return new InicioResponse(registro, recorrido);
    }
    
    @PostMapping("/{idRecorrido}")
    public Registro registrarPasoControl(@PathVariable long idRecorrido, @Valid @RequestBody RegistroRequest peticion) {
        Registro resultado = null;
        Recorrido recorrido = carrerasService.getRecorrido(idRecorrido);
        if(recorrido != null) {
            Carrera carrera = recorrido.getCarrera();
            Control control = carrera.getControles().get(peticion.getCodigo());
            if(control != null) {
                // Obtiene el usuario corredor
                Usuario corredor = usuariosService.getUsuarioPeticion();
                Recorrido recorridoPendiente = participacionesService.getRecorridoPendiente(corredor);
                if(recorridoPendiente != null) {
                    // Corredor realizando recorrido
                    if(Objects.equals(recorrido.getId(), recorridoPendiente.getId())) {
                        // Está corriendo recorrido que tiene pendiente
                        if(carrerasService.checkSecretoControl(peticion.getSecreto(), control, carrera)) {
                            // Control válido, comprueba si el registro es válido
                            Participacion participacion = participacionesService.getParticipacionUsuarioRecorrido(corredor, recorrido);
                            if(participacion != null) {
                                if(carrera.getModalidad() == Carrera.Modalidad.TRAZADO) {
                                    // Comprueba si el control registrado es el siguiente que le toca
                                    String siguiente = recorrido.getTrazado().get(participacion.getRegistros().size());
                                    if(!siguiente.contentEquals(control.getCodigo())) {
                                        // Registro inválido
                                        lanzaError422(RegistroRequest.ERROR_CONTROL_EQUIVOCADO);
                                    }
                                } else {
                                    // SCORE
                                    // Comprueba si no ha registrado ya el control
                                    List<Registro> registros = participacion.getRegistros();
                                    boolean yaRegistrado = false;
                                    int i = 0;
                                    while(!yaRegistrado && i < registros.size()) {
                                        yaRegistrado = registros.get(i).getControl().contentEquals(control.getCodigo());
                                        i++;
                                    }

                                    if(yaRegistrado) {
                                        // Ya ha registrado este control
                                        lanzaError422(RegistroRequest.ERROR_YA_REGISTRADO);
                                    }
                                }

                                // Registro válido
                                Registro nRegistro = new Registro(control.getCodigo(), new Date());
                                participacion.getRegistros().add(nRegistro);
                                if(control.getTipo() == Control.Tipo.META) {
                                    // Ha finalizado el recorrido
                                    participacion.setPendiente(false);
                                }
                                
                                // Actualiza la participación
                                if(participacionesService.guardaParticipacion(participacion) != null) {
                                    resultado = nRegistro;
                                } else {
                                    // No se pudo crear el registro -> 500
                                    throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar paso por control");
                                }
                            } else {
                                // Error inesperado
                                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al registrar control");
                            }
                        } else {
                            // El secreto no se corresponde con el control
                            lanzaError422(RegistroRequest.ERROR_SECRETO);
                        }
                    } else {
                        // El usuario no está corriendo esta carrera
                        lanzaError422(RegistroRequest.ERROR_OTRO_RECORRIDO);
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
            // No existe el recorrido -> 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el recorrido");
        }
        
        return resultado;
    }
    
    @GetMapping("/mapa/{idRecorrido}")
    public void getMapaRecorrido(@PathVariable long idRecorrido, HttpServletResponse response) {
        Recorrido rec = carrerasService.getRecorrido(idRecorrido);
        if(rec != null) {
            // ¿Puede acceder al mapa?
            boolean permiso = false;
            Usuario usuario = usuariosService.getUsuarioPeticion();
            if(Objects.equals(usuario.getId(), rec.getCarrera().getOrganizador().getId())) {
                // Es el organizador, puede acceder
                permiso = true;
            } else if(participacionesService.haParticipadoEnRecorrido(usuario, rec)) {
                // Participante, puede acceder
                permiso = true;
            }
            
            if(permiso) {
                if(rec.getMapa() != null) {
                    try {
                        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                        IOUtils.copy(new ByteArrayInputStream(rec.getMapa()), response.getOutputStream());
                    } catch (IOException ex) {
                        Logger.getLogger(CarrerasController.class.getName()).log(Level.SEVERE, null, ex);
                        throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener el mapa");
                    }
                } else {
                    // No tiene mapa -> 404
                    throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El recorrido no tiene mapa");
                }
            } else {
                // No tiene permiso -> 403
                throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo pueden ver el mapa los participantes y el organizador.");
            }
        } else {
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe ningún recorrido con ese ID");
        }
        
    }
    
    @GetMapping("/pendiente")
    public PendienteResponse getDatosCarreraPendiente(HttpServletResponse response) {
        PendienteResponse res = null;
        
        Usuario corredor = usuariosService.getUsuarioPeticion();
        Recorrido recorrido = participacionesService.getRecorridoPendiente(corredor);
        if(recorrido != null) {
            // Tiene un recorrido pendiente de acabar
            Participacion participacion = participacionesService.getParticipacionUsuarioRecorrido(corredor, recorrido);
            res = new PendienteResponse(recorrido.getCarrera(), recorrido.getId(), participacion);
        } else {
            // No tiene recorrido pendiente: 204 (No content)
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        
        return res;
    }
    
    /**
     * Lanza el código de error 422 (Unprocessable Entity) con el mensaje especificado (ver constantes).
     * @param mensaje Mensaje de error
     */
    private void lanzaError422(String mensaje) throws ResponseStatusException {
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, mensaje);
    }

}
