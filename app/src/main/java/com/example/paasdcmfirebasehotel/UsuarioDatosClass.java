package com.example.paasdcmfirebasehotel;

public class UsuarioDatosClass {

    private String objNombre;

    public UsuarioDatosClass() {
    }

    public UsuarioDatosClass(String objNombre) {
        this.objNombre = objNombre;
    }

    public String getObjNombre() {
        return objNombre;
    }

    public void setObjNombre(String objNombre) {
        this.objNombre = objNombre;
    }

    @Override
    public String toString() {
        return objNombre;
    }
}
