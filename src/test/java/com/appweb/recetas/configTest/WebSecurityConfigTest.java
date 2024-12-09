package com.appweb.recetas.configTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

import com.appweb.recetas.config.WebSecurityConfig;

@SpringBootTest
class WebSecurityConfigTest {

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Test
    void testSecurityFilterChainExists() {
        try {
            SecurityFilterChain filterChain = webSecurityConfig.securityFilterChain(null);
            assertNotNull(filterChain, "El SecurityFilterChain no debe ser null");
        } catch (Exception e) {
            fail("No se esperaba una excepción al crear el SecurityFilterChain: " + e.getMessage());
        }
    }
}
