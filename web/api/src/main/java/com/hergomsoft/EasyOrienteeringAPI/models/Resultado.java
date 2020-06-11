package com.hergomsoft.EasyOrienteeringAPI.models;

import java.util.Date;
import java.util.List;

public class Resultado {
    // Tipos de resultado
    public static final int OK = 0;
    public static final int ERROR_TARJETA = 1;
    public static final int DESCALIFICADO = 2;
    
    private int tipo;
    private Date tiempoInicio;
    private Date tiempoFin;
    private List<Registro> registros;
}
