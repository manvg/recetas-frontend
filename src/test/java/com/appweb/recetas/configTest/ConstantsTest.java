package com.appweb.recetas.configTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.appweb.recetas.config.Constants;

@SpringBootTest
class ConstantsTest {

    @Autowired
    private Environment environment;

    private Constants constants;
    private String backendUrl;

    @BeforeEach
    void setup() {
        backendUrl = environment.getProperty("backend.url");
        constants = new Constants();
        ReflectionTestUtils.setField(constants, "backendUrl", backendUrl);
    }

    @Test
    void testGetBackendUrl() {
        assertEquals(backendUrl, constants.getBackendUrl(), "El valor de backendUrl debe coincidir con el configurado");
    }

    @Test
    void testGetSecretKeySuccess() {
        String secretKey = System.getenv("JWT_SECRET");

        if (secretKey == null || secretKey.isEmpty()) {
            fail("La variable de entorno JWT_SECRET no está configurada en el entorno de pruebas");
        }

        String result = Constants.getSecretKey();

        assertEquals(secretKey, result, "La clave secreta debe coincidir con la configurada en la variable de entorno");
    }
    @Test
    void testGetSecretKeyFailure() {
        Constants.setEnvironmentSupplier(() -> null);
    
        Exception exception = assertThrows(IllegalStateException.class, Constants::getSecretKey);
        assertEquals("La variable de entorno JWT_SECRET no está configurada.", exception.getMessage());
    
        Constants.resetEnvironmentSupplier(); // Restaura el comportamiento original
    }
    
    

    
    
    @Test
    void testStaticConstants() {
        assertEquals("/api/authentication/login", Constants.BACKEND_URL_AUTH, "La URL de autenticación debe coincidir");
        assertEquals("src/main/resources/static/videos", Constants.ABSOLUTE_PATH_VIDEOS, "El path de videos debe coincidir");
        assertEquals("src/main/resources/static/images", Constants.ABSOLUTE_PATH_IMAGES, "El path de imágenes debe coincidir");
        assertEquals("images/", Constants.PATH_SAVE_IMAGES, "El path relativo de imágenes debe coincidir");
        assertEquals("videos/", Constants.PATH_SAVE_VIDEOS, "El path relativo de videos debe coincidir");
    }
}
