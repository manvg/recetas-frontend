package com.appweb.recetas.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletResponse;

import com.appweb.recetas.config.Constants;
import com.appweb.recetas.config.TokenStore;
import com.appweb.recetas.model.dto.AuthResponse;
import com.appweb.recetas.model.dto.LoginDto;

@Service
public class AuthenticationService {

    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenStore tokenStore;

    @Value("${backend.url}")
    private String backendUrl;

    public AuthResponse authenticate(LoginDto loginDto, HttpServletResponse response) {
        String backendLoginUrl = backendUrl + Constants.BACKEND_URL_AUTH;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginDto> requestEntity = new HttpEntity<>(loginDto, headers);

        try {
            ResponseEntity<AuthResponse> apiResponse = restTemplate.exchange(backendLoginUrl, HttpMethod.POST, requestEntity, AuthResponse.class);

            AuthResponse authResponse = apiResponse.getBody();
            if (apiResponse.getStatusCode().is2xxSuccessful() && authResponse != null) {
                String token = authResponse.getToken();

                tokenStore.setToken(response, token);

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(ROLE_USER));

                Authentication authenticatedToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticatedToken);

                return authResponse;
            } else {
                return new AuthResponse(false, null, "Usuario y contraseña inválidos.");
            }

        } catch (HttpClientErrorException e) {
            return new AuthResponse(false, null, "Error de comunicación con el servidor: " + e.getMessage());
        } catch (Exception e) {
            return new AuthResponse(false, null, "Error inesperado: " + e.getMessage());
        }
    }
}
