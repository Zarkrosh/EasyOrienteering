package com.hergomsoft.easyoapi.models;

import com.hergomsoft.easyoapi.models.serializers.IdEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Table(	name = "usuarios", 
    uniqueConstraints = { 
        @UniqueConstraint(columnNames = "nombre"),
        @UniqueConstraint(columnNames = "email") 
})
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
})
public class Usuario implements IdEntity {
    public static final int MAXLEN_NOMBRE = 30;
    public static final int MINLEN_NOMBRE = 3;
    public static final int MAXLEN_EMAIL = 100;
    public static final int MAXLEN_CLUB = 30;
    public static final int MINLEN_CLUB = 0;
    public static final int MAXLEN_PASS = 64;
    public static final int MINLEN_PASS = 8;
    
    public enum RolUsuario { USUARIO, ADMIN }
    
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "NOMBRE", length = MAXLEN_NOMBRE, nullable = false, unique = true)
    private String nombre;
    
    @JsonIgnore
    @Email
    @Column(name = "EMAIL", length = MAXLEN_EMAIL, nullable = false, unique = true)
    private String email;
    
    @Column(name = "CLUB", length = MAXLEN_CLUB, nullable = true)
    private String club;
    
    @JsonIgnore
    @Column(name = "PASSWORD", length = MAXLEN_PASS, nullable = false)
    private String password;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private Date fechaRegistro;
    
    @Type(type = "jsonb")
    @Column(name = "ROLES", columnDefinition = "json")
    private Set<RolUsuario> roles = new HashSet<>();

    public Usuario() {}

    public Usuario(String nombre, String email, String club, String password, Date fechaRegistro, Set<RolUsuario> roles) {
        this.nombre = nombre;
        this.email = email;
        this.club = club;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
        this.roles = roles;
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

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
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

    public Set<RolUsuario> getRoles() {
        return roles;
    }

    public void setRoles(Set<RolUsuario> roles) {
        this.roles = roles;
    }
    
}
