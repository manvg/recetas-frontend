package com.appweb.recetas.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.appweb.recetas.config.Constants;
import com.appweb.recetas.config.TokenStore;
import com.appweb.recetas.model.dto.AuthResponse;
import com.appweb.recetas.model.dto.LoginDto;
import com.appweb.recetas.service.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
class AuthenticationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TokenStore tokenStore;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Autowired
    private Environment environment;

    private String backendUrl;

    @BeforeEach
    void setup() {
        backendUrl = environment.getProperty("backend.url");
        ReflectionTestUtils.setField(authenticationService, "backendUrl", backendUrl);
    }

    //Verifica que la autenticación con credenciales válidas retorna un token y establece el token en el almacenamiento.
    @Test
    void authenticateSuccess() {
        LoginDto loginDto = new LoginDto("usuariotest@example.com", "testing123");
        AuthResponse mockResponse = new AuthResponse(true, "mock-token", "Success");

        ResponseEntity<AuthResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(responseEntity.getBody()).thenReturn(mockResponse);

        String backendLoginUrl = backendUrl + Constants.BACKEND_URL_AUTH;
        when(restTemplate.exchange(eq(backendLoginUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthResponse.class)))
                .thenReturn(responseEntity);

        AuthResponse result = authenticationService.authenticate(loginDto, response);

        assertEquals("mock-token", result.getToken());
        assertEquals("Success", result.getMessage());
        verify(tokenStore, times(1)).setToken(response, "mock-token");
    }

    @Test
    void authenticateInvalidCredentials() {
        LoginDto loginDto = new LoginDto("user@example.com", "wrong-password");
        String backendLoginUrl = backendUrl + Constants.BACKEND_URL_AUTH;
    
        doThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.UNAUTHORIZED))
            .when(restTemplate)
            .exchange(eq(backendLoginUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthResponse.class));
    
        AuthResponse result = authenticationService.authenticate(loginDto, response);
    
        assertEquals(false, result.isStatus());
        assertEquals("Error de comunicación con el servidor: 401 UNAUTHORIZED", result.getMessage());
    
        // Verifica que el método setToken no fue llamado
        verify(tokenStore, never()).setToken(any(), anyString());
    }
    
    @Test
    void authenticateUnexpectedError() {
        LoginDto loginDto = new LoginDto("user@example.com", "password");

        String backendLoginUrl = backendUrl + Constants.BACKEND_URL_AUTH;
        when(restTemplate.exchange(eq(backendLoginUrl), eq(HttpMethod.POST), any(HttpEntity.class), eq(AuthResponse.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        AuthResponse result = authenticationService.authenticate(loginDto, response);

        assertEquals(false, result.isStatus());
        assertEquals("Error inesperado: Unexpected error", result.getMessage());
        verify(tokenStore, times(0)).setToken(any(), anyString());
    }
}
