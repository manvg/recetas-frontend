package com.appweb.recetas.controllerTest.apiTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.appweb.recetas.controller.api.AuthenticationController;
import com.appweb.recetas.model.dto.AuthResponse;
import com.appweb.recetas.model.dto.LoginDto;
import com.appweb.recetas.service.AuthenticationService;

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
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testLoginSuccess() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setContrasena("password");

        when(authenticationService.authenticate(eq(loginDto), eq(response)))
            .thenReturn(new AuthResponse(true, "token", "Inicio de sesión exitoso"));

        String result = authenticationController.login(loginDto, redirectAttributes, response);

        assertEquals("redirect:/home", result);
        verify(redirectAttributes, never()).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void testLoginFailure() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setContrasena("password");

        when(authenticationService.authenticate(eq(loginDto), eq(response)))
            .thenReturn(new AuthResponse(false, null, "Credenciales inválidas"));

        String result = authenticationController.login(loginDto, redirectAttributes, response);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("error", "Credenciales inválidas");
    }

    @Test
    void testLoginException() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setContrasena("password");

        when(authenticationService.authenticate(eq(loginDto), eq(response)))
            .thenThrow(new RuntimeException("Error inesperado"));

        String result = authenticationController.login(loginDto, redirectAttributes, response);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Ha ocurrido un error"));
    }

    @Test
    void testLoginPageAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        String result = authenticationController.loginPage(redirectAttributes);

        assertEquals("redirect:/home", result);
    }

    @Test
    void testLoginPageAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = authenticationController.loginPage(redirectAttributes);

        assertEquals("login", result);
    }
}
