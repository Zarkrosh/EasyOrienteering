package com.hergomsoft.easyoapi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @Column(name = "TOKEN")
    private String token;
    
    @ManyToOne
    private Usuario usuario;

    public Token() {}
    
    public Token(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    
}
