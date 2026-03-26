package filmotokio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 * Sets up API documentation and security schemes
 */
@Configuration
public class OpenApiConfig {

    // Creates and configures the OpenAPI documentation
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Set up the basic API information
                .info(new Info()
                        .title("FILMOTOKIO API REST")
                        .version("1.0.0")
                        .description("JWT authentication and film review management REST API")
                        // Add contact information
                        .contact(new Contact()
                                .name("Kiniame Carvalho")
                                .email("contact@filmtokio.com")))
                // Configure the server URLs
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server")))
                // Set up filmotokio.security configuration for JWT authentication
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("JWT token in format: Bearer <token>")))
                // Apply the filmotokio.security requirement to all endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
