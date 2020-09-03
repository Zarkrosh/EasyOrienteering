package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.CarreraSimplificada;
import com.hergomsoft.easyoapi.repository.CarreraRepository;
import com.hergomsoft.easyoapi.repository.RecorridoRepository;
import com.hergomsoft.easyoapi.utils.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarreraService implements ICarreraService {

    @Autowired
    private CarreraRepository repoCarrera;
    @Autowired
    private RecorridoRepository repoRecorrido;
    
    
    @Autowired
    private IParticipacionService participacionesService;
    
    @Value("${easyo.carreras.secretgeneral}")
    private String secretCarreras; // Secreto guardado en el servidor

    @Override
    public List<Carrera> findAll() {
        return (List<Carrera>) repoCarrera.findAll();
    }
    
    @Override
    public List<CarreraSimplificada> buscaCarreras(long idUsuario, String nombre, String tipo, String modalidad, Pageable pageable) {
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        int size = pageable.getPageSize();
        List<Carrera> carreras = repoCarrera.buscaCarreras(idUsuario, nombre.toUpperCase(), tipo.toUpperCase(), modalidad.toUpperCase(), offset, size);
        List<CarreraSimplificada> simplificadas = new ArrayList<>();
        for(Carrera c : carreras) simplificadas.add(new CarreraSimplificada(c));
        return simplificadas;
    }
    
    @Override
    public Carrera getCarrera(long id) {
        try {
            Optional<Carrera> res = repoCarrera.findById(id);
            return (res.isPresent()) ? res.get() : null;
        } catch(Exception e) {
            System.out.println("[!] Error al obtener la carrera con id " + id);
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener la carrera");
        }
    }

    @Override
    public Carrera saveCarrera(Carrera carrera) {
        if(carrera != null) {
            // Genera el secret para la carrera mediante el hash MD5 de la concatenación
            // del secret del servidor con una cadena aleatoria generada de su mismo largo.
            String rand = Utils.cadenaAleatoria(secretCarreras.length());
            String secret = Utils.md5(secretCarreras + rand);
            carrera.setSecret(secret);
            
            // Comprueba que los datos de la carrera son válidos
            checkDatosCarrera(carrera);

            // TODO Evita guardar mapas en edición si no se han modificado (si son nulls).

            // Guarda la carrera general
            Carrera res = repoCarrera.save(carrera);
            // Luego guarda los recorridos y controles que no son guardados en cascada
            List<Recorrido> gRecorridos = new ArrayList<>();
            for(Recorrido r : carrera.getRecorridos()) {
                r.setCarrera(res);
                gRecorridos.add(repoRecorrido.save(r));
            }
            res.setRecorridos(gRecorridos);
            /*
            Map<String, Control> gControles = new HashMap<>();
            for(Control c : carrera.getControles().values()) {
                gControles.put(c.getCodigo(), repoControl.save(c));
            }
            res.setControles(gControles);
            */
            return res;
        } else {
            throw new IllegalArgumentException("La carrera a guardar no puede ser null");
        }
    }

    @Override
    public void editCarrera(Carrera anterior, Carrera nueva) {
        if(nueva != null && nueva.getId() != null) {
            if(anterior != null) {
                nueva.setId(anterior.getId());
                nueva.setSecret(anterior.getSecret());
                // Comprueba datos válidos
                checkDatosCarrera(nueva);
                repoCarrera.save(nueva);
                
                // ¿Cambios en mapas?
                //    Vacío para indicar que no se ha cambiado
                //    Nulo para indicar que se ha borrado
                for(Recorrido rec : nueva.getRecorridos()) {
                    if(rec.getId() != null) {
                        if(rec.getMapa() == null) {
                            // Mapa borrado
                            // TODO
                        } else {
                            // TODO
                        }
                    }
                }
                
                /*
                NOTAS:
                    - Solo se actualizan los mapas de los recorridos si es necesario.
                    - No se actualizan los controles.
                */
            } else {
                // No existe ninguna carrera con ese ID
            }
        } else {
            throw new IllegalArgumentException("La carrera a guardar no puede ser null");
        }
    }
    
    @Override
    public void deleteCarrera(Carrera carrera) {
        repoCarrera.delete(carrera);
    }
    
    @Override
    public boolean deleteCarrera(long id) {
        if(existeCarrera(id)) {
            repoCarrera.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean existeCarrera(long id) {
        return repoCarrera.existsById(id);
    }
    
    @Override
    public boolean checkSecretoControl(String secreto, Control control, Carrera carrera) {
        return secreto.contentEquals(getSecretoControl(control, carrera));
    }
    
    @Override
    public Map<String, String> getControlesConSecretosCarrera(Carrera carrera) {
        Map<String, String> res = new TreeMap<>();
        
        // Itera los recorridos
        for(Recorrido recorrido : carrera.getRecorridos()) {
            // Salida: INICIO|<ID-RECORRIDO>|<SECRETO-CONTROL> 
            String secreto = getSecretoRecorrido(recorrido);
            res.put(recorrido.getNombre(), String.format("INICIO|%d|%s", recorrido.getId(), secreto));
        }
        
        // Itera los controles
        for(Control control : carrera.getControles().values()) {
            if(control.getTipo() != Control.Tipo.SALIDA) {
                // Resto : <CODIGO-CONTROL>|<SECRETO-CONTROL>
                String secreto = getSecretoControl(control, carrera);
                res.put(control.getCodigo(), String.format("%s|%s", control.getCodigo(), secreto));
            }
        }
        
        return res;
    }
    
    @Override
    public String getSecretoRecorrido(Recorrido recorrido) {
        return Utils.md5(recorrido.getNombre()+ recorrido.getCarrera().getSecret());
    }
    
    @Override
    public String getSecretoControl(Control control, Carrera carrera) {
        return Utils.md5(control.getCodigo() + carrera.getSecret());
    }
    
    @Override
    public List<Carrera> getCarrerasParticipadasUsuario(Usuario usuario) {
        List<Participacion> participaciones = participacionesService.getParticipacionesUsuario(usuario);
        Set<Carrera> set = new HashSet<>();
        for(Participacion p : participaciones) set.add(p.getRecorrido().getCarrera());
        return new ArrayList<>(set);
    }

    @Override
    public List<Carrera> getCarrerasOrganizadasUsuario(Usuario usuario) {
        return repoCarrera.findByOrganizadorId(usuario.getId());
    }
    
    @Override
    public Recorrido getRecorrido(long id) {
        try {
            Optional<Recorrido> res = repoRecorrido.findById(id);
            return (res.isPresent()) ? res.get() : null;
        } catch(Exception e) {
            System.out.println("[!] Error al obtener el recorrido con id " + id);
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener el recorrido");
        }
    }
    
    /**
     * Realiza una comprobación y corrección de los datos de la carrera.
     * @param carrera Carrera a comprobar
     */
    private void checkDatosCarrera(Carrera carrera) {
        // Trimming
        for(Control c : carrera.getControles().values()) c.setCodigo(c.getCodigo().trim());
        String nombre = carrera.getNombre().trim();
        if(nombre.length() >= Carrera.MIN_LEN_NOMBRE && nombre.length() <= Carrera.MAX_LEN_NOMBRE) {
            carrera.setNombre(nombre);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("La longitud del nombre debe comprender entre %d y %d caracteres.", Carrera.MIN_LEN_NOMBRE, Carrera.MAX_LEN_NOMBRE));
        }
        String notas = (carrera.getNotas() != null) ? carrera.getNotas().trim() : "";
        if(notas.length() > Carrera.MAX_LEN_NOTAS) notas = notas.substring(0, Carrera.MAX_LEN_NOTAS);
        carrera.setNotas(notas);
        
        if(carrera.getControles() == null || carrera.getControles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay controles.");
        }
        if(carrera.getControles().get(Carrera.CODIGO_SALIDA) == null ||
                carrera.getControles().get(Carrera.CODIGO_META) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe haber por lo menos una salida y una meta.");    
        }
        
        
        // Comprobaciones relativas al tipo
        if(carrera.getTipo() == Carrera.Tipo.EVENTO) {
            // EVENTO
            if(carrera.getFecha() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se debe indicar la fecha del evento.");
            } 
        } else {
            // CIRCUITO
            // Ubicacion obligatoria
        }
        
        // Comprobaciones relativas a la modalidad
        if(carrera.getModalidad() == Carrera.Modalidad.TRAZADO) {
            // TRAZADO
            if(carrera.getRecorridos() == null || carrera.getRecorridos().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe haber por lo menos un recorrido.");
            }
            
            
            // Los controles no tienen puntuación
            for(Control c : carrera.getControles().values()) c.setPuntuacion(0);
        } else {
            // SCORE
        }
    }
    
}
