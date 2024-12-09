package com.appweb.recetas.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.appweb.recetas.config.TokenStore;
import com.appweb.recetas.model.dto.Receta;
import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.service.RecetaService;

import java.util.Collections;
import java.util.List;

@SpringBootTest
class RecetaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TokenStore tokenStore;

    @InjectMocks
    private RecetaService recetaService;

    @Autowired
    private Environment environment;

    private String backendUrl;

    @BeforeEach
    void setup() {
        backendUrl = environment.getProperty("backend.url");
        if (backendUrl == null || backendUrl.isEmpty()) {
            throw new IllegalStateException("La propiedad 'backend.url' no está configurada.");
        }
        org.springframework.test.util.ReflectionTestUtils.setField(recetaService, "urlBackend", backendUrl);
    }

    // Métodos existentes: no se han eliminado
    @Test
    void testCreateRecetaSuccess() {
        Receta receta = new Receta();
        receta.setNombre("Receta de prueba");
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ResponseModel mockResponse = new ResponseModel(true, "Receta creada con éxito.");

        when(tokenStore.getToken(request)).thenReturn("mock-token");
        when(restTemplate.exchange(
            eq(backendUrl + "/api/recetas"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(ResponseModel.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        ResponseModel result = recetaService.create(receta, request, response);

        assertTrue(result.getStatus());
        assertEquals("Receta creada con éxito.", result.getMessage());
    }

    @Test
    void testCreateRecetaWithoutToken() {
        Receta receta = new Receta();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenStore.getToken(request)).thenReturn(null);

        ResponseModel result = recetaService.create(receta, request, response);

        assertFalse(result.getStatus());
        assertEquals("Token no encontrado. Inicie sesión nuevamente.", result.getMessage());
    }

    @Test
    void testCreateRecetaHttpClientErrorException() {
        Receta receta = new Receta();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenStore.getToken(request)).thenReturn("mock-token");
        when(restTemplate.exchange(
            eq(backendUrl + "/api/recetas"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(ResponseModel.class)
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST, "Error del servidor"));

        ResponseModel result = recetaService.create(receta, request, response);

        assertFalse(result.getStatus());
        assertTrue(result.getMessage().contains("Error de comunicación con el servidor:"));
    }

    @Test
    void testCreateRecetaUnexpectedError() {
        Receta receta = new Receta();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(tokenStore.getToken(request)).thenReturn("mock-token");
        when(restTemplate.exchange(
            eq(backendUrl + "/api/recetas"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(ResponseModel.class)
        )).thenThrow(new RuntimeException("Error inesperado"));

        ResponseModel result = recetaService.create(receta, request, response);

        assertFalse(result.getStatus());
        assertTrue(result.getMessage().contains("Error inesperado:"));
    }

    @Test
    void testGetAllRecetasSuccess() {
        List<Receta> mockRecetas = List.of(new Receta(), new Receta());

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(mockRecetas));

        List<Receta> result = recetaService.getAllRecetas("nombre", null, null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllRecetasEmpty() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(Collections.emptyList()));

        List<Receta> result = recetaService.getAllRecetas(null, null, null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetRecetaByIdSuccess() {
        Integer id = 1;
        Receta mockReceta = new Receta();
        mockReceta.setIdReceta(id);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(tokenStore.getToken(request)).thenReturn("mock-token");
        when(restTemplate.exchange(
            eq(backendUrl + "/api/recetas/" + id),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Receta.class)
        )).thenReturn(ResponseEntity.ok(mockReceta));

        Receta result = recetaService.getRecetaById(id, request);

        assertNotNull(result);
        assertEquals(id, result.getIdReceta());
    }

    @Test
    void testGetRecetaByIdWithoutToken() {
        Integer id = 1;
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(tokenStore.getToken(request)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recetaService.getRecetaById(id, request);
        });

        assertEquals("Token no válido o ausente.", exception.getMessage());
    }

    @Test
    void testGetRecetaByIdError() {
        Integer id = 1;
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(tokenStore.getToken(request)).thenReturn("mock-token");
        when(restTemplate.exchange(
            eq(backendUrl + "/api/recetas/" + id),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Receta.class)
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recetaService.getRecetaById(id, request);
        });

        assertTrue(exception.getMessage().contains("Error al obtener la receta:"));
    }

    @Test
    void testGetAllRecetasWithFilters() {
        // Caso donde todos los filtros están presentes
        List<Receta> mockRecetas = List.of(new Receta(), new Receta());

        when(restTemplate.exchange(
            contains("nombre=nombre"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(mockRecetas));

        List<Receta> result = recetaService.getAllRecetas("nombre", "descripcion", "tipoCocina", "paisOrigen", "dificultad");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllRecetasWithoutFilters() {
        // Caso donde no se envían filtros
        when(restTemplate.exchange(
            eq(backendUrl + "/api/recetas"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(Collections.emptyList()));

        List<Receta> result = recetaService.getAllRecetas(null, null, null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllRecetasHttpClientErrorException() {
        //Caso donde ocurre un HttpClientErrorException
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST));

        List<Receta> result = recetaService.getAllRecetas("nombre", null, null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllRecetasUnexpectedError() {
        //Caso donde ocurre un error inesperado
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Error inesperado"));

        List<Receta> result = recetaService.getAllRecetas("nombre", null, null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}