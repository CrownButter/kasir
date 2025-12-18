package com.dwincomputer.kasir.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Paths.get(uploadDir) akan otomatis menyesuaikan OS (Windows/Linux)
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String resourcePath = uploadPath.toUri().toString();

        registry.addResourceHandler("/api/images/**")
                .addResourceLocations(resourcePath);
    }
}