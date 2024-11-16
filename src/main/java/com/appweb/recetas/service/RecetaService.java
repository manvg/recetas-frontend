package com.appweb.recetas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.appweb.recetas.config.Constants;
import com.appweb.recetas.config.TokenStore;
import com.appweb.recetas.model.dto.Receta;
import com.appweb.recetas.model.dto.ResponseModel;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Collections;

@Service
public class RecetaService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenStore tokenStore;

    public ResponseModel create(Receta receta, HttpServletRequest request, HttpServletResponse response) {
        String backendUrl = Constants.BACKEND_URL + "/api/recetas";
        // Obtiene el token desde el TokenStore
        String token = tokenStore.getToken(request);
        
        // Prepara los headers con el token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        } else {
            return new ResponseModel(false, "Token no encontrado. Inicie sesión nuevamente.");
        }

        HttpEntity<Receta> requestEntity = new HttpEntity<>(receta, headers);

        try {
            // Llamada a la API del backend
            ResponseEntity<ResponseModel> apiResponse = restTemplate.exchange(backendUrl, HttpMethod.POST, requestEntity, ResponseModel.class);

            if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
                return apiResponse.getBody();
            } else {
                return new ResponseModel(false, "Error al crear receta en el backend.");
            }

        } catch (HttpClientErrorException e) {
            return new ResponseModel(false, "Error de comunicación con el servidor: " + e.getMessage());
        } catch (Exception e) {
            return new ResponseModel(false, "Error inesperado: " + e.getMessage());
        }
    }

    public List<Receta> getAllRecetas(HttpServletRequest request) {
        String backendUrl = Constants.BACKEND_URL + "/api/recetas";

        String token = tokenStore.getToken(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        } else {
            return Collections.emptyList();
        }
    
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    
        try {
            // Llamada a la API del backend
            ResponseEntity<List<Receta>> apiResponse = restTemplate.exchange(backendUrl,HttpMethod.GET,requestEntity,new ParameterizedTypeReference<List<Receta>>() {} );
    
            if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
                return apiResponse.getBody();
            } else {
                return Collections.emptyList();
            }
    
        } catch (HttpClientErrorException e) {
            //System.err.println("Error de comunicación con el servidor: " + e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            // Manejo de errores generales
            //System.err.println("Error inesperado: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    
}
