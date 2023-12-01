package com.example.simulateurmarche.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**") // Allow CORS for all endpoints
                            .allowedOrigins("http://localhost:4200") // Allowed origin
                            .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                            .allowedHeaders("*") // Allowed headers
                            .allowCredentials(true); // Allowing credentials (cookies, etc.)
                    // Configuration for /api-auth/**


                }
            };
        }

}
