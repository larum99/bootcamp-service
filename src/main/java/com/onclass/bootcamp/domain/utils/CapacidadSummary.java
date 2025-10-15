package com.onclass.bootcamp.domain.utils;

public class CapacidadSummary {

    private Long id;
    private String nombre;

    public CapacidadSummary(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}