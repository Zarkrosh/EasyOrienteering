package com.hergomsoft.easyoapi.models;

public class Coordenadas {
    private float x;
    private float y;

    public Coordenadas() {}
    
    public Coordenadas(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
