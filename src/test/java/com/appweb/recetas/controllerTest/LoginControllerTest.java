package com.appweb.recetas.controllerTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.appweb.recetas.controller.LoginController;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testLoginPageAuthenticatedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        String result = loginController.loginPage(model);

        assertEquals("redirect:/home", result);
        verify(model, never()).addAttribute(eq("authenticated"), anyBoolean());
    }

    @Test
    void testLoginPageAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = loginController.loginPage(model);

        assertEquals("login", result);
        verify(model).addAttribute("authenticated", false);
    }

    @Test
    void testLoginPageNullAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);

        String result = loginController.loginPage(model);

        assertEquals("login", result);
        verify(model).addAttribute("authenticated", false);
    }
}

