package filmotokio.repository;

import filmotokio.domain.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Film repository for database operations
 * Handles film data persistence
 */
@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    // Find a film by its exact title
    Optional<Film> findByTitle(String title);

    // Search for films by keyword in title (case-insensitive)
    // Returns all films whose titles contain the given keyword
    @Query("SELECT f FROM Film f WHERE LOWER(f.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Film> findByTitleContainingIgnoreCase(@Param("title") String title);
}
