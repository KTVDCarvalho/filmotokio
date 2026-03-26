package filmotokio.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

// JWT authentication request
// JWT request with username and password
@Data
public class JwtRequest {
    // Username for authentication - required
    @NotBlank(message = "Username is required")
    private String username;
    
    // Password for authentication - required
    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor
    public JwtRequest() {}

    // Constructor with parameters
    public JwtRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
