package filmotokio.util;

/**
 * Constants used throughout the application
 * Centralizes common values to avoid duplication and magic strings
 */
public final class ApplicationConstants {
    
    // User roles
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    
    // Error messages
    public static final String USERNAME_ALREADY_EXISTS = "Username already in use.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already in use.";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String ERROR_CREATING_USER = "Error creating user. Please try again.";
    public static final String ERROR_CREATING_ADMIN = "Error creating administrator. Please try again.";
    
    // Success messages
    public static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
    public static final String ADMIN_CREATED_SUCCESSFULLY = "Admin created successfully";
    
    // Validation messages
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String EMAIL_REQUIRED = "Email is required";
    
    // Private constructor to prevent instantiation
    private ApplicationConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
