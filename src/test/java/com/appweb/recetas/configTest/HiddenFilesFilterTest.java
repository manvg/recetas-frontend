package com.appweb.recetas.configTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appweb.recetas.config.HiddenFilesFilter;

import java.io.IOException;

class HiddenFilesFilterTest {

    private HiddenFilesFilter filter;

    @BeforeEach
    void setup() {
        filter = new HiddenFilesFilter();
    }

    @Test
    void testDoFilterBlocksHiddenFiles() throws IOException, ServletException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        FilterChain mockChain = mock(FilterChain.class);

        when(mockRequest.getRequestURI()).thenReturn("/.git/config");

        filter.doFilter(mockRequest, mockResponse, mockChain);

        verify(mockResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
        verify(mockChain, never()).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testDoFilterAllowsNormalFiles() throws IOException, ServletException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        FilterChain mockChain = mock(FilterChain.class);

        when(mockRequest.getRequestURI()).thenReturn("/public/file.txt");

        filter.doFilter(mockRequest, mockResponse, mockChain);

        verify(mockResponse, never()).sendError(HttpServletResponse.SC_FORBIDDEN);
        verify(mockChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void testInitDoesNothing() throws ServletException {
        FilterConfig mockFilterConfig = mock(FilterConfig.class);

        assertDoesNotThrow(() -> filter.init(mockFilterConfig), "El método init no debe lanzar excepciones");
    }

    @Test
    void testDestroyDoesNothing() {
        assertDoesNotThrow(() -> filter.destroy(), "El método destroy no debe lanzar excepciones");
    }
}

