package com.appweb.recetas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.model.dto.Usuario;

@Service
public class UsuarioService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${backend.url}")
    private String urlBackend;

    public ResponseModel create(Usuario usuario, HttpServletRequest request, HttpServletResponse response) {
        String backendUrl = urlBackend + "/api/usuarios";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Usuario> requestEntity = new HttpEntity<>(usuario, headers);

        try {
            ResponseEntity<ResponseModel> apiResponse = restTemplate.exchange(backendUrl, HttpMethod.POST, requestEntity, ResponseModel.class);

            if (apiResponse.getStatusCode().is2xxSuccessful() && apiResponse.getBody() != null) {
                return apiResponse.getBody();
            } else {
                return new ResponseModel(false, "Error al crear usuario en backend.");
            }

        } catch (HttpClientErrorException e) {
            var mensajeError = e.getMessage().toString();
            return new ResponseModel(false, "Error de comunicación con el servidor: " + mensajeError);
        } catch (Exception e) {
            return new ResponseModel(false, "Error inesperado: " + e.getMessage());
        }
    }
}

