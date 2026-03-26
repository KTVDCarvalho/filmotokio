package filmotokio.repository;

import filmotokio.domain.Film;
import filmotokio.domain.Review;
import filmotokio.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Review repository for database operations
 * Handles review data persistence
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Check if a user has already reviewed a specific film
    boolean existsByUserAndFilm(User user, Film film);
    
    // Find all reviews for a specific film and include user data
    // Uses JOIN FETCH to load user and film data in a single query
    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.film WHERE r.film = :film")
    List<Review> findByFilmWithUser(@Param("film") Film film);
    
    // Delete all reviews for a film by its ID
    void deleteByFilmId(Long filmId);
    
    // Methods for REST API endpoints
    // Find all reviews written by a specific user
    List<Review> findByUserId(Long userId);
    
    // Check if a user has reviewed a specific film (using IDs)
    boolean existsByUserIdAndFilmId(Long userId, Long filmId);
}
