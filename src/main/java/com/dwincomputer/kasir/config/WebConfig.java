package com.dwincomputer.kasir.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // IMPORT INI YANG KURANG
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Mengizinkan Frontend mengakses API Backend di Server
        registry.addMapping("/**")
                .allowedOrigins("https://demokasir.dwincomputer.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mendeteksi path folder uploads secara otomatis (Windows/Linux/Docker)
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String resourcePath = uploadPath.toUri().toString();

        registry.addResourceHandler("/api/images/**")
                .addResourceLocations(resourcePath);
    }
}