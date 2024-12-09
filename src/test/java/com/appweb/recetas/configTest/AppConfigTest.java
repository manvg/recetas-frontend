package com.appweb.recetas.configTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import com.appweb.recetas.config.AppConfig;

class AppConfigTest {

    @Test
    void testRestTemplateBeanCreation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            RestTemplate restTemplate = context.getBean(RestTemplate.class);

            assertNotNull(restTemplate, "El bean RestTemplate no debe ser nulo");
        }
    }
}

