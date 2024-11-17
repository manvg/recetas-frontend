package com.appweb.recetas.model.dto;

import java.util.List;

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
    private String tipoCocina;

    @NotNull
    @Size(min = 2, max = 150, message = "Debe tener entre 2 y 150 caracteres")
    private String paisOrigen;

    @NotNull
    @Size(min = 2, max = 30, message = "Debe tener entre 2 y 30 caracteres")
    private String tiempoCoccion;

    @NotNull
    @Size(min = 2, max = 50, message = "Debe tener entre 2 y 50 caracteres")
    private String dificultad;

    private String urlImagen; 
    
    private String urlVideo;

    private List<String> ingredientesList;
    private List<String> instruccionesList;

    // Getters y Setters

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
        return tipoCocina;
    }

    public void setTipoCocina(String tipoCocina) {
        this.tipoCocina = tipoCocina;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public String getTiempoCoccion() {
        return tiempoCoccion;
    }

    public void setTiempoCoccion(String tiempoCoccion) {
        this.tiempoCoccion = tiempoCoccion;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public List<String> getIngredientesList() {
        return ingredientesList;
    }
    
    public void setIngredientesList(List<String> ingredientesList) {
        this.ingredientesList = ingredientesList;
    }
    
    public List<String> getInstruccionesList() {
        return instruccionesList;
    }
    
    public void setInstruccionesList(List<String> instruccionesList) {
        this.instruccionesList = instruccionesList;
    }
}
