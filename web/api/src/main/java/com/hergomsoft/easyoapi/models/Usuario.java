package com.hergomsoft.easyoapi.models;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(	name = "usuarios", 
    uniqueConstraints = { 
        @UniqueConstraint(columnNames = "nombre"),
        @UniqueConstraint(columnNames = "email") 
})
public class Usuario {
    public static final int MLEN_NOMBRE = 20;
    public static final int MLEN_EMAIL = 50;
   
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "NOMBRE", length = MLEN_NOMBRE, nullable = false)
    private String nombre;
    
    @Column(name = "EMAIL", length = MLEN_EMAIL, nullable = false)
    private String email;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private Date fechaRegistro;

    public Usuario() {}

    public Usuario(String nombre, String email, String password, Date fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
}
