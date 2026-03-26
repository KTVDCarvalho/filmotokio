package filmotokio.repository;

import filmotokio.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User repository for database operations
 * Handles user data persistence
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // Find a user by their username
    Optional<User> findByUsername(String username);
    
    // Check if a user with the given username already exists
    boolean existsByUsername(String username);
    
    // Check if a user with the given email already exists
    boolean existsByEmail(String email);
}
