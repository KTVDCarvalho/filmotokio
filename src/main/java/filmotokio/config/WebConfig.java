package filmotokio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class
 * Sets up CORS and static resource handling
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Configure Cross-Origin Resource Sharing (CORS) settings
    // CORS allows cross-domain requests
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply CORS settings to all API endpoints
        registry.addMapping("/api/**")
                // Allow requests from these specific origins
                .allowedOrigins("http://localhost:8080", "http://localhost:8080/swagger-ui/index.html")
                // Allow these HTTP methods
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Allow all headers
                .allowedHeaders("*")
                // Allow cookies and authentication
                .allowCredentials(true);
    }

    // Configure static resource handlers for serving uploaded files
    // Serves files from uploads directory
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the /uploads/** URL pattern to serve files from the uploads directory
        registry
                .addResourceHandler("/uploads/**")
                // Set the physical location where files are stored
                .addResourceLocations("file:./uploads/");
    }
}


