package com.appweb.recetas.controllerTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.appweb.recetas.controller.LogoutController;

class LogoutControllerTest {

    private final LogoutController logoutController = new LogoutController();

    @Test
    void testLogoutRedirection() {
        String result = logoutController.logout();

        assertEquals("redirect:/login", result, "El método logout debe redirigir a /login");
    }
}
