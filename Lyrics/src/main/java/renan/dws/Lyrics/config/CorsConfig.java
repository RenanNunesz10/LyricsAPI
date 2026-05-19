package renan.dws.Lyrics.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a todos os endpoints da API
                .allowedOrigins("http://localhost:3000", "http://localhost:5173") // Origens permitidas (ex: React, Vue)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600); // Tempo de cache das configurações de CORS em segundos (1 hora)
    }
}