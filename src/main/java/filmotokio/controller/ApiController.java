package filmotokio.controller;

import filmotokio.dto.JwtRequest;
import filmotokio.dto.JwtResponse;
import filmotokio.dto.ReviewDto;
import filmotokio.domain.User;
import filmotokio.security.JwtTokenUtil;
import filmotokio.service.UserService;
import filmotokio.service.ReviewService;
import filmotokio.service.MigrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * REST API controller
 * Manages JWT authentication, reviews, and migration
 */
@RestController
@RequestMapping("/api")
@Tag(name = "API REST", description = "Endpoints for JWT authentication and review management")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    // Handles user authentication (login/logout)
    @Autowired
    private AuthenticationManager authenticationManager;

    // Utility for generating and validating JWT tokens
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Service for user-related operations
    @Autowired
    private UserService userService;

    // Service for review-related operations
    @Autowired
    private ReviewService reviewService;

    // Service for film migration operations
    @Autowired
    private MigrationService migrationService;

    // Endpoint for user login - returns JWT token if credentials are valid
    @PostMapping("/auth/login")
    @Operation(summary = "JWT Login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirements() // No filmotokio.security required for login
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {
        logger.info("[ApiController] - Login attempt - User: {}", authenticationRequest.getUsername());

        try {
            // Authenticate user with credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), 
                    authenticationRequest.getPassword())
            );

            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            
            // Update user's last login date
            user.setLastLoginDate(LocalDateTime.now());
            logger.info("[ApiController] - Updating lastLoginDate for user: {} - Date: {}", 
                       user.getUsername(), user.getLastLoginDate());
            userService.save(user);
            
            // Generate JWT token for user
            String token = jwtTokenUtil.generateToken(userDetails);

            // Create response with token and user information
            JwtResponse response = new JwtResponse(token, user.getId(), user.getUsername(), 
                user.getEmail(), user.getRole());

            logger.info("[ApiController] - Login successful - User: {}", authenticationRequest.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle login failure
            logger.error("[ApiController] - Login failed - User: {}, Error: {}, Stack: {}", 
                authenticationRequest.getUsername(), e.getClass().getSimpleName(), e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            errorResponse.put("message", "Incorrect username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Endpoint for creating a new film review
    @PostMapping("/new-review")
    @Operation(summary = "Create review", description = "Creates a new review for a film")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "409", description = "User already has a review for this film"),
        @ApiResponse(responseCode = "404", description = "Film not found")
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDto reviewDto, 
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        // Check if the authorization token is provided
        if (token == null || !token.startsWith("Bearer ")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token not provided");
            errorResponse.put("message", "Authorization header is required in format: Bearer <token>");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        // Handle duplicate "Bearer Bearer" issue in token
        String cleanToken = token.replaceFirst("^Bearer\\s+", "");
        if (cleanToken.startsWith("Bearer ")) {
            cleanToken = cleanToken.substring(7); // Remove second "Bearer "
        }
        
        // Extract username from the JWT token
        String username = jwtTokenUtil.getUsernameFromToken(cleanToken);
        logger.info("[ApiController] - Creating review - User: {}, Film: {}", username, reviewDto.getFilmId());

        try {
            // Try to create the review using the filmotokio.service
            ReviewDto createdReview = reviewService.createReviewDto(reviewDto, username);
            logger.info("[ApiController] - Review created successfully - ID: {}", createdReview.getId());
            return ResponseEntity.ok(createdReview);

        } catch (RuntimeException e) {
            // Handle review creation errors
            logger.warn("[ApiController] - Error creating review - User: {}, Reason: {}", username, e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error creating review");
            errorResponse.put("message", e.getMessage());
            
            // Return appropriate HTTP status based on error type
            if (e.getMessage().contains("already has a review")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            } else if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }
    }

    // Endpoint to get all reviews written by a specific user
    @GetMapping("/user/{id}/reviews")
    @Operation(summary = "List reviews by user", description = "Returns all reviews from a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews found successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUserReviews(@PathVariable Long id) {
        logger.info("[ApiController] - Searching user reviews - ID: {}", id);

        try {
            // Get all user reviews
            List<ReviewDto> reviews = reviewService.getReviewsByUserId(id);
            logger.info("[ApiController] - Found {} reviews for user {}", reviews.size(), id);
            return ResponseEntity.ok(reviews);

        } catch (RuntimeException e) {
            // Handle case where user is not found
            logger.warn("[ApiController] - User not found - ID: {}", id);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "No user found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Endpoint to start the film migration process
    @PostMapping("/migration/start")
    @Operation(summary = "Start film migration", description = "Starts the film migration to CSV")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Migration started successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Error starting migration")
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> startMigration() {
        logger.info("[ApiController] - Starting film migration");
        
        try {
            // Start the migration process
            MigrationService.MigrationResult result = migrationService.migrateFilmsToCsv();
            
            // Create response with migration results
            Map<String, Object> response = new HashMap<>();
            response.put("message", result.getMessage());
            response.put("filmsMigrated", result.getFilmsMigrated());
            response.put("success", result.isSuccess());
            response.put("status", result.isSuccess() ? "COMPLETED" : "FAILED");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Handle errors during migration
            logger.error("[ApiController] - Error starting migration: {}", e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error starting migration");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Endpoint to check the current migration status
    @GetMapping("/migration/status")
    @Operation(summary = "Check migration status", description = "Checks if there are films to migrate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status checked successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> checkMigrationStatus() {
        logger.info("[ApiController] - Checking migration status");
        
        try {
            // Get current migration status from filmotokio.service
            Map<String, Object> status = migrationService.getMigrationStatus();
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            // Handle errors while checking status
            logger.error("[ApiController] - Error checking migration status: {}", e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error checking migration status");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    
    
    // Global exception handler for unexpected errors in this controller
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        logger.error("[ApiController] - Unexpected error: {}", e.getMessage(), e);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal server error");
        errorResponse.put("message", "An unexpected error occurred. Please try again later.");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
