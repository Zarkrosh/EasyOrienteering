package com.hergomsoft.easyoapi;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Coordenadas;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.RegistroService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import com.hergomsoft.easyoapi.utils.Utils;
import com.thedeanda.lorem.LoremIpsum;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
public class DatosPrueba {
    @Autowired
    UsuarioService usuariosService;
    @Autowired
    CarreraService carrerasService;
    @Autowired
    RegistroService registrosService;
    
    final int NUMERO_USUARIOS = 300;
    final int NUMERO_CARRERAS_EVENTO = 50;
    final int NUMERO_CARRERAS_CIRCUITO = 50;
    final float PROBABILIDAD_CLUB = 0.7f;
    final int LARGO_PASSWORD = 12;
    final Date MIN_RANDOM_DATE = new Date(LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
    final Date MAX_RANDOM_DATE = new Date();
    final int MIN_CONTROLES = 15;
    final int MAX_CONTROLES = 50;
    final int MIN_CONTROLES_RECORRIDO = 8;
    final int MAX_CONTROLES_RECORRIDO = 30;
    final int MAX_RECORRIDOS = 10;
    final float MIN_LATITUD = 38.2f;
    final float MAX_LATITUD = 43.3f;
    final float MIN_LONGITUD = -8.5f;
    final float MAX_LONGITUD = -0.5f;
    final float PROBABILIDAD_EVENTO_PRIVADO = 0.3f;
    final float PROBABILIDAD_NOTAS = 0.7f;
    final float PROBABILIDAD_COORDENADAS_EVENTO = 0.8f;
    final int MIN_CORREDORES_RESULTADOS = 5;
    final int MAX_CORREDORES_RESULTADOS = 50;
    final int MIN_TIEMPO_PARCIAL = 60;
    final int MAX_TIEMPO_PARCIAL = 1000;
    final float MAX_DESVIACION_PARCIAL = 0.5f;
    
    final String[] prefijosEventos = {
        "1ª Prueba Popular", "1ª Carrera Popular", "Entrenamiento",
        "Carrera de orientación", "Prueba de orientación"
    };
    final String[] prefijosCircuitos = {
        "CPO", "Circuito Permanente", "Circuito"
    };
    
    Random random = new Random();
    
    @Test
    @Sql(scripts = {"/db_gen.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void generaDatosPrueba() throws Exception {
        File fileNombres = new File("src/test/resources/nombres.txt");
        File fileClubes = new File("src/test/resources/clubes.txt");
        File filePoblaciones = new File("src/test/resources/poblaciones.txt");
        String[] nombres = readLines(fileNombres);
        String[] clubes = readLines(fileClubes);
        String[] poblaciones = readLines(filePoblaciones);
        
        aleatoriza(nombres);
        aleatoriza(poblaciones);
        
        // USUARIOS
        List<Usuario> usuariosGenerados = new ArrayList<>();
        for(int i = 0; i < NUMERO_USUARIOS; i++) {
            String nombre = nombres[i % nombres.length];
            String username = nombre + getTerminacionNombreAleatoria();
            String email = String.format("%s@test.com", username.toLowerCase());
            String club = "";
            if(random.nextFloat() <= PROBABILIDAD_CLUB) {
                club = getStringAleatoria(clubes);
            }
            String pass = Utils.sha256(Utils.cadenaAleatoria(LARGO_PASSWORD));
            Date fechaRegistro = new Date(ThreadLocalRandom.current()
                              .nextLong(MIN_RANDOM_DATE.getTime(), MAX_RANDOM_DATE.getTime()));
            Usuario usuario = new Usuario(username, email, club, pass, fechaRegistro);
            usuariosGenerados.add( usuariosService.saveUsuario(usuario) );
        }
        
        // CARRERAS
        List<Carrera> carrerasGeneradas = new ArrayList<>();
        Carrera.Modalidad[] modalidades = Carrera.Modalidad.values();
        // Eventos
        for(int i = 0; i < NUMERO_CARRERAS_EVENTO; i++) {
            Usuario organizador = getUsuarioAleatorio(usuariosGenerados);
            Carrera.Modalidad modalidad = modalidades[random.nextInt(modalidades.length)];
            Carrera carrera = getCarreraAleatoria(organizador, getStringAleatoria(poblaciones), modalidad, Carrera.Tipo.EVENTO);
            carrerasGeneradas.add( carrerasService.saveCarrera(carrera) );
        }
        // Circuitos
        for(int i = 0; i < NUMERO_CARRERAS_CIRCUITO; i++) {
            Usuario organizador = getUsuarioAleatorio(usuariosGenerados);
            Carrera.Modalidad modalidad = modalidades[random.nextInt(modalidades.length)];
            Carrera carrera = getCarreraAleatoria(organizador, getStringAleatoria(poblaciones), modalidad, Carrera.Tipo.CIRCUITO);
            carrerasGeneradas.add( carrerasService.saveCarrera(carrera) );
        }
    }
    
    public Carrera getCarreraAleatoria(Usuario organizador, String poblacion, Carrera.Modalidad modalidad, Carrera.Tipo tipoCarrera) {
        String nombre = getNombreCarreraAleatorio(poblacion, (tipoCarrera == Carrera.Tipo.CIRCUITO));
        String notas = "";
        if(random.nextFloat() <= PROBABILIDAD_NOTAS) {
            notas = LoremIpsum.getInstance().getParagraphs(1, 3);
        }
        Float latitud = getFloatAleatorio(MIN_LATITUD, MAX_LATITUD);
        Float longitud = getFloatAleatorio(MIN_LONGITUD, MAX_LONGITUD);
        boolean privada;
        if(tipoCarrera == Carrera.Tipo.CIRCUITO) {
            privada = false;
        } else {
            if(random.nextFloat() >= PROBABILIDAD_COORDENADAS_EVENTO) latitud = longitud = null;
            privada = (random.nextFloat() <= PROBABILIDAD_EVENTO_PRIVADO);
        }
        
        // Controles
        Map<String, Control> controles = new HashMap<>();
        Coordenadas coordsFake = new Coordenadas(0,0); // TODO Corregir fallo cuando es null
        int nControles = getIntAleatorio(MIN_CONTROLES, MAX_CONTROLES);
        controles.put("S1", new Control("S1", Control.Tipo.SALIDA, 0, coordsFake));
        controles.put("M1", new Control("M1", Control.Tipo.META, 0, coordsFake));
        for(int j = 0; j < nControles; j++) {
            String codigo = "" + j + 31;
            int puntuacion = (modalidad == Carrera.Modalidad.SCORE) ? (j + 31) / 10 : 0;
            controles.put(codigo, new Control(codigo, Control.Tipo.CONTROL, puntuacion, coordsFake));
        }

        // Recorridos
        List<Recorrido> recorridos = new ArrayList<>();
        if(modalidad == Carrera.Modalidad.TRAZADO) {
            Set<String> setCodigosControles = controles.keySet();
            setCodigosControles.remove("S1");
            setCodigosControles.remove("M1");
            String[] codigosControles = new String[setCodigosControles.size()];
            setCodigosControles.toArray(codigosControles);

            int nRecorridos = random.nextInt(MAX_RECORRIDOS) + 1;
            for(int j = 0; j < nRecorridos; j++) {
                String nombreRecorrido = "R" + (j+1); // TODO otros nombres
                int nControlesRec = getIntAleatorio(MIN_CONTROLES_RECORRIDO, MAX_CONTROLES_RECORRIDO);
                nControlesRec = Math.min(nControlesRec, codigosControles.length);
                List<String> trazado = new ArrayList<>();
                trazado.add("S1");
                aleatoriza(codigosControles);
                for(int k = 0; k < nControlesRec; k++) {
                    trazado.add(codigosControles[k]);
                }
                trazado.add("M1");
                recorridos.add(new Recorrido(nombreRecorrido, trazado, null));
            }
        }

        return new Carrera(nombre, tipoCarrera, modalidad, organizador, latitud, longitud, privada, recorridos, controles, notas);
    }
    
    public String getNombreCarreraAleatorio(String poblacion, boolean esCircuito) {
        String res;
        
        if(esCircuito) res = getStringAleatoria(prefijosCircuitos);
        else res = getStringAleatoria(prefijosEventos);
        
        res += " " + poblacion;
        
        return res;
    }
    
    public Usuario getUsuarioAleatorio(List<Usuario> lista) {
        return lista.get( random.nextInt(lista.size()) );
    }
    
    public int getIntAleatorio(int min, int max) {
        return min + random.nextInt(max - min);
    }
    
    public float getFloatAleatorio(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    public String getStringAleatoria(String[] array) {
        int rnd = random.nextInt(array.length);
        return array[rnd];
    }
    
    public String getTerminacionNombreAleatoria() {
        String res = "";
        
        if(random.nextFloat() <= 0.5) {
            res += "_";
        }
        
        // Números
        int n = random.nextInt(4) + 1;
        for(int i = 0; i < n; i++) {
            res += random.nextInt(10);
        }
        
        return res;
    }
    
    private void aleatoriza(String[] array) {
        List<String> list = Arrays.asList(array);
        Collections.shuffle(list);
        list.toArray(array);
    }
    
    private String[] readLines(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines.toArray(new String[lines.size()]);
    }
}
