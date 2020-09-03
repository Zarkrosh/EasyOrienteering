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
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
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
    private ICarreraService carrerasService;
    
    @Autowired
    private IUsuarioService usuariosService;
    
    @Autowired
    private IParticipacionService participacionesService;
    
    @GetMapping("/participadas")
    public List<CarreraSimplificada> getCarrerasParticipadasUsuario() {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        List<Carrera> participadas = carrerasService.getCarrerasParticipadasUsuario(usuario);
        List<CarreraSimplificada> simplificadas = new ArrayList<>();
        for(Carrera c : participadas) simplificadas.add(new CarreraSimplificada(c));
        return simplificadas;
    }
    
    @GetMapping("/organizadas")
    public List<CarreraSimplificada> getCarrerasOrganizadasUsuario() {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        List<Carrera> organizadas = carrerasService.getCarrerasOrganizadasUsuario(usuario);
        List<CarreraSimplificada> simplificadas = new ArrayList<>();
        for(Carrera c : organizadas) simplificadas.add(new CarreraSimplificada(c));
        return simplificadas;
    }
    
    @GetMapping("/buscar")
    public List<CarreraSimplificada> buscaCarreras(
            @Nullable @RequestParam("nombre") String nombre, 
            @Nullable @RequestParam("tipo") String tipo, 
            @Nullable @RequestParam("modalidad") String modalidad,
            Pageable pageable) {
        if(nombre == null) nombre = "";
        if(tipo == null) tipo = "";
        if(modalidad == null) modalidad = "";
        long idUsuario = usuariosService.getUsuarioPeticion().getId();
        return carrerasService.buscaCarreras(idUsuario, nombre, tipo, modalidad, pageable);
    }
    
    @GetMapping("/{idCarrera}")
    public Carrera getCarrera(@PathVariable long idCarrera) {
        // TODO Si es privada solo pueden verlas los participantes y el organizador
        Carrera res = carrerasService.getCarrera(idCarrera);
        if(res != null) {
            if(res.isPrivada()) {
                // ¿Puede ver la carrera?
                boolean permiso = false;
                Usuario usuario = usuariosService.getUsuarioPeticion();
                if(Objects.equals(usuario.getId(), res.getOrganizador().getId())) {
                    // Es el organizador, puede acceder
                    permiso = true;
                } else if(participacionesService.haParticipadoEnCarrera(usuario, res)) {
                    // Participante, puede acceder
                    permiso = true;
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
    public void getMapasCarrera(@PathVariable long idCarrera, HttpServletResponse response) {
        Carrera carrera = getCarrera(idCarrera);
        Usuario usuario = usuariosService.getUsuarioPeticion();
        if(Objects.equals(usuario.getId(), carrera.getId())) {
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
            // No tiene permiso -> 403
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo puede descargar los mapas el organizador.");
        } 
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Carrera nuevaCarrera(@RequestBody Carrera carrera) {
        // El creador de la carrera es el usuario que realiza la petición
        //Usuario org = usuariosService.getUsuarioPeticion();
        Usuario org = usuariosService.getUsuario(2L); // Usuario de prueba de creación
        carrera.setOrganizador(org);
        
        // Crea la carrera
        return carrerasService.saveCarrera(carrera);
    }
    
    @PutMapping("/{id}")
    public void editarCarrera(@RequestBody Carrera carrera, @PathVariable long id) {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        Carrera c = getCarrera(id);
        //if(Objects.equals(usuario.getId(), c.getOrganizador().getId())) {
        if(true) { // DEBUG
            carrerasService.editCarrera(c, carrera);
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo el organizador puede editar la carrera");
        }
    }
    
    @DeleteMapping("/{id}")
    public void borrarCarrera(@PathVariable long id) {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        Carrera c = getCarrera(id);
        if(Objects.equals(usuario.getId(), c.getOrganizador().getId())) {
            carrerasService.deleteCarrera(id);
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo el organizador puede borrar la carrera.");
        }
    }
    
    @GetMapping("/{id}/qr")
    public Map<String, String> getControlesQRCarrera(@PathVariable long id) {
        Map<String, String> res;
        
        Usuario usuario = usuariosService.getUsuarioPeticion();
        Carrera carrera = getCarrera(id);
        // Solo es accesible por el organizador de la carrera
        //if(Objects.equals(usuario.getId(), carrera.getOrganizador().getId())) {
        if(true) { // DEBUG
            res = carrerasService.getControlesConSecretosCarrera(carrera);
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo puede acceder el organizador de la carrera");
        }
        
        return res;
    }
    
}
