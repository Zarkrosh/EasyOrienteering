package com.hergomsoft.easyoapi.controllers;


import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.CarreraSimplificada;
import com.hergomsoft.easyoapi.services.ICarreraService;
import com.hergomsoft.easyoapi.services.IParticipacionService;
import com.hergomsoft.easyoapi.services.IUsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/carreras")
public class CarrerasController {
    
    @Autowired
    private ICarreraService carreraService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private IParticipacionService participacionService;
    
    @GetMapping("/participadas")
    public List<CarreraSimplificada> getCarrerasParticipadasUsuario(Authentication authentication) {
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        List<Carrera> participadas = carreraService.getCarrerasParticipadasUsuario(usuario);
        List<CarreraSimplificada> simplificadas = new ArrayList<>();
        for(Carrera c : participadas) simplificadas.add(new CarreraSimplificada(c));
        return simplificadas;
    }
    
    @GetMapping("/organizadas")
    public List<CarreraSimplificada> getCarrerasOrganizadasUsuario(Authentication authentication) {
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        List<Carrera> organizadas = carreraService.getCarrerasOrganizadasUsuario(usuario);
        List<CarreraSimplificada> simplificadas = new ArrayList<>();
        for(Carrera c : organizadas) simplificadas.add(new CarreraSimplificada(c));
        return simplificadas;
    }
    
    @GetMapping("/buscar")
    public List<CarreraSimplificada> buscaCarreras(Authentication authentication,
            @Nullable @RequestParam("nombre") String nombre, 
            @Nullable @RequestParam("tipo") String tipo, 
            @Nullable @RequestParam("modalidad") String modalidad,
            Pageable pageable) {
        if(nombre == null) nombre = "";
        if(tipo == null) tipo = "";
        if(modalidad == null) modalidad = "";
        long idUsuario = -1;
        try {
            Usuario usuario = null;usuarioService.getUsuarioPeticion(authentication);
            if(usuario != null) idUsuario = usuario.getId();
        } catch(ResponseStatusException e) {
            // No quiero que se tome en cuenta la excepción
        }
        return carreraService.buscaCarreras(idUsuario, nombre, tipo, modalidad, pageable);
    }
    
    @GetMapping("/{idCarrera}")
    public Carrera getCarrera(Authentication authentication,
            @PathVariable long idCarrera) {
        // TODO Si es privada solo pueden verlas los participantes y el organizador
        Carrera res = carreraService.getCarrera(idCarrera);
        if(res != null) {
            if(res.isPrivada()) {
                // ¿Puede ver la carrera?
                boolean permiso = false;
                Usuario usuario = null;
                try {
                    usuario = usuarioService.getUsuarioPeticion(authentication);
                } catch(ResponseStatusException e) {
                    // No quiero que se tome en cuenta la excepción aquí
                }
                
                if(usuario != null) {
                    if(Objects.equals(usuario.getId(), res.getOrganizador().getId())) {
                        // Es el organizador, puede acceder
                        permiso = true;
                    } else if(participacionService.haParticipadoEnCarrera(usuario, res)) {
                        // Participante, puede acceder
                        permiso = true;
                    }
                }
                
                if(!permiso) {
                    // No tiene permiso -> 403
                    throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Carrera privada: solo pueden ver la carrera los participantes y el organizador.");
                }
            }
        } else {
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe ninguna carrera con ese ID");
        }
        
        return res;
    }
    
    @GetMapping("/{idCarrera}/mapas")
    public void getMapasCarrera(Authentication authentication,
            @PathVariable long idCarrera, HttpServletResponse response) {
        
        Carrera carrera = getCarrera(authentication, idCarrera);
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        if(Objects.equals(usuario.getId(), carrera.getOrganizador().getId())) {
            // Comprueba si tiene mapas
            boolean hayMapas = false;
            int i = 0;
            while(!hayMapas && i < carrera.getRecorridos().size()) {
                byte[] mapa = carrera.getRecorridos().get(i).getMapa();
                hayMapas = (mapa != null && mapa.length > 0);
                i++;
            }
            
            if(hayMapas) {
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=mapas.zip");

                try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
                    for(Recorrido r : carrera.getRecorridos()) {
                        ZipEntry e = new ZipEntry(r.getNombre() + ".jpg");
                        byte[] mapa = r.getMapa();
                        e.setSize(mapa.length);
                        e.setTime(System.currentTimeMillis());
                        zippedOut.putNextEntry(e);
                        StreamUtils.copy(mapa, zippedOut);
                        zippedOut.closeEntry();
                    }

                    zippedOut.finish();
                } catch (Exception e) {
                    // Error inesperado
                    throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Se ha producido un error al obtener los mapas.");
                }  
            } else {
                // No hay mapas
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No hay mapas");
            }
        } else {
            // No tiene permiso -> 403
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo puede descargar los mapas el organizador.");
        } 
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Carrera creaCarrera(Authentication authentication, @RequestBody Carrera carrera) {
        // El creador de la carrera es el usuario que realiza la petición
        Usuario org = usuarioService.getUsuarioPeticion(authentication);
        //Usuario org = usuarioService.getUsuario(2L); // Usuario de prueba de creación
        carrera.setOrganizador(org);
        
        // Crea la carrera
        return carreraService.saveCarrera(carrera);
    }
    
    @PutMapping("/{id}")
    public void editaCarrera(Authentication authentication,
            @RequestBody Carrera carrera, @PathVariable long id) {
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        Carrera c = getCarrera(authentication, id);
        //if(Objects.equals(usuario.getId(), c.getOrganizador().getId())) {
        if(true) { // DEBUG
            carreraService.editCarrera(c, carrera);
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo el organizador puede editar la carrera");
        }
    }
    
    @DeleteMapping("/{id}")
    public void borraCarrera(Authentication authentication,
            @PathVariable long id) {
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        Carrera c = getCarrera(authentication, id);
        if(Objects.equals(usuario.getId(), c.getOrganizador().getId())) {
            carreraService.deleteCarrera(id);
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo el organizador puede borrar la carrera.");
        }
    }
    
    @GetMapping("/{id}/qr")
    public Map<String, String> getControlesQRCarrera(Authentication authentication,
            @PathVariable long id) {
        Map<String, String> res;
        
        Usuario usuario = usuarioService.getUsuarioPeticion(authentication);
        Carrera carrera = getCarrera(authentication, id);
        // Solo es accesible por el organizador de la carrera
        //if(Objects.equals(usuario.getId(), carrera.getOrganizador().getId())) {
        if(true) { // DEBUG
            res = carreraService.getControlesConSecretosCarrera(carrera);
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo puede acceder el organizador de la carrera");
        }
        
        return res;
    }
    
}
