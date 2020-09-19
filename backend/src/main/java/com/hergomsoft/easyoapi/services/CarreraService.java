package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.CarreraSimplificada;
import com.hergomsoft.easyoapi.repositories.CarreraRepository;
import com.hergomsoft.easyoapi.utils.Constants;
import com.hergomsoft.easyoapi.utils.Utils;
import com.hergomsoft.easyoapi.utils.Validators;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
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
    private CarreraRepository carreraRepository;
    
    @Autowired
    private IRecorridoService recorridoService;
    
    @Autowired
    private IParticipacionService participacionService;
    
    @Value("#{environment.EASYO_CARRERAS_SECRETGENERAL}")
    private String secretCarreras; // Secreto guardado en el servidor

    @Override
    public List<Carrera> findAll() {
        return (List<Carrera>) carreraRepository.findAll();
    }
    
    @Override
    public List<CarreraSimplificada> buscaCarreras(long idUsuario, String nombre, String tipo, String modalidad, Pageable pageable) {
        int offset = pageable.getPageSize() * pageable.getPageNumber();
        int size = pageable.getPageSize();
        List<Carrera> carreras = carreraRepository.buscaCarreras(idUsuario, nombre.toUpperCase(), tipo.toUpperCase(), modalidad.toUpperCase(), offset, size);
        List<CarreraSimplificada> simplificadas = new ArrayList<>();
        for(Carrera c : carreras) simplificadas.add(new CarreraSimplificada(c));
        return simplificadas;
    }
    
    @Override
    public Carrera getCarrera(long id) {
        try {
            Optional<Carrera> res = carreraRepository.findById(id);
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
            checkDatosCarrera(carrera, false);

            // Guarda la carrera general
            Carrera res = carreraRepository.save(carrera);
            // Luego guarda los recorridos que no son guardados en cascada
            List<Recorrido> gRecorridos = new ArrayList<>();
            for(Recorrido r : carrera.getRecorridos()) {
                r.setCarrera(res);
                gRecorridos.add(recorridoService.guardaRecorrido(r));
            }
            res.setRecorridos(gRecorridos);
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
                
                // Restricciones de edición
                // No puede cambiar la modalidad
                if(anterior.getModalidad() != nueva.getModalidad())
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se puede cambiar la modalidad de la carrera.");    
                // No se pueden cambiar los controles y recorridos
                nueva.setControles(anterior.getControles());
                nueva.setRecorridos(anterior.getRecorridos());
                
                // Comprueba datos válidos
                checkDatosCarrera(nueva, true);
                carreraRepository.save(nueva);
                
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
    public boolean deleteCarrera(long id) {
        if(carreraRepository.existsById(id)) {
            carreraRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean checkSecretoControl(String secreto, Control control, Carrera carrera) {
        return secreto.contentEquals(getSecretoControl(control, carrera));
    }
    
    @Override
    public boolean checkSecretoRecorrido(String secreto, Recorrido recorrido) {
        return secreto.contentEquals(getSecretoRecorrido(recorrido));
    }
    
    @Override
    public Map<String, String> getControlesConSecretosCarrera(Carrera carrera) {
        Map<String, String> res = new TreeMap<>();
        
        // Itera los controles (excepto salida)
        Control salida = null;
        for(Control control : carrera.getControles().values()) {
            if(control.getTipo() != Control.Tipo.SALIDA) {
                // Resto : <CODIGO-CONTROL>|<SECRETO-CONTROL>
                String secreto = getSecretoControl(control, carrera);
                res.put(control.getCodigo(), String.format(Constants.FORMATO_QR_CONTROL, control.getCodigo(), secreto));
            } else {
                salida = control;
            }
        }
        
        if(salida == null) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar los controles.");
        // TODO Tomar medidas correctivas
        
        // Itera los recorridos
        for(Recorrido recorrido : carrera.getRecorridos()) {
            // Salida: <CODIGO-SALIDA>|<ID-RECORRIDO>|<SECRETO-CONTROL> 
            String secreto = getSecretoRecorrido(recorrido);
            res.put(recorrido.getNombre(), String.format(Constants.FORMATO_QR_SALIDA, salida.getCodigo(), recorrido.getId(), secreto));
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
        List<Participacion> participaciones = participacionService.getParticipacionesUsuario(usuario);
        Set<Carrera> set = new HashSet<>();
        for(Participacion p : participaciones) set.add(p.getRecorrido().getCarrera());
        return new ArrayList<>(set);
    }

    @Override
    public List<Carrera> getCarrerasOrganizadasUsuario(Usuario usuario) {
        return carreraRepository.findByOrganizadorId(usuario.getId());
    }
    
    /**
     * Realiza una comprobación y corrección de los datos de la carrera.
     * @param carrera Carrera a comprobar
     * @param edit Los datos son una edición de la carrera
     */
    private void checkDatosCarrera(Carrera carrera, boolean edit) {
        String nombre = carrera.getNombre().trim();
        if(nombre.length() >= Carrera.MIN_LEN_NOMBRE && nombre.length() <= Carrera.MAX_LEN_NOMBRE) {
            carrera.setNombre(nombre);
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("La longitud del nombre debe comprender entre %d y %d caracteres.", Carrera.MIN_LEN_NOMBRE, Carrera.MAX_LEN_NOMBRE));
        }
        String notas = (carrera.getNotas() != null) ? carrera.getNotas().trim() : "";
        if(notas.length() > Carrera.MAX_LEN_NOTAS) notas = notas.substring(0, Carrera.MAX_LEN_NOTAS);
        carrera.setNotas(notas);
        
        // Fecha 
        Date fecha = carrera.getFecha();
        if(!edit) {
            // Al crear una carrera la fecha no puede ser anterior a la de hoy ni posterior a la de hoy dentro de un año
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int dia = calendar.get(Calendar.DAY_OF_MONTH);
            int mes = calendar.get(Calendar.MONTH) + 1;
            int ano = calendar.get(Calendar.YEAR);
            Date minFecha = new Date(LocalDateTime.of(ano, mes, dia, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
            Date maxFecha = new Date(LocalDateTime.of(ano + 1, mes, dia, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
            if(fecha != null && (fecha.before(minFecha) || fecha.after(maxFecha)))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La fecha debe ser como mínimo hoy y como máximo el año que viene.");
        }
        
        
        // Controles
        if(carrera.getControles() == null || carrera.getControles().isEmpty()) 
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No hay controles.");
        if(carrera.getControles().get(Constants.CODIGO_SALIDA) == null || carrera.getControles().get(Constants.CODIGO_META) == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Debe haber por lo menos una salida y una meta.");    
        
        int nSalidas = 0;
        int nMetas = 0;
        for(Control c : carrera.getControles().values()) {
            if(c.getTipo() == Control.Tipo.SALIDA) nSalidas++;
            else if(c.getTipo() == Control.Tipo.META) nMetas++;
            
            // Código
            String codigo = c.getCodigo().trim();
            if(codigo.isEmpty())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No puede haber códigos de control vacíos.");
            if(!Validators.checkCaracteresCodigo(codigo))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Los códigos de control solo pueden ser alfanuméricos.");
            if(c.getTipo() != Control.Tipo.SALIDA && codigo.contentEquals(Constants.CODIGO_SALIDA)) 
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("No se puede utilizar el código \"%s\" en controles que no son de salida.", Constants.CODIGO_SALIDA));
            if(c.getTipo() != Control.Tipo.META && codigo.contentEquals(Constants.CODIGO_META)) 
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, String.format("No se puede utilizar el código \"%s\" en controles que no son de meta.", Constants.CODIGO_META));
            
            c.setCodigo(codigo);
        }
        if(nSalidas != 1) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Debe existir una única salida.");
        if(nMetas != 1) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Debe existir una única meta.");
        
        
        // Comprobaciones relativas al tipo
        if(carrera.getTipo() == Carrera.Tipo.EVENTO) {
            // EVENTO
            // Fecha obligatoria
            if(fecha == null)
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Se debe indicar la fecha del evento.");
        } else {
            // CIRCUITO
            // Ubicacion obligatoria
            if(carrera.getLatitud() == null || carrera.getLongitud() == null)
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Un circuito debe tener una ubicación obligatoria.");
            // Solo puede ser público
            if(carrera.isPrivada()) 
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Un circuito no puede ser privado.");
        }
        
        // Comprobaciones relativas a la modalidad
        if(carrera.getModalidad() == Carrera.Modalidad.TRAZADO) {
            // TRAZADO
            if(carrera.getRecorridos() == null || carrera.getRecorridos().isEmpty())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Debe haber por lo menos un recorrido.");      
            // Los controles no tienen puntuación
            for(Control c : carrera.getControles().values()) c.setPuntuacion(0);
        } else {
            // SCORE
        }
    }
    
}
