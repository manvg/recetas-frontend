package com.appweb.recetas.config;

import org.springframework.stereotype.Component;

@Component
public class TokenStore {

    private String token;
    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
    public void clearToken() {
        this.token = null;
    }
}