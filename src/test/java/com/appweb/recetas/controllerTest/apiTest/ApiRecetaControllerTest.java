package com.appweb.recetas.controllerTest.apiTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.appweb.recetas.config.Constants;
import com.appweb.recetas.controller.api.ApiRecetaController;
import com.appweb.recetas.model.dto.Receta;
import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.service.RecetaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ApiRecetaControllerTest {

    @Mock
    private RecetaService recetaService;

    @Mock
    private MultipartFile foto;

    @Mock
    private MultipartFile video;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApiRecetaController apiRecetaController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateRecetaSuccess() throws Exception {
        Receta receta = new Receta();
        receta.setNombre("Tacos");

        when(foto.isEmpty()).thenReturn(false);
        when(foto.getOriginalFilename()).thenReturn("image.jpg");
        when(video.isEmpty()).thenReturn(false);
        when(video.getOriginalFilename()).thenReturn("video.mp4");

        when(recetaService.create(eq(receta), eq(request), eq(response)))
            .thenReturn(new ResponseModel(true, "Creado exitosamente"));

        String result = apiRecetaController.createReceta(receta, foto, video, redirectAttributes, request, response);

        assertEquals("redirect:/home", result);
        verify(redirectAttributes, never()).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void testCreateRecetaFailure() throws Exception {
        Receta receta = new Receta();
        receta.setNombre("Tacos");

        when(foto.isEmpty()).thenReturn(true);
        when(video.isEmpty()).thenReturn(true);

        when(recetaService.create(eq(receta), eq(request), eq(response)))
            .thenReturn(new ResponseModel(false, "Error al crear receta"));

        String result = apiRecetaController.createReceta(receta, foto, video, redirectAttributes, request, response);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("error", "Error al crear receta");
    }

    @Test
    void testCreateRecetaException() throws Exception {
        Receta receta = new Receta();
        receta.setNombre("Tacos");

        when(foto.isEmpty()).thenThrow(new RuntimeException("Error inesperado"));

        String result = apiRecetaController.createReceta(receta, foto, video, redirectAttributes, request, response);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Ha ocurrido un error"));
    }

    @Test
    void testGetAllRecetasSuccess() {
        List<Receta> mockRecetas = List.of(new Receta(), new Receta());
        when(recetaService.getAllRecetas(any(), any(), any(), any(), any())).thenReturn(mockRecetas);

        List<Receta> result = apiRecetaController.getAllRecetas(null, null, null, null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllRecetasFailure() {
        when(recetaService.getAllRecetas(any(), any(), any(), any(), any())).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            apiRecetaController.getAllRecetas(null, null, null, null, null);
        });

        assertTrue(exception.getMessage().contains("Error al obtener las recetas"));
    }

    @Test
    void testGetRecetaByIdSuccess() {
        Receta mockReceta = new Receta();
        mockReceta.setIdReceta(1);

        when(recetaService.getRecetaById(eq(1), eq(request))).thenReturn(mockReceta);

        Receta result = apiRecetaController.getRecetaById(1, request);

        assertNotNull(result);
        assertEquals(1, result.getIdReceta());
    }

    @Test
    void testCreatePageAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        String result = apiRecetaController.createPage(redirectAttributes);

        assertEquals("redirect:/home", result);
    }

    @Test
    void testCreatePageAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = apiRecetaController.createPage(redirectAttributes);

        assertEquals("nueva-receta", result);
    }
}

