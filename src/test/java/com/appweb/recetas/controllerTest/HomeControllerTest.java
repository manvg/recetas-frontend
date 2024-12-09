package com.appweb.recetas.controllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.appweb.recetas.controller.HomeController;
import com.appweb.recetas.controller.api.ApiRecetaController;
import com.appweb.recetas.model.dto.Receta;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private ApiRecetaController apiRecetaController;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testHomeWithResults() {
        String name = "Recetas";
        String nombre = "Tacos";
        List<Receta> mockRecetas = List.of(new Receta(), new Receta());

        when(apiRecetaController.getAllRecetas(eq(nombre), any(), any(), any(), any()))
            .thenReturn(mockRecetas);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        String result = homeController.home(name, nombre, null, null, null, null, model);

        assertEquals("home", result);
        verify(model).addAttribute("name", name);
        verify(model).addAttribute("recetas", mockRecetas);
        verify(model).addAttribute("authenticated", true);
    }

    @Test
    void testHomeWithEmptyResults() {
        String name = "Recetas";
        String nombre = "NoExiste";

        when(apiRecetaController.getAllRecetas(eq(nombre), any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = homeController.home(name, nombre, null, null, null, null, model);

        assertEquals("home", result);
        verify(model).addAttribute("name", name);
        verify(model).addAttribute("recetas", Collections.emptyList());
        verify(model).addAttribute("authenticated", false);
    }

    @Test
    void testHomeWithNullResults() {
        String name = "Recetas";
        String nombre = "NoExiste";

        when(apiRecetaController.getAllRecetas(eq(nombre), any(), any(), any(), any()))
            .thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        String result = homeController.home(name, nombre, null, null, null, null, model);

        assertEquals("home", result);
        verify(model).addAttribute("name", name);
        verify(model).addAttribute("recetas", Collections.emptyList());
        verify(model).addAttribute("authenticated", true);
    }

    @Test
    void testRootRedirectsToHome() {
        String name = "Recetas";
        String nombre = "Lazaña";
        List<Receta> mockRecetas = List.of(new Receta());

        when(apiRecetaController.getAllRecetas(eq(nombre), any(), any(), any(), any()))
            .thenReturn(mockRecetas);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        String result = homeController.root(name, nombre, null, null, null, null, model);

        assertEquals("home", result);
        verify(model).addAttribute("name", name);
        verify(model).addAttribute("recetas", mockRecetas);
        verify(model).addAttribute("authenticated", true);
    }
}