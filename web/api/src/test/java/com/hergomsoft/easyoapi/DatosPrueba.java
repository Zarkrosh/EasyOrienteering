package com.hergomsoft.easyoapi;

import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Coordenada;
import com.hergomsoft.easyoapi.models.Participacion;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Registro;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.services.CarreraService;
import com.hergomsoft.easyoapi.services.ParticipacionService;
import com.hergomsoft.easyoapi.services.UsuarioService;
import com.hergomsoft.easyoapi.utils.Constants;
import com.thedeanda.lorem.LoremIpsum;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
public class DatosPrueba {
    @Autowired
    UsuarioService usuariosService;
    @Autowired
    CarreraService carrerasService;
    @Autowired
    ParticipacionService participacionService;
    @Autowired
    PasswordEncoder encoder;
    
    final int NUMERO_USUARIOS = 100;
    final int NUMERO_CARRERAS_EVENTO = 50;
    final int NUMERO_CARRERAS_CIRCUITO = 50;
    final float PROBABILIDAD_CLUB = 0.7f;
    final int LARGO_PASSWORD = 12;
    final Date MIN_RANDOM_DATE = new Date(LocalDateTime.of(2020, 1, 1, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
    final Date MAX_RANDOM_DATE = new Date(LocalDateTime.of(2020, 7, 1, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
    final int MIN_CONTROLES = 10;
    final int MAX_CONTROLES = 40;
    final int MIN_CONTROLES_RECORRIDO = 10;
    final int MAX_CONTROLES_RECORRIDO = 24;
    final int MIN_CONTROLES_SCORE = 8;
    final int MAX_CONTROLES_SCORE = 16;
    final int MAX_RECORRIDOS = 6;
    final float PROBABILIDAD_EVENTO_PRIVADO = 0.3f;
    final float PROBABILIDAD_NOTAS = 0.7f;
    final float PROBABILIDAD_COORDENADAS_EVENTO = 0.8f;
    final int MIN_CORREDORES_RESULTADOS = 5;
    final int MAX_CORREDORES_RESULTADOS = 20;
    final int MIN_TIEMPO_PARCIAL = 30;
    final int MAX_TIEMPO_PARCIAL = 400;
    final float MAX_DESVIACION_PARCIAL = 0.5f;
    final float PROBABILIDAD_FULL_SCORE = 0.6f;
    final float MIN_PORCENTAJE_COMPLETITUD_SCORE = 0.6f;
    
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
        File fileMunicipios = new File("src/test/resources/municipios.txt");
        String[] nombres = readLines(fileNombres);
        String[] clubes = readLines(fileClubes);
        List<Municipio> municipios = generaMunicipios(readLines(fileMunicipios));
        
        aleatoriza(nombres);
        
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
            String pass = encoder.encode(username);
            Date fechaRegistro = new Date(ThreadLocalRandom.current()
                              .nextLong(MIN_RANDOM_DATE.getTime(), MAX_RANDOM_DATE.getTime()));
            Set<Usuario.RolUsuario> roles = new HashSet<>();
            roles.add(Usuario.RolUsuario.USUARIO);
            Usuario usuario = new Usuario(username, email, club, pass, fechaRegistro, roles);
            usuariosGenerados.add( usuariosService.saveUsuario(usuario) );
        }
        
        // CARRERAS
        List<Carrera> carrerasGeneradas = new ArrayList<>();
        Carrera.Modalidad[] modalidades = Carrera.Modalidad.values();
        // Eventos
        for(int i = 0; i < NUMERO_CARRERAS_EVENTO; i++) {
            Usuario organizador = getUsuarioAleatorio(usuariosGenerados);
            Carrera.Modalidad modalidad = modalidades[random.nextInt(modalidades.length)];
            Carrera carrera = getCarreraAleatoria(organizador, getMunicipioAleatorio(municipios), modalidad, Carrera.Tipo.EVENTO);
            carrerasGeneradas.add( carrerasService.saveCarrera(carrera) );
        }
        // Circuitos
        for(int i = 0; i < NUMERO_CARRERAS_CIRCUITO; i++) {
            Usuario organizador = getUsuarioAleatorio(usuariosGenerados);
            Carrera.Modalidad modalidad = modalidades[random.nextInt(modalidades.length)];
            Carrera carrera = getCarreraAleatoria(organizador, getMunicipioAleatorio(municipios), modalidad, Carrera.Tipo.CIRCUITO);
            carrerasGeneradas.add( carrerasService.saveCarrera(carrera) );
        }
        
        // RESULTADOS
        Date fechaSalida = new Date(LocalDateTime.of(2020, 8, 25, 9, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
        int cont = 1;
        for(Carrera carrera : carrerasGeneradas) {
            System.out.println("[*] " + cont++ + " / " + carrerasGeneradas.size());
            int numeroCorredores = getIntAleatorio(MIN_CORREDORES_RESULTADOS, MAX_CORREDORES_RESULTADOS);
            if(carrera.getModalidad() == Carrera.Modalidad.TRAZADO) {
                for(Recorrido recorrido : carrera.getRecorridos()) {
                    Collections.shuffle(usuariosGenerados);
                    Map<Usuario, Participacion> participaciones = new HashMap<>();
                    List<Usuario> corredores = new ArrayList<>();
                    for(int i = 0; i < numeroCorredores; i++) {
                        corredores.add(usuariosGenerados.get(i));
                        participaciones.put(usuariosGenerados.get(i), new Participacion(usuariosGenerados.get(i), recorrido));
                    }
                    Map<Usuario, Long> millisAcumulados = new HashMap<>();
                    
                    for(String codigoControl : recorrido.getTrazado()) {
                        int millisOptimo = getIntAleatorio(MIN_TIEMPO_PARCIAL, MAX_TIEMPO_PARCIAL) * 1000;
                        for(int i = 0; i < numeroCorredores; i++) {
                            float desviacion = millisOptimo * getFloatAleatorio(0, MAX_DESVIACION_PARCIAL);
                            long millisParcial = millisOptimo + (long) desviacion;
                            long millisAcum = millisAcumulados.getOrDefault(corredores.get(i), 0l) + millisParcial;
                            millisAcumulados.put(usuariosGenerados.get(i), millisAcum);
                            Date fechaRegistro = new Date(fechaSalida.getTime() + millisAcum);
                            Registro registro = new Registro(codigoControl, fechaRegistro);
                            // Actualiza la participación
                            participaciones.get(corredores.get(i)).getRegistros().add(registro);
                        }
                    }
                    
                    // Guarda las participaciones
                    for(Participacion p : participaciones.values()) {
                        p.setFechaInicio(p.getRegistros().get(0).getFecha());
                        p.setPendiente(false);
                        participacionService.guardaParticipacion(p);
                    }
                }
            } else {
                // SCORE
                List<Control> controles = new ArrayList<>(carrera.getControles().values());
                Recorrido recScore = carrera.getRecorridos().get(0);
                Collections.shuffle(usuariosGenerados);
                for(int i = 0; i < numeroCorredores; i++) {
                    Participacion participacion = new Participacion(usuariosGenerados.get(i), recScore);
                    Collections.shuffle(controles);
                    List<Control> registrados = new ArrayList<>();
                    if(getFloatAleatorio(0, 1) <= PROBABILIDAD_FULL_SCORE) {
                        // Registra todos los controles
                        for(Control c : controles) {
                            if(c.getTipo() == Control.Tipo.CONTROL) registrados.add(c);
                        }
                    } else {
                        // Registra algunos controles
                        int numeroRegistrados = getIntAleatorio((int)(controles.size() * MIN_PORCENTAJE_COMPLETITUD_SCORE), controles.size() - 1);
                        for(int j = 0; j < numeroRegistrados; j++) {
                            Control c = controles.get(j);
                            if(c.getTipo() == Control.Tipo.CONTROL) registrados.add(c);
                        }
                    }
                    
                    // Todos los usuarios registran la salida a la misma hora
                    Registro regSalida = new Registro(Constants.CODIGO_SALIDA, fechaSalida);
                    participacion.getRegistros().add(regSalida);
                    
                    // Registran con tiempos aleatorios sus controles
                    long millisAcum = 0;
                    Collections.shuffle(registrados);
                    for(Control c : registrados) {
                        millisAcum += getIntAleatorio(MIN_TIEMPO_PARCIAL, MAX_TIEMPO_PARCIAL) * 1000;
                        Date fechaRegistro = new Date(fechaSalida.getTime() + millisAcum);
                        Registro registro = new Registro(c.getCodigo(), fechaRegistro);
                        participacion.getRegistros().add(registro);
                    }
                    // Meta
                    participacion.getRegistros().add(new Registro(Constants.CODIGO_META, new Date(fechaSalida.getTime() + millisAcum + 20000))); // 20s sprint
                    participacion.setFechaInicio(participacion.getRegistros().get(0).getFecha());
                    participacion.setPendiente(false);
                    participacionService.guardaParticipacion(participacion);
                }
            }
        }
    }
    
    public Carrera getCarreraAleatoria(Usuario organizador, Municipio municipio, Carrera.Modalidad modalidad, Carrera.Tipo tipoCarrera) {
        String nombre = getNombreCarreraAleatorio(municipio.nombre, (tipoCarrera == Carrera.Tipo.CIRCUITO));
        String notas = "";
        if(random.nextFloat() <= PROBABILIDAD_NOTAS) {
            notas = LoremIpsum.getInstance().getParagraphs(1, 3);
        }
        Float latitud = municipio.latitud;
        Float longitud = municipio.longitud;
        boolean privada;
        if(tipoCarrera == Carrera.Tipo.CIRCUITO) {
            privada = false;
        } else {
            if(random.nextFloat() >= PROBABILIDAD_COORDENADAS_EVENTO) latitud = longitud = null;
            privada = (random.nextFloat() <= PROBABILIDAD_EVENTO_PRIVADO);
        }
        Date fecha = new Date(ThreadLocalRandom.current()
                .nextLong(MIN_RANDOM_DATE.getTime(), MAX_RANDOM_DATE.getTime()));
        
        // Controles
        Map<String, Control> controles = new HashMap<>();
        Coordenada coordsFake = null;
        int nControles;
        if(modalidad == Carrera.Modalidad.SCORE) nControles = getIntAleatorio(MIN_CONTROLES_SCORE, MAX_CONTROLES_SCORE);
        else nControles = getIntAleatorio(MIN_CONTROLES, MAX_CONTROLES);
        controles.put(Constants.CODIGO_SALIDA, new Control(Constants.CODIGO_SALIDA, Control.Tipo.SALIDA, 0, coordsFake));
        controles.put(Constants.CODIGO_META, new Control(Constants.CODIGO_META, Control.Tipo.META, 0, coordsFake));
        for(int j = 0; j < nControles; j++) {
            String codigo = Integer.toString(j + 31);
            int puntuacion = (modalidad == Carrera.Modalidad.SCORE) ? (j + 31) / 10 : 0;
            controles.put(codigo, new Control(codigo, Control.Tipo.CONTROL, puntuacion, coordsFake));
        }

        // Recorridos
        List<Recorrido> recorridos = new ArrayList<>();
        if(modalidad == Carrera.Modalidad.TRAZADO) {
            Set<String> setCodigosControles = new HashSet<>(controles.keySet());
            setCodigosControles.remove(Constants.CODIGO_SALIDA);
            setCodigosControles.remove(Constants.CODIGO_META);
            String[] codigosControles = new String[setCodigosControles.size()];
            setCodigosControles.toArray(codigosControles);

            int nRecorridos = random.nextInt(MAX_RECORRIDOS) + 1;
            for(int j = 0; j < nRecorridos; j++) {
                String nombreRecorrido = "R" + (j+1); // TODO otros nombres
                int nControlesRec = getIntAleatorio(MIN_CONTROLES_RECORRIDO, MAX_CONTROLES_RECORRIDO);
                nControlesRec = Math.min(nControlesRec, codigosControles.length);
                List<String> trazado = new ArrayList<>();
                trazado.add(Constants.CODIGO_SALIDA);
                aleatoriza(codigosControles);
                for(int k = 0; k < nControlesRec; k++) {
                    trazado.add(codigosControles[k]);
                }
                trazado.add(Constants.CODIGO_META);
                recorridos.add(new Recorrido(nombreRecorrido, trazado, null));
            }
        } else {
            // Las carreras SCORE solo tienen un recorrido
            recorridos.add(new Recorrido(Constants.RECORRIDO_SCORE, new ArrayList<>(), null));
        }

        Carrera carrera = new Carrera(nombre, tipoCarrera, modalidad, organizador, latitud, longitud, privada, recorridos, controles, notas);
        carrera.setFecha(fecha);
        return carrera;
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
    
    public Municipio getMunicipioAleatorio(List<Municipio> lista) {
        return lista.get( random.nextInt(lista.size()) );
    }
    
    public int getIntAleatorio(int min, int max) {
        return min + random.nextInt(max - min);
    }
    
    public float getFloatAleatorio(float min, float max) {
        float res = min + random.nextFloat() * (max - min);
        return res;
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
    
    private class Municipio {
        public String nombre;
        public float latitud;
        public float longitud;

        public Municipio(String nombre, float latitud, float longitud) {
            this.nombre = nombre;
            this.latitud = latitud;
            this.longitud = longitud;
        }
    }
    
    private List<Municipio> generaMunicipios(String[] lineas) {
        List<Municipio> municipios = new ArrayList<>();
        // nombremunicipio;latitud;longitud
        for(String linea : lineas) {
            String[] campos = linea.split(";");
            String nombre = new String(campos[0].getBytes(), StandardCharsets.UTF_8).split("\\(")[0];
            float latitud = Float.parseFloat(campos[1]);
            float longitud = Float.parseFloat(campos[2]);
            municipios.add(new Municipio(nombre, latitud, longitud));
        }
        
        return municipios;
    }
}
