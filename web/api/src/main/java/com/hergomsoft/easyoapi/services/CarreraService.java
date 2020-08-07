package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.repository.CarreraRepository;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CarreraService implements ICarreraService {

    @Autowired
    private CarreraRepository repoCarrera;
    
    @Value("${easyo.carreras.secretgeneral}")
    private String secretCarreras; // Secreto guardado en el servidor

    @Override
    public List<Carrera> findAll() {
        return (List<Carrera>) repoCarrera.findAll();
    }
    
    @Override
    public Carrera getCarrera(long id) {
        try {
            Optional<Carrera> res = repoCarrera.findById(id);
            return (res.isPresent()) ? res.get() : null;
        } catch(Exception e) {
            System.out.println("[!] Error al obtener la carrera con id " + id);
            return null;
        }
    }

    @Override
    public Carrera saveCarrera(Carrera carrera) {
        if(carrera != null) {
            // Genera el secret para la carrera mediante el hash MD5 de la concatenación
            // del secret del servidor con una cadena aleatoria generada de su mismo largo.
            String rand = cadenaAleatoria(secretCarreras.length());
            String secret = md5(secretCarreras + rand);
            carrera.setSecret(secret);
            return guardaCarrera(carrera);
        } else {
            throw new IllegalArgumentException("La carrera a guardar no puede ser null");
        }
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
                guardaCarrera(carrera);
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
    
    private Carrera guardaCarrera(Carrera carrera) {
        // Primero guarda la carrera general
        Carrera res = repoCarrera.save(carrera);
        // Luego guarda los controles que no son guardados debido al problema
        // asociado al uso de la clave foránea del ID de la carrera.
        for(Control c : carrera.getControles().values()) {
            repoCarrera.insertaControl(c.getCodigo(), res.getId(), c.getTipo().name());
        }
        res.setControles(carrera.getControles());
        
        return res;
    }
    
    @Override
    public String getSecretoControl(Control control) {
        return md5(control.getCodigo() + control.getCarrera().getSecret());
    }
    
    @Override
    public List<Carrera> getCarrerasParticipadasUsuario(Usuario usuario) {
        return repoCarrera.getCarrerasCorridasUsuario(usuario.getId());
    }

    @Override
    public List<Carrera> getCarrerasOrganizadasUsuario(Usuario usuario) {
        return repoCarrera.findByOrganizadorId(usuario.getId());
    }
    
    
    
    /**
     * Genera una cadena aleatoria del tamaño especificado.
     * Creds: https://www.baeldung.com/java-random-string
     * @param n Tamaño de la cadena
     * @return Cadena aleatoria de  tamaño n
     */
    private String cadenaAleatoria(int n) {
        int leftLimit = 48; // '0'
        int rightLimit = 122; // 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .limit(n)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();

        return generatedString;
    }
    
    /**
     * Devuelve el hash MD5 de la cadena especificada.
     * Creds: https://www.baeldung.com/java-md5
     * @param cadena Cadena a computar
     * @return MD5 de la cadena
     */
    private String md5(String cadena) {
        if(cadena != null) {
            String res = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(cadena.getBytes());
                byte[] digest = md.digest();
                res = DatatypeConverter.printHexBinary(digest).toUpperCase();
            } catch (NoSuchAlgorithmException e) {}

            return res;
        } else {
            throw new IllegalArgumentException("Error al obtener el MD5. La cadena no puede ser null.");
        }
    }
    
}
