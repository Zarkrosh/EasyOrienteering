package com.hergomsoft.easyoapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.validation.constraints.Email;

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
    
    @Column(name = "NOMBRE", length = MLEN_NOMBRE, nullable = false, unique = true)
    private String nombre;
    
    @Email
    @Column(name = "EMAIL", length = MLEN_EMAIL, nullable = false, unique = true)
    private String email;
    
    @JsonIgnore
    @Column(name = "PASSWORD")
    private String password;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private Date fechaRegistro;

    public Usuario() {}

    public Usuario(String nombre, String email, String password, Date fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
}
