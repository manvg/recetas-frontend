package com.appweb.recetas.config;


import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenStore {

    private static final String COOKIE_NAME = "JWT_TOKEN";
    private static final int COOKIE_EXPIRY = 86400; // 1 día en segundos

    // Establece el token en una cookie
    public void setToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);   // Protege la cookie contra acceso JavaScript
        cookie.setPath("/");        // La cookie es accesible en todas las rutas
        cookie.setMaxAge(COOKIE_EXPIRY); // Duración de la cookie
        response.addCookie(cookie);
    }

    // Obtiene el token desde la cookie
    public String getToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // Retorna null si no hay cookie de token
    }

    // Limpia el token eliminando la cookie
    public void clearToken(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expira la cookie inmediatamente
        response.addCookie(cookie);
    }
}
