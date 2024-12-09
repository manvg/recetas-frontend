package com.appweb.recetas.configTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appweb.recetas.config.TokenStore;

class TokenStoreTest {

    private TokenStore tokenStore;

    @BeforeEach
    void setup() {
        tokenStore = new TokenStore();
    }

    @Test
    void testSetToken() {
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        String token = "test-token";

        tokenStore.setToken(mockResponse, token);

        verify(mockResponse).addCookie(argThat(cookie -> 
            "JWT_TOKEN".equals(cookie.getName()) &&
            "test-token".equals(cookie.getValue()) &&
            cookie.isHttpOnly() &&
            "/".equals(cookie.getPath()) &&
            cookie.getMaxAge() == 86400
        ));
    }

    @Test
    void testGetTokenFound() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("JWT_TOKEN", "test-token") };
        when(mockRequest.getCookies()).thenReturn(cookies);

        String token = tokenStore.getToken(mockRequest);

        assertEquals("test-token", token, "El token debe coincidir con el valor de la cookie");
    }

    @Test
    void testGetTokenNotFound() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("OTHER_COOKIE", "other-value") };
        when(mockRequest.getCookies()).thenReturn(cookies);

        String token = tokenStore.getToken(mockRequest);

        assertNull(token, "Si no se encuentra la cookie, el token debe ser null");
    }

    @Test
    void testGetTokenNoCookies() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getCookies()).thenReturn(null);

        String token = tokenStore.getToken(mockRequest);

        assertNull(token, "Si no hay cookies en la solicitud, el token debe ser null");
    }

    @Test
    void testClearToken() {
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        tokenStore.clearToken(mockResponse);

        verify(mockResponse).addCookie(argThat(cookie -> 
            "JWT_TOKEN".equals(cookie.getName()) &&
            cookie.getValue() == null &&
            cookie.isHttpOnly() &&
            "/".equals(cookie.getPath()) &&
            cookie.getMaxAge() == 0
        ));
    }
}