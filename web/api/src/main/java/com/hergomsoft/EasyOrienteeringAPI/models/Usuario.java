package com.hergomsoft.EasyOrienteeringAPI.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(	name = "usuarios", 
    uniqueConstraints = { 
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email") 
})
public class Usuario {
    public static final int MLEN_NOMBRE = 20;
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    //@NotBlank
    //@Size(max = MLEN_NOMBRE)
    private String username;
    
    //@Email
    private String email;
    
    private String password;
    
    @Temporal(TemporalType.DATE)
    private Date fecharegistro;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_usuario", 
        joinColumns = @JoinColumn(name = "id_usuario"), 
        inverseJoinColumns = @JoinColumn(name = "id_role"))
    private Set<Role> roles = new HashSet<>();

    public Usuario() {}

    public Usuario(String username, String email, String password, Date fecharegistro) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fecharegistro = fecharegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return username;
    }

    public void setNombre(String username) {
        this.username = username;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Date getFechaRegistro() {
        return fecharegistro;
    }

    public void setFechaRegistro(Date fecharegistro) {
        this.fecharegistro = fecharegistro;
    }
    
}
