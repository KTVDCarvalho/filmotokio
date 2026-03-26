package filmotokio.service;

import filmotokio.domain.Film;
import filmotokio.repository.FilmMigrationRepository;
import filmotokio.repository.FilmRepository;
import filmotokio.repository.ScoreRepository;
import filmotokio.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filmotokio.service handles film-related business logic
 * It manages film operations like deletion and data management
 */
@Service
public class FilmService {

    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);

    // Repository for film database operations
    @Autowired
    private FilmRepository filmRepository;

    // Repository for score database operations
    @Autowired
    private ScoreRepository scoreRepository;

    // Repository for review database operations
    @Autowired
    private ReviewRepository reviewRepository;

    // Repository for film migration operations
    @Autowired
    private FilmMigrationRepository filmMigrationRepository;

    // Delete a film and all its related data
    @Transactional
    public void deleteFilm(Long id) {
        logger.info("[FilmService] - Attempting to delete film - ID: {}", id);

        try {
            // Find the film by ID or throw exception if not found
            Film film = filmRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Film not found"));

            logger.info("[FilmService] - Film found - ID: {}, Title: \"{}\"", id, film.getTitle());

            // Clear relationships to avoid constraint violations
            logger.debug("[FilmService] - Clearing film relationships - ID: {}", id);
            
            // Delete migration records related to this film
            filmMigrationRepository.deleteByFilmId(id);
            logger.debug("[FilmService] - Migration records deleted - Film ID: {}", id);
            
            // Delete all scores for this film
            scoreRepository.deleteByFilmId(id);
            logger.debug("[FilmService] - Scores deleted - Film ID: {}", id);
            
            // Delete related reviews
            reviewRepository.deleteByFilmId(id);
            logger.debug("[FilmService] - Reviews deleted - Film ID: {}", id);
            
            // Clear relationships with people
            film.getActors().clear();
            film.getDirectors().clear();
            film.getScreenwriters().clear();
            film.getMusicians().clear();
            film.getPhotographers().clear();

            filmRepository.save(film);
            filmRepository.delete(film);

            logger.info("[FilmService] - Film deleted successfully - ID: {}, Title: \"{}\"", id, film.getTitle());
        } catch (RuntimeException e) {
            logger.error("[FilmService] - ERROR deleting film - ID: {}, Exception: {}", 
                        id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("[FilmService] - Unexpected error deleting film - ID: {}, Exception: {}", 
                        id, e.getMessage(), e);
            throw new RuntimeException("Error deleting film", e);
        }
    }
}
