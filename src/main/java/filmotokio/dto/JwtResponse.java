package filmotokio.dto;

import lombok.Data;

// JWT authentication response
// JWT response with token and user data
@Data
public class JwtResponse {
    // JWT token for authentication
    private String token;
    // Token type (always "Bearer" for JWT)
    private String type = "Bearer";
    // User's unique ID
    private Long id;
    // User's username
    private String username;
    // User's email address
    private String email;
    // User's role (USER or ADMIN)
    private String role;

    // Constructor to create a response with all user information
    public JwtResponse(String token, Long id, String username, String email, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
