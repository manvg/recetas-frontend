package com.appweb.recetas.controllerTest.apiTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.appweb.recetas.controller.api.ApiUsuarioController;
import com.appweb.recetas.model.dto.ResponseModel;
import com.appweb.recetas.model.dto.Usuario;
import com.appweb.recetas.service.UsuarioService;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
class ApiUsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

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
    private ApiUsuarioController apiUsuarioController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateUsuarioSuccess() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");

        when(usuarioService.create(eq(usuario), eq(request), eq(response)))
            .thenReturn(new ResponseModel(true, "Usuario creado exitosamente"));

        String result = apiUsuarioController.createUsuario(usuario, redirectAttributes, request, response);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes, never()).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void testCreateUsuarioFailure() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");

        when(usuarioService.create(eq(usuario), eq(request), eq(response)))
            .thenReturn(new ResponseModel(false, "Error al crear usuario"));

        String result = apiUsuarioController.createUsuario(usuario, redirectAttributes, request, response);

        assertEquals("redirect:/home", result);
        verify(redirectAttributes).addFlashAttribute("error", "Error al crear usuario");
    }

    @Test
    void testCreateUsuarioException() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");

        when(usuarioService.create(eq(usuario), eq(request), eq(response)))
            .thenThrow(new RuntimeException("Error inesperado"));

        String result = apiUsuarioController.createUsuario(usuario, redirectAttributes, request, response);

        assertEquals("redirect:/register", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Ha ocurrido un error"));
    }

    @Test
    void testCreatePageAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        String result = apiUsuarioController.createPage(redirectAttributes);

        assertEquals("redirect:/home", result);
    }

    @Test
    void testCreatePageAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = apiUsuarioController.createPage(redirectAttributes);

        assertEquals("register", result);
    }
}
