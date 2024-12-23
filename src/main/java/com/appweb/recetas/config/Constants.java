package com.appweb.recetas.config;

import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    @Value("${backend.url}")
    private String backendUrl;

    public static final String BACKEND_URL_AUTH = "/api/authentication/login";
    public static final String ABSOLUTE_PATH_VIDEOS = "src/main/resources/static/videos";
    public static final String ABSOLUTE_PATH_IMAGES = "src/main/resources/static/images";
    public static final String PATH_SAVE_IMAGES = "images/";
    public static final String PATH_SAVE_VIDEOS = "videos/";

    private static Supplier<String> environmentSupplier = () -> System.getenv("JWT_SECRET");

    public String getBackendUrl() {
        return backendUrl;
    }

    public static void setEnvironmentSupplier(Supplier<String> supplier) {
        environmentSupplier = supplier;
    }

    public static void resetEnvironmentSupplier() {
        environmentSupplier = () -> System.getenv("JWT_SECRET");
    }

    public static String getSecretKey() {
        String secretKey = environmentSupplier.get();
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("La variable de entorno JWT_SECRET no está configurada.");
        }
        return secretKey;
    }
}
