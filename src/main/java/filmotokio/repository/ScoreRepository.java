package filmotokio.repository;

import filmotokio.domain.Film;
import filmotokio.domain.Score;
import filmotokio.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    // Search user's rating for a film
    Optional<Score> findByUserAndFilm(User user, Film film);

    // Search all ratings for a film
    List<Score> findByFilm(Film film);
    
    // Delete all ratings for a film by ID
    void deleteByFilmId(Long filmId);
}