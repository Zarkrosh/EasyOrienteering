package com.hergomsoft.easyoapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hergomsoft.easyoapi.models.serializers.ControlSoloCodigoSerializer;
import com.hergomsoft.easyoapi.models.serializers.IdEntityToIdSerializer;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "registros")
public class Registro implements IdEntity {
    
    @JsonIgnore
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonSerialize(using = IdEntityToIdSerializer.class)
    @ManyToOne(optional = false)
    private Usuario corredor;
    
    @JsonSerialize(using = ControlSoloCodigoSerializer.class)
    @ManyToOne(optional = false)
    private Control control;
    
    @JsonSerialize(using = IdEntityToIdSerializer.class)
    @ManyToOne(optional = false)
    private Recorrido recorrido;
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA", insertable = false)
    private Date fecha;

    public Registro() {
    }

    public Registro(Usuario corredor, Control control, Recorrido recorrido, Date fecha) {
        this.corredor = corredor;
        this.control = control;
        this.recorrido = recorrido;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getCorredor() {
        return corredor;
    }

    public void setCorredor(Usuario corredor) {
        this.corredor = corredor;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
}
