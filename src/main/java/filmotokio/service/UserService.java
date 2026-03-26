package filmotokio.service;

import filmotokio.domain.User;
import filmotokio.repository.UserRepository;
import filmotokio.util.ApplicationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;


/**
 * User service for registration and authentication
 * Manages user data operations
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Repository for user database operations
    @Autowired
    private UserRepository userRepository;

    // Encoder for hashing passwords securely
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    // Register a new user in the system
    public String registerUser(User user) {
        return registerUserWithRole(user, ApplicationConstants.USER_ROLE);
    }

    // Register a new admin in the system
    public String adminRegister(User user) {
        return registerUserWithRole(user, user.getRole() != null && !user.getRole().isEmpty() ? user.getRole() : ApplicationConstants.USER_ROLE);
    }

    // Common registration logic to avoid duplication
    private String registerUserWithRole(User user, String defaultRole) {
        String userType = ApplicationConstants.USER_ROLE.equals(defaultRole) ? "user" : "admin";
        logger.info("[UserService] - Attempting to register new {} - Username: {}, Email: {}", 
                   userType, user.getUsername(), user.getEmail());

        // Check if username is already taken
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("[UserService] - WARNING: Username already exists for {} - Username: {}", userType, user.getUsername());
            return ApplicationConstants.USERNAME_ALREADY_EXISTS;
        }

        // Check if email is already taken
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("[UserService] - WARNING: Email already exists for {} - Email: {}", userType, user.getEmail());
            return ApplicationConstants.EMAIL_ALREADY_EXISTS;
        }

        try {
            // Encode the password for filmotokio.security
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // Set the account creation date
            user.setCreationDate(LocalDate.now());
            // Activate the account by default
            user.setActive(true);
            // Set the user role
            user.setRole(user.getRole() != null && !user.getRole().isEmpty() ? user.getRole() : defaultRole);
            // Set default profile picture if none provided
            if (user.getImage() == null || user.getImage().trim().isEmpty()) {
                user.setImage("/images/default-avatar.svg");
            }

            User savedUser = userRepository.save(user);
            logger.info("[UserService] - {} created successfully - ID: {}, Username: {}", 
                       ApplicationConstants.USER_ROLE.equals(defaultRole) ? "User" : "Admin", savedUser.getId(), savedUser.getUsername());
            return null;
        } catch (Exception e) {
            logger.error("[UserService] - ERROR creating {} - Username: {}, Exception: {}", 
                        userType, user.getUsername(), e.getMessage(), e);
            String errorMessage = ApplicationConstants.USER_ROLE.equals(defaultRole) ? 
                ApplicationConstants.ERROR_CREATING_USER : 
                ApplicationConstants.ERROR_CREATING_ADMIN;
            return errorMessage;
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Transactional
    public User save(User user) {
        try {
            logger.info("[UserService] - Saving user - Username: {}, LastLoginDate: {}", 
                       user.getUsername(), user.getLastLoginDate());
            User savedUser = userRepository.save(user);
            logger.info("[UserService] - User saved successfully - ID: {}, LastLoginDate: {}", 
                       savedUser.getId(), savedUser.getLastLoginDate());
            return savedUser;
        } catch (Exception e) {
            logger.error("[UserService] - Error saving user - Username: {}, Exception: {}", 
                        user.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
