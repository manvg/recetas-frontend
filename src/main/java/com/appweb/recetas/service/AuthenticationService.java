package com.appweb.recetas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.appweb.recetas.config.Constants;
import com.appweb.recetas.model.dto.AuthResponse;
import com.appweb.recetas.model.dto.LoginDto;

@Service
public class AuthenticationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthResponse authenticate(LoginDto loginDto) {
        String backendLoginUrl = Constants.BACKEND_URL + Constants.BACKEND_URL_AUTH;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginDto> requestEntity = new HttpEntity<>(loginDto, headers);

        try {
            // Llama al backend y recibe la respuesta como AuthResponse
            ResponseEntity<AuthResponse> response = restTemplate.exchange(backendLoginUrl, HttpMethod.POST, requestEntity, AuthResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                return new AuthResponse(false, null, "Usuario y contraseña inválidos.");
            }

        } catch (HttpClientErrorException e) {
            // Manejo de errores en caso de fallo de la solicitud al backend
            return new AuthResponse(false, null, "Error de comunicación con el servidor."+ e.getMessage());
        }
    }
}