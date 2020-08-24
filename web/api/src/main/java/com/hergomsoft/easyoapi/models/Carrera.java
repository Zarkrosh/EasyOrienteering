package com.hergomsoft.easyoapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "carreras")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Carrera implements IdEntity {
    public static final int MAX_LEN_NOMBRE = 50;
    public static final int MIN_LEN_NOMBRE = 5;
    public static final int MAX_LEN_NOTAS = 1000;
    
    public enum Tipo {EVENTO, CIRCUITO};
    public enum Modalidad {TRAZADO, SCORE};
   
    @Id
    @Column(name = "ID", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @Column(name = "SECRET")
    private String secret;
    
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "com.hergomsoft.easyoapi.models.EnumTypesPostgres")
    @Column(name = "TIPO")
    private Tipo tipo;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "com.hergomsoft.easyoapi.models.EnumTypesPostgres")
    @Column(name = "MODALIDAD")
    private Modalidad modalidad;
    
    @ManyToOne(optional = false)
    private Usuario organizador;
    
    @Column(name = "PRIVADA")
    private boolean privada;
    
    @Column(name = "LATITUD")
    private Float latitud;
    
    @Column(name = "LONGITUD")
    private Float longitud;
    
    @Column(name = "NOTAS")
    private String notas;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="CARRERA_ID", referencedColumnName="ID")
    private List<Recorrido> recorridos;
    
    // No se utiliza cascada porque da error debido a la clave foránea del ID de carrera
    // usado como clave primaria conjunta
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "carrera")
    @MapKey(name = "codigo")
    private Map<String, Control> controles;

    public Carrera() {}

    public Carrera(String nombre, Tipo tipo, Modalidad modalidad, 
            Usuario organizador, Float latitud, Float longitud, boolean privada, 
            List<Recorrido> recorridos, Map<String, Control> controles, String notas) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.modalidad = modalidad;
        this.organizador = organizador;
        this.latitud = latitud;
        this.longitud = longitud;
        this.privada = privada;
        this.recorridos = recorridos;
        this.controles = controles;
        this.notas = notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
    }

    public Float getLatitud() {
        return latitud;
    }

    public void setLatitud(Float latitud) {
        this.latitud = latitud;
    }

    public Float getLongitud() {
        return longitud;
    }

    public void setLongitud(Float longitud) {
        this.longitud = longitud;
    }
    
    public boolean tieneUbicacion() {
        return latitud != null && longitud != null;
    }

    public boolean isPrivada() {
        return privada;
    }

    public void setPrivada(boolean privada) {
        this.privada = privada;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<Recorrido> getRecorridos() {
        return recorridos;
    }

    public void setRecorridos(List<Recorrido> recorridos) {
        this.recorridos = recorridos;
    }

    public Map<String, Control> getControles() {
        return controles;
    }

    public void setControles(Map<String, Control> controles) {
        this.controles = controles;
    }

    
    /**
     * Devuelve el recorrido de la carrera con el ID especificado. Devuelve null,
     * si no contiene ninguno con dicho ID.
     * @param id ID del recorrido
     * @return Recorrido o null
     */
    public Recorrido getRecorridoPorID(long id) {
        Recorrido res = null;
        
        int i = 0;
        while(res == null && i < recorridos.size()) {
            if(recorridos.get(i).getId() == id) res = recorridos.get(i);
            i++;
        }
        
        return res;
    }
    
}

