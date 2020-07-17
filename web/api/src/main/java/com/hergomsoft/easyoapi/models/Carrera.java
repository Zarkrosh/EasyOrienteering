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
    public static final int MLEN_NOMBRE = 50;
    
    public enum TIPO {EVENTO, CIRCUITO};
    public enum MODALIDAD {LINEA, SCORE};
   
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
    private TIPO tipo;
    
    @Enumerated(EnumType.STRING)
    @Type(type = "com.hergomsoft.easyoapi.models.EnumTypesPostgres")
    @Column(name = "MODALIDAD")
    private MODALIDAD modalidad;
    
    @ManyToOne(optional = false)
    private Usuario organizador;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="CARRERA_ID", referencedColumnName="ID")
    private List<Recorrido> recorridos;
    
    // No se utiliza cascada porque da error debido a la clave foránea del ID de carrera
    // usado como clave primaria conjunta
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "carrera")
    @MapKey(name = "codigo")
    private Map<String, Control> controles;
    
    // PENDIENTES
    //private List<Resultado> resultados;

    public Carrera() {}

    public Carrera(String secret, String nombre, TIPO tipo, MODALIDAD modalidad, Usuario organizador, List<Recorrido> recorridos, Map<String, Control> controles) {
        this.secret = secret;
        this.nombre = nombre;
        this.tipo = tipo;
        this.modalidad = modalidad;
        this.organizador = organizador;
        this.recorridos = recorridos;
        this.controles = controles;
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

    public TIPO getTipo() {
        return tipo;
    }

    public void setTipo(TIPO tipo) {
        this.tipo = tipo;
    }

    public MODALIDAD getModalidad() {
        return modalidad;
    }

    public void setModalidad(MODALIDAD modalidad) {
        this.modalidad = modalidad;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
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

