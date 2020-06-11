package com.hergomsoft.EasyOrienteeringAPI.models;

import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Carrera {
    public static final int MLEN_NOMBRE = 50;
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Usuario organizador;
    private String nombre;
    private List<Recorrido> recorridos;
    private List<Resultado> resultados;

}

