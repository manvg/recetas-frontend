package com.appweb.recetas.model.dto;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Validated
@NoArgsConstructor
public class LoginDto {

    @Size(max = 50, message = "Debe tener un máximo de 50 caracteres")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @Size(min = 6, max = 20, message = "La contraseña debe contener entre 6 y 20 caracteres con al menos una letra y un número")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "La contraseña debe contener entre 6 y 20 caracteres con al menos una letra y un número")
    private String contrasena;

    public LoginDto(String email, String contrasena) {
        this.email = email;
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}