package com.appweb.recetas.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Usuario {

    private int idUsuario;

    @NotNull
    @Size(min = 2, max = 255, message = "Debe tener entre 2 y 255 caracteres")
    private String nombre;

    @NotNull
    @Size(max = 50, message = "Debe tener un máximo de 50 caracteres")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotNull
    @Size(min = 6, max = 20, message = "La contraseña debe contener entre 6 y 20 caracteres con al menos una letra y un número")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "La contraseña debe contener entre 6 y 20 caracteres con al menos una letra y un número")
    private String contrasena;

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getcontrasena() {
        return contrasena;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setcontrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}