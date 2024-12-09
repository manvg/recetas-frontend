package com.appweb.recetas.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.model.dto.Usuario;
import com.appweb.recetas.service.UsuarioService;

@SpringBootTest
class UsuarioServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UsuarioService usuarioService;

    @Autowired
    private Environment environment;

    private String backendUrl;

    @BeforeEach
    void setup() {
        backendUrl = environment.getProperty("backend.url");
        if (backendUrl == null || backendUrl.isEmpty()) {
            throw new IllegalStateException("La propiedad 'backend.url' no está configurada.");
        }
        org.springframework.test.util.ReflectionTestUtils.setField(usuarioService, "urlBackend", backendUrl);
    }

    @Test
    void testCreateUsuarioSuccess() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setEmail("juan@example.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ResponseModel mockResponse = new ResponseModel(true, "Usuario creado con éxito.");

        when(restTemplate.exchange(
            eq(backendUrl + "/api/usuarios"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(ResponseModel.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseModel result = usuarioService.create(usuario, request, response);

        assertTrue(result.getStatus());
        assertEquals("Usuario creado con éxito.", result.getMessage());
    }

    @Test
    void testCreateUsuarioServerError() {
        Usuario usuario = new Usuario();
        usuario.setNombre("UsuarioPrueba");
        usuario.setEmail("usuarioPrueba@gmail.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(restTemplate.exchange(
            eq(backendUrl + "/api/usuarios"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(ResponseModel.class)
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseModel result = usuarioService.create(usuario, request, response);

        assertFalse(result.getStatus());
        assertTrue(result.getMessage().contains("Error de comunicación con el servidor:"));
    }

    @Test
    void testCreateUsuarioUnexpectedError() {
        Usuario usuario = new Usuario();
        usuario.setNombre("UsuarioPrueba");
        usuario.setEmail("usuarioPrueba@gmail.com");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(restTemplate.exchange(
            eq(backendUrl + "/api/usuarios"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(ResponseModel.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        ResponseModel result = usuarioService.create(usuario, request, response);

        assertFalse(result.getStatus());
        assertEquals("Error inesperado: Unexpected error", result.getMessage());
    }
}