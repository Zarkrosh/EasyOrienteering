package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Recorrido;

public interface IRecorridoService {
    
    /**
     * Devuelve el recorrido con el ID especificado, o null si no existe.
     * @param id Identificador del recorrido
     * @return Recorrido o null
     */
    Recorrido getRecorrido(long id);
    
    
    /**
     * Crea o actualiza el recorrido.
     * @param recorrido Recorrido
     * @return Recorrido guardado o null si no pudo guardarse
     */
    Recorrido guardaRecorrido(Recorrido recorrido);
}
