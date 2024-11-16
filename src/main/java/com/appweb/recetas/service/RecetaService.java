package com.appweb.recetas.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    
}
