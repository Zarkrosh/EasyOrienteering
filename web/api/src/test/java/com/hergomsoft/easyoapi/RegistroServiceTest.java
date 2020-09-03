package com.hergomsoft.easyoapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hergomsoft.easyoapi.models.Carrera;
import com.hergomsoft.easyoapi.models.Control;
import com.hergomsoft.easyoapi.models.Recorrido;
import com.hergomsoft.easyoapi.models.Usuario;
import com.hergomsoft.easyoapi.models.requests.RegistroRequest;
import com.hergomsoft.easyoapi.services.CarreraService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.matchers.JUnitMatchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/db_gen_datos.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RegistroServiceTest {
    
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    CarreraService carrerasService;
 
    @BeforeEach
    public void setup() throws Exception {
        
    }
    
    @Test
    void carrera() throws Exception {
        Carrera carrera = carrerasService.getCarrera(1);
        for(Recorrido r : carrera.getRecorridos()) {
            System.out.println(r.getNombre());
            System.out.println(r.getTrazado());
        }
    }
    
    @Test
    void nuevoRecorrido() throws Exception {
        Usuario usuario = new Usuario("asdf", "asdf", "asfd", "asdf", new Date());
        List<Recorrido> recorridos = new ArrayList<>();
        List<String> trazado = new ArrayList<>();
        trazado.add("SALIDA");
        trazado.add("31");
        trazado.add("32");
        trazado.add("META");
        recorridos.add(new Recorrido("R1", trazado, null));
        Map<String, Control> controles = new HashMap<>();
        controles.put("SALIDA", new Control("SALIDA", Control.Tipo.SALIDA, 0, null));
        controles.put("31", new Control("31", Control.Tipo.SALIDA, 0, null));
        controles.put("32", new Control("32", Control.Tipo.SALIDA, 0, null));
        controles.put("META", new Control("META", Control.Tipo.SALIDA, 0, null));
        Carrera carrera = new Carrera("Prueba", Carrera.Tipo.EVENTO, Carrera.Modalidad.TRAZADO, usuario, null, null, false, recorridos, controles, "asdf");
        carrera.setFecha(new Date());
        this.mvc.perform(post("/api/carreras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carrera)))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }
    
    /*
    @Test
    void registroCarreraNoExistente() throws Exception {
        RegistroRequest registro = new RegistroRequest("S1", "ignorado", 1L, 1L); // Ignorado todo
        this.mvc.perform(post("/api/registros/1337")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }
    
    @Test
    void registroControlNoExistente() throws Exception {
        RegistroRequest registro = new RegistroRequest("99", "ignorado", 1L, 1L);
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(status().reason(containsString(RegistroRequest.ERROR_NO_EXISTE_CONTROL))); 
    }
    
    @Test
    void registroSalidaSinRecorrido() throws Exception {
        RegistroRequest registro = new RegistroRequest("S1", "ignorado", null, 1L);
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(status().reason(containsString(RegistroRequest.ERROR_SIN_RECORRIDO))); 
    }
    
    @Test
    void registroControlNoSalida() throws Exception {
        RegistroRequest registro = new RegistroRequest("31", "ignorado", 1L, 1L);
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(status().reason(containsString(RegistroRequest.ERROR_ESCANEA_SALIDA))); 
    }
    
    @Test
    void registroSalidaRecorridoAjeno() throws Exception {
        RegistroRequest registro = new RegistroRequest("S1", "ignorado", 999L, 1L);
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(status().reason(containsString(RegistroRequest.ERROR_RECORRIDO_AJENO))); 
    }
    
    @Test
    void registroControlEquivocado() throws Exception {
        Carrera carrera = carrerasService.getCarrera(1);
        Map<String, String> secretos = carrerasService.getControlesConSecretosCarrera(carrera);
        RegistroRequest registroSalida = new RegistroRequest("S1", secretos.get("S1"), 1L, 1L);
        RegistroRequest registroErroneo = new RegistroRequest("32", secretos.get("32"), 1L, 1L);
        
        // Registra la salida correctamente
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroSalida)));
        
        // El siguiente control sería el 31, pero intenta registrar el 32
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroErroneo)))
                .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(status().reason(containsString(RegistroRequest.ERROR_CONTROL_EQUIVOCADO))); 
    }
    
    @Test
    void registroSalidaYaIniciado() throws Exception {
        Carrera carrera = carrerasService.getCarrera(1);
        Map<String, String> secretos = carrerasService.getControlesConSecretosCarrera(carrera);
        RegistroRequest registroSalida = new RegistroRequest("S1", secretos.get("S1"), 1L, 1L);
        RegistroRequest registro31 = new RegistroRequest("31", secretos.get("31"), 1L, 1L);
        RegistroRequest registro33 = new RegistroRequest("33", secretos.get("33"), 1L, 1L);
        RegistroRequest registroMeta = new RegistroRequest("M1", secretos.get("M1"), 1L, 1L);
        
        // Realiza la carrera
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroSalida)));
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro31)));
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro33)));
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroMeta)));
        
        // Intenta volver a registrar la salida
        this.mvc.perform(post("/api/registros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroSalida)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(status().reason(containsString(RegistroRequest.ERROR_YA_CORRIDO))); 
    }
*/
    
}
