package com.appweb.recetas.configTest;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.appweb.recetas.config.ContentSecurityPolicyFilter;

import java.io.IOException;

class ContentSecurityPolicyFilterTest {

    private ContentSecurityPolicyFilter filter;

    @BeforeEach
    void setup() {
        filter = new ContentSecurityPolicyFilter();
    }

    @Test
    void testDoFilterAddsCSPHeader() throws IOException, ServletException {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        FilterChain mockChain = Mockito.mock(FilterChain.class);

        filter.doFilter(mockRequest, mockResponse, mockChain);

        ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);

        // Verifica que el encabezado "Content-Security-Policy" fue agregado
        Mockito.verify(mockResponse).setHeader(headerNameCaptor.capture(), headerValueCaptor.capture());

        assertEquals("Content-Security-Policy", headerNameCaptor.getValue());
        assertEquals(
            "default-src 'self'; " +
            "script-src 'self'; " +
            "style-src 'self'; " +
            "img-src 'self' data:; " +
            "font-src 'self'; " +
            "connect-src 'self'; " +
            "frame-ancestors 'self'; " +
            "object-src 'none'; " +
            "form-action 'self';",
            headerValueCaptor.getValue(),
            "El valor del encabezado Content-Security-Policy debe coincidir con el configurado"
        );

        // Verifica que el chain.doFilter() se haya llamado
        Mockito.verify(mockChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testDoFilterNonHttpResponse() throws IOException, ServletException {
        ServletRequest mockRequest = Mockito.mock(ServletRequest.class);
        ServletResponse mockResponse = Mockito.mock(ServletResponse.class);
        FilterChain mockChain = Mockito.mock(FilterChain.class);

        filter.doFilter(mockRequest, mockResponse, mockChain);

        assertFalse(mockResponse instanceof HttpServletResponse, "La respuesta no debe ser una instancia de HttpServletResponse");

        Mockito.verify(mockChain).doFilter(mockRequest, mockResponse);
    }
}

