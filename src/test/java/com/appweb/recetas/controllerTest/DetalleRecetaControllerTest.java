package com.appweb.recetas.controllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.appweb.recetas.controller.DetalleRecetaController;
import com.appweb.recetas.controller.api.ApiRecetaController;
import com.appweb.recetas.model.dto.Receta;

import java.util.Arrays;

class DetalleRecetaControllerTest {

    @Mock
    private ApiRecetaController apiRecetaController;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DetalleRecetaController detalleRecetaController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testDetalleRecetaSuccess() {
        Integer recetaId = 1;
        Receta mockReceta = new Receta();
        mockReceta.setIdReceta(recetaId);
        mockReceta.setIngredientes("harina;azúcar;huevo");
        mockReceta.setInstrucciones("mezclar;hornear;servir");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        when(apiRecetaController.getRecetaById(eq(recetaId), eq(request))).thenReturn(mockReceta);

        String result = detalleRecetaController.detalleReceta(recetaId, model, request);

        assertEquals("detalle-receta", result);
        verify(model).addAttribute("authenticated", true);
        verify(model).addAttribute(eq("receta"), argThat(receta -> {
            Receta recetaObj = (Receta) receta;
            return recetaObj.getIngredientesList().equals(Arrays.asList("harina", "azúcar", "huevo")) &&
                   recetaObj.getInstruccionesList().equals(Arrays.asList("mezclar", "hornear", "servir"));
        }));
    }

    @Test
    void testDetalleRecetaAnonymousUser() {
        Integer recetaId = 1;
        Receta mockReceta = new Receta();
        mockReceta.setIdReceta(recetaId);
        mockReceta.setIngredientes("harina;azúcar;huevo");
        mockReceta.setInstrucciones("mezclar;hornear;servir");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(apiRecetaController.getRecetaById(eq(recetaId), eq(request))).thenReturn(mockReceta);

        String result = detalleRecetaController.detalleReceta(recetaId, model, request);

        assertEquals("detalle-receta", result);
        verify(model).addAttribute("authenticated", false);
        verify(model).addAttribute(eq("receta"), any(Receta.class));
    }

    @Test
    void testDetalleRecetaRecetaNotFound() {
        Integer recetaId = 1;
    
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(null); // Simula que el nombre es nulo
        when(apiRecetaController.getRecetaById(eq(recetaId), eq(request))).thenReturn(null);
    
        String result = detalleRecetaController.detalleReceta(recetaId, model, request);
    
        assertEquals("detalle-receta", result);
        verify(model).addAttribute("authenticated", true);
        verify(model).addAttribute(eq("receta"), isNull());
    }
}
    

