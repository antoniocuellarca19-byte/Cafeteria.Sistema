package com.cafeteria.sistema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SistemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaApplication.class, args);
	}

    // ESTA ES LA CONFIGURACIÓN MAESTRA DE SEGURIDAD
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica a TODAS las URLs
                        .allowedOriginPatterns("*") // ¡Permite CUALQUIER origen! (Más potente que localhost)
                        .allowedMethods("*") // Permite GET, POST, PUT, DELETE
                        .allowedHeaders("*") // Permite cualquier cabecera
                        .allowCredentials(true);
            }
        };
    }
}