package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.responses.CarreraSimplificada;
import com.hergomsoft.easyoapi.repository.CarreraRepository;
import com.hergomsoft.easyoapi.repository.RecorridoRepository;
import com.hergomsoft.easyoapi.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarreraService implements ICarreraService {

    @Autowired
    private CarreraRepository repoCarrera;
    @Autowired
    private RecorridoRepository repoRecorrido;
    
    @Value("${easyo.carreras.secretgeneral}")
    private String secretCarreras; // Secreto guardado en el servidor

    @Override
    public List<Carrera> findAll() {
        return (List<Carrera>) repoCarrera.findAll();
    }
    
    @Override
    public List<CarreraSimplificada> buscaCarreras(long idUsuario, String nombre, String tipo, String modalidad, Pageable pageable) {
        Page<Carrera> carreras = repoCarrera.buscaCarreras(idUsuario, nombre.toUpperCase(), tipo.toUpperCase(), modalidad.toUpperCase(), pageable);
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
            return null;
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
            return guardaCarrera(carrera, true);
        } else {
            throw new IllegalArgumentException("La carrera a guardar no puede ser null");
        }
    }
    
    @Override
    public void editDatosCarrera(Carrera carrera) {
        guardaCarrera(carrera, false);
    }

    @Override
    public void editCarrera(Carrera carrera) {
        if(carrera != null && carrera.getId() != null) {
            Carrera prev = getCarrera(carrera.getId());
            if(prev != null) {
                // Comprueba datos válidos
                // TODO
                
                // Borra la carrera anterior y sus datos asociados
                repoCarrera.deleteById(carrera.getId());
                // TODO: analizar optimización de destrucción de los recorridos
                // Guarda la carrera actualizada
                guardaCarrera(carrera, true);
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
    public boolean checkSecretoControl(String secreto, Control control) {
        return secreto.contentEquals(getSecretoControl(control));
    }
    
    @Override
    public Map<String, String> getSecretosCarrera(Carrera carrera) {
        Map<String, String> res = new HashMap<>();
        
        // Itera por los controles calculando su secreto
        for(Control control : carrera.getControles().values()) {
            String secreto = getSecretoControl(control);
            res.put(control.getCodigo(), secreto);
        }
        
        return res;
    }
    
    private Carrera guardaCarrera(Carrera carrera, boolean deepEdit) {
        // Primero guarda la carrera general
        Carrera res = repoCarrera.save(carrera);
        // Luego guarda los controles que no son guardados debido al problema
        // asociado al uso de la clave foránea del ID de la carrera.
        if(deepEdit) {
            for(Control c : carrera.getControles().values()) {
                repoCarrera.insertaControl(c.getCodigo(), res.getId(), c.getTipo().name());
            }
            res.setControles(carrera.getControles());
        }
        
        return res;
    }
    
    @Override
    public String getSecretoControl(Control control) {
        return Utils.md5(control.getCodigo() + control.getCarrera().getSecret());
    }
    
    @Override
    public List<Carrera> getCarrerasParticipadasUsuario(Usuario usuario) {
        return repoCarrera.getCarrerasCorridasUsuario(usuario.getId());
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
            return null;
        }
    }
    
}
