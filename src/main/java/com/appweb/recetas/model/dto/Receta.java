package com.appweb.recetas.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Receta {
    private int idReceta;

    @NotNull
    @Size(min = 2, max = 100, message = "Debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull
    @Size(min = 2, max = 100, message = "Debe tener entre 2 y 250 caracteres")
    private String descripcion;

    @NotNull
    @Size(min = 2, max = 500, message = "Debe tener entre 2 y 500 caracteres")
    private String ingredientes;

    @NotNull
    @Size(min = 2, max = 500, message = "Debe tener entre 2 y 500 caracteres")
    private String instrucciones;

    @NotNull
    @Size(min = 2, max = 150, message = "Debe tener entre 2 y 150 caracteres")
    private String tipo_cocina;

    @NotNull
    @Size(min = 2, max = 150, message = "Debe tener entre 2 y 150 caracteres")
    private String pais_origen;

    @NotNull
    @Size(min = 2, max = 30, message = "Debe tener entre 2 y 30 caracteres")
    private String tiempo_coccion;

    @NotNull
    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres")
    private String dificultad;

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    public String getTipo_cocina() {
        return tipo_cocina;
    }

    public void setTipo_cocina(String tipo_cocina) {
        this.tipo_cocina = tipo_cocina;
    }

    public String getPais_origen() {
        return pais_origen;
    }

    public void setPais_origen(String pais_origen) {
        this.pais_origen = pais_origen;
    }

    public String getTiempo_coccion() {
        return tiempo_coccion;
    }

    public void setTiempo_coccion(String tiempo_coccion) {
        this.tiempo_coccion = tiempo_coccion;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }
}
