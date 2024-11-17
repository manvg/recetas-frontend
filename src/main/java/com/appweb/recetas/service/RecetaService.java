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
import org.springframework.web.util.UriComponentsBuilder;

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

    public List<Receta> getAllRecetas(String nombre,String descripcion,String tipoCocina,String paisOrigen,String dificultad,HttpServletRequest request) 
    {
        String backendUrl = Constants.BACKEND_URL + "/api/recetas";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(backendUrl);

        // Agregar parámetros opcionales a la URL
        if (nombre != null && !nombre.isEmpty()) {
            uriBuilder.queryParam("nombre", nombre);
        }
        if (descripcion != null && !descripcion.isEmpty()) {
            uriBuilder.queryParam("descripcion", descripcion);
        }
        if (tipoCocina != null && !tipoCocina.isEmpty()) {
            uriBuilder.queryParam("tipoCocina", tipoCocina);
        }
        if (paisOrigen != null && !paisOrigen.isEmpty()) {
            uriBuilder.queryParam("paisOrigen", paisOrigen);
        }
        if (dificultad != null && !dificultad.isEmpty()) {
            uriBuilder.queryParam("dificultad", dificultad);
        }

        //Construir la URL final
        String finalUrl = uriBuilder.toUriString();

        //Obtener el token para la autenticación
        //String token = tokenStore.getToken(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // if (token != null) {
        //     headers.setBearerAuth(token);
        // } else {
        //     return Collections.emptyList();
        // }

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Receta>> apiResponse = restTemplate.exchange(finalUrl,HttpMethod.GET,requestEntity,new ParameterizedTypeReference<List<Receta>>() {});

            if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
                return apiResponse.getBody();
            } else {
                return Collections.emptyList();
            }

        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Receta getRecetaById(Integer id, HttpServletRequest request) {
        String backendUrl = Constants.BACKEND_URL + "/api/recetas/" + id;
    
        String token = tokenStore.getToken(request);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        } else {
            throw new RuntimeException("Token no válido o ausente.");
        }
    
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    
        try {
            ResponseEntity<Receta> apiResponse = restTemplate.exchange(
                backendUrl,
                HttpMethod.GET,
                requestEntity,
                Receta.class
            );
    
            if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
                return apiResponse.getBody();
            } else {
                throw new RuntimeException("No se pudo obtener la receta.");
            }
    
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener la receta: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }
    
}
