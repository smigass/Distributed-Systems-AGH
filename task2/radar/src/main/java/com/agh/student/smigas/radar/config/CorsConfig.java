package com.agh.student.smigas.radar.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/**") // Które ścieżki
                        .allowedOriginPatterns("http://localhost:[*]", "http://127.0.0.1:[*]")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Jakie metody
                        .allowedHeaders("*") // Jakie nagłówki (np. x-magicapi-key jeśli leci z frontu)
                        .allowCredentials(true);
            }
        };
    }
}
