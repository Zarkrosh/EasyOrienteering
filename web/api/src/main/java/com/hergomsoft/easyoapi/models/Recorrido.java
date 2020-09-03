package com.hergomsoft.easyoapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hergomsoft.easyoapi.models.serializers.MapaDeserializer;
import com.hergomsoft.easyoapi.models.serializers.MapaSerializer;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonStringType.class),
})
@Entity
@Table(name = "recorridos")
public class Recorrido implements IdEntity, Serializable {
    public static final int MLEN_NOMBRE = 15;
    
    @Id
    @Column(name = "ID", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "NOMBRE", length = MLEN_NOMBRE, nullable = false)
    private String nombre;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Carrera carrera;
    
    @Type(type = "json")
    @Column(name = "TRAZADO", columnDefinition = "json")
    private List<String> trazado; // Salida - Controles intermedios* - Meta

    @JsonSerialize(using = MapaSerializer.class)
    @JsonDeserialize(using = MapaDeserializer.class)
    @Lob
    @Column(name = "MAPA")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] mapa;

    public Recorrido() {}

    public Recorrido(String nombre, List<String> trazado, byte[] mapa) {
        this.nombre = nombre;
        this.trazado = trazado;
        this.mapa = mapa;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public List<String> getTrazado() {
        return trazado;
    }

    public void setTrazado(List<String> trazado) {
        this.trazado = trazado;
    }

    public byte[] getMapa() {
        return mapa;
    }

    public void setMapa(byte[] mapa) {
        this.mapa = mapa;
    }    

}
