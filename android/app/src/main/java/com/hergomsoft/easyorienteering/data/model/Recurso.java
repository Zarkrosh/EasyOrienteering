package com.hergomsoft.easyorienteering.data.model;

public class Recurso<T> {
    private T recurso;
    private String error;

    public Recurso() {
        recurso = null;
        error = null;
    }

    // En caso de éxito se usa este constructor
    public Recurso(T recurso) {
        this.recurso = recurso;
        this.error = null;
    }

    // En caso de error se usa este constructor
    public Recurso(String error) {
        this.error = error;
        this.recurso = null;
    }

    public T getRecurso() {
        return recurso;
    }

    public void setRecurso(T recurso) {
        this.recurso = recurso;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean hayError() {
        return recurso == null;
    }
}
