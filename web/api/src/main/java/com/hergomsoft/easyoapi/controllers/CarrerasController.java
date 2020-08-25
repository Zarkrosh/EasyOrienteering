package com.hergomsoft.easyoapi.controllers;


import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.requests.UbicacionRequest;
import com.hergomsoft.easyoapi.models.responses.CarreraSimplificada;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private CarreraService carrerasService;
    
    @Autowired
    private UsuarioService usuariosService;
    
    @GetMapping("/participadas")
    public List<Carrera> getCarrerasParticipadasUsuario() {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        List<Carrera> participadas = carrerasService.getCarrerasParticipadasUsuario(usuario);
        return participadas;
    }
    
    @GetMapping("/organizadas")
    public List<Carrera> getCarrerasOrganizadasUsuario() {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        List<Carrera> organizadas = carrerasService.getCarrerasOrganizadasUsuario(usuario);
        return organizadas;
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
    
    @GetMapping("/{idCarrera}/mapas")
    public void getMapasCarrera(@PathVariable long idCarrera, HttpServletResponse response) {
        // TODO Restricción solo usuario organizador
        Carrera carrera = getCarrera(idCarrera);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=mapas.zip");

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for(Recorrido r : carrera.getRecorridos()) {
                ZipEntry e = new ZipEntry(r.getNombre() + ".jpg");
                byte[] mapa = r.getMapa();
                e.setSize(mapa.length);
                e.setTime(System.currentTimeMillis());
                zippedOut.putNextEntry(e);
                // And the content of the resource:
                StreamUtils.copy(mapa, zippedOut);
                zippedOut.closeEntry();
            }
            
            zippedOut.finish();
        } catch (Exception e) {
            // Do something with Exception
        }   
    }
    
    @GetMapping("/mapa/{idRecorrido}")
    public void getMapaRecorrido(@PathVariable long idRecorrido, HttpServletResponse response) {
        // TODO Restricciones de descarga (solo participantes, organizador...)
        Recorrido rec = carrerasService.getRecorrido(idRecorrido);
        if(rec != null) {
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
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe ningún recorrido con ese ID");
        }
        
    }
    
    @GetMapping("/{id}")
    public Carrera getCarrera(@PathVariable long id) {
        Carrera res = carrerasService.getCarrera(id);
        if(res == null) {
            // No existe -> 404
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe ninguna carrera con ese ID");
        } else {
            return res;
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
        if(Objects.equals(usuario.getId(), c.getOrganizador().getId())) {
            // TODO Comprobar datos válidos
            carrera.setId(id);
            carrerasService.editCarrera(carrera);
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Solo puede editar el organizador de la carrera");
        }
    }
    
    @DeleteMapping("/{id}")
    public void borrarCarrera(@PathVariable long id) {
        carrerasService.deleteCarrera(id);
    }
    
    @GetMapping("/{id}/qr")
    public Map<String, String> getControlesQRCarrera(@PathVariable long id) {
        Map<String, String> res = new TreeMap<>();
        
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
    
    @PutMapping("/{idCarrera}/ubicacion")
    public void setUbicacionCarrera(@PathVariable long idCarrera, @RequestBody UbicacionRequest ubicacion) {
        Usuario usuario = usuariosService.getUsuarioPeticion();
        Carrera carrera = getCarrera(idCarrera);
        
        if(Objects.equals(usuario.getId(), carrera.getOrganizador().getId())) {
            if(ubicacion.getLatitud() != null && ubicacion.getLongitud() != null) {
                carrera.setLatitud(ubicacion.getLatitud());
                carrera.setLongitud(ubicacion.getLongitud());
                carrerasService.editDatosCarrera(carrera);
            } else {
                // Mala petición
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No puede haber ningún campo nulo");
            }
        } else {
            // No es el organizador de la carrera
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "No eres el organizador de esta carrera");
        }
    }
    
}
