package com.hergomsoft.easyoapi.models;

import com.hergomsoft.easyoapi.models.serializers.IdEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonStringType.class),
})
@Entity
@Table( name = "participaciones")
public class Participacion implements IdEntity {

    @JsonIgnore
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_INICIO")
    private Date fechaInicio;
    
    @Type(type = "json")
    @Column(name = "REGISTROS", columnDefinition = "json")
    private List<Registro> registros;
    
    @Column(name = "ABANDONADO")
    private boolean abandonado;
    
    @Column(name = "PENDIENTE")
    private boolean pendiente;
    
    @ManyToOne(optional = false)
    private Usuario corredor;
    
    @JsonIgnore
    @ManyToOne(optional = false)
    private Recorrido recorrido;

    public Participacion() {}
    
    public Participacion(Usuario corredor, Recorrido recorrido) {
        this.corredor = corredor;
        this.recorrido = recorrido;
        this.registros = new ArrayList<>();
        this.abandonado = false;
        this.pendiente = true;
        
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public void setRegistros(List<Registro> registros) {
        this.registros = registros;
    }

    public boolean isAbandonado() {
        return abandonado;
    }

    public void setAbandonado(boolean abandonado) {
        this.abandonado = abandonado;
    }

    public boolean isPendiente() {
        return pendiente;
    }

    public void setPendiente(boolean pendiente) {
        this.pendiente = pendiente;
    }

    public Usuario getCorredor() {
        return corredor;
    }

    public void setCorredor(Usuario corredor) {
        this.corredor = corredor;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }
    
}
