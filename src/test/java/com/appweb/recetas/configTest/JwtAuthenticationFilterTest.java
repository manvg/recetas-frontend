package com.appweb.recetas.configTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.appweb.recetas.config.JwtAuthenticationFilter;
import com.appweb.recetas.config.TokenStore;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Key;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;

import com.appweb.recetas.config.Constants;

import java.util.Collections;
import com.appweb.recetas.config.JwtAuthenticationFilter;
import com.appweb.recetas.config.TokenStore;


@SpringBootTest
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;

    @Mock
    private TokenStore mockTokenStore;

    private Key secretKey;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        filter = new JwtAuthenticationFilter(mockTokenStore);
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave segura
        System.setProperty("JWT_SECRET", new String(secretKey.getEncoded())); // Simula la clave en el entorno
    }

    private void invokeDoFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws Exception {
        Method method = JwtAuthenticationFilter.class.getDeclaredMethod("doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(filter, request, response, chain);
    }

    @Test
    void testDoFilterInternalValidToken() throws Exception {
        // Mock del token válido
        String validToken = Jwts.builder()
                .setSubject("usuario")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("#E@M%R!&EGD%srHG823sAK3&gBi&U4Q7".getBytes()))
                .compact();
    
        when(mockTokenStore.getToken(any(HttpServletRequest.class))).thenReturn(validToken);
    
        // Mock de Request, Response y FilterChain
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        FilterChain mockFilterChain = mock(FilterChain.class);
    
        // Ejecuta el filtro
        jwtAuthenticationFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
    
        // Verificaciones
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication, "El contexto de seguridad debe tener una autenticación");
        assertEquals("usuario", authentication.getName(), "El nombre de usuario debe coincidir");
        assertTrue(authentication.isAuthenticated(), "La autenticación debe estar configurada como autenticada");
    
        // Verifica que el filtro pase al siguiente en la cadena
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }
    
    
    

    @Test
    void testDoFilterInternalInvalidToken() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        FilterChain mockChain = mock(FilterChain.class);

        String invalidToken = "invalid-token";
        when(mockTokenStore.getToken(mockRequest)).thenReturn(invalidToken);

        invokeDoFilterInternal(mockRequest, mockResponse, mockChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "El contexto de seguridad no debe tener una autenticación");

        verify(mockChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testDoFilterInternalNoToken() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        FilterChain mockChain = mock(FilterChain.class);

        when(mockTokenStore.getToken(mockRequest)).thenReturn(null);

        invokeDoFilterInternal(mockRequest, mockResponse, mockChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(), "El contexto de seguridad no debe tener una autenticación");

        verify(mockChain).doFilter(mockRequest, mockResponse);
    }
}
