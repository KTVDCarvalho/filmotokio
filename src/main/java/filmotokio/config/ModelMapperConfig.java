package filmotokio.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ModelMapper configuration for object mapping
// Converts between object types
@Configuration
public class ModelMapperConfig {

    // Creates and configures the ModelMapper bean
    @Bean
    public ModelMapper modelMapper() {
        // Return a new ModelMapper instance
        return new ModelMapper();
    }
}
