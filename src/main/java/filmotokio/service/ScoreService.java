package filmotokio.service;

import filmotokio.domain.Film;
import filmotokio.domain.Score;
import filmotokio.domain.User;
import filmotokio.repository.FilmRepository;
import filmotokio.repository.ScoreRepository;
import filmotokio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ScoreService {

    private static final Logger logger = LoggerFactory.getLogger(ScoreService.class);

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Transactional
    public boolean submitScore(Long filmId, UserDetails currentUser, Integer value) {

        logger.info("[ScoreService] - Starting submitScore - Film ID: {}, User: {}, Value: {}", 
                   filmId, currentUser.getUsername(), value);

        // Validate score
        if (value == null || value < 1 || value > 5) {
            logger.warn("[ScoreService] - WARNING: Invalid score - Value: {}", value);
            throw new IllegalArgumentException("Invalid score");
        }

        try {
            logger.debug("[ScoreService] - Searching film with ID: {}", filmId);
            Film film = filmRepository.findById(filmId)
                    .orElseThrow(() -> {
                        logger.error("[ScoreService] - Film not found - ID: {}", filmId);
                        return new RuntimeException("Film not found");
                    });

            logger.debug("[ScoreService] - Searching user: {}", currentUser.getUsername());
            User user = userRepository.findByUsername(currentUser.getUsername())
                    .orElseThrow(() -> {
                        logger.error("[ScoreService] - Authenticated user not found in database - Username: {}", 
                                   currentUser.getUsername());
                        logger.error("[ScoreService] - UserDetails class: {}", currentUser.getClass().getName());
                        logger.error("[ScoreService] - UserDetails authorities: {}", 
                                   currentUser.getAuthorities().stream()
                                           .map(auth -> auth.getAuthority())
                                           .toList());
                        return new RuntimeException("User not found in database. Please try to login again.");
                    });

            logger.info("[ScoreService] - User found - ID: {}, Username: {}, Role: {}", 
                       user.getId(), user.getUsername(), user.getRole());

            // Check existing rating
            Score score = scoreRepository.findByUserAndFilm(user, film).orElse(null);

            if (score != null) {
                // UPDATE existing rating
                logger.debug("[ScoreService] - Updating existing rating - User: {}, Film ID: {}, Previous value: {}, New value: {}", 
                            user.getUsername(), filmId, score.getValue(), value);
                score.setValue(value);
                logger.info("[ScoreService] - Rating updated successfully - User: {}, Film ID: {}, New value: {}", 
                           user.getUsername(), filmId, value);
            } else {
                // CREATE new rating
                logger.debug("[ScoreService] - Creating new rating - User: {}, Film ID: {}, Value: {}", 
                            user.getUsername(), filmId, value);
                score = new Score();
                score.setFilm(film);
                score.setUser(user);
                score.setValue(value);
                logger.info("[ScoreService] - New rating created successfully - User: {}, Film ID: {}, Value: {}", 
                           user.getUsername(), filmId, value);
            }

            scoreRepository.save(score);
            logger.info("[ScoreService] - Rating saved successfully to database");
            return true;

        } catch (RuntimeException e) {
            logger.error("[ScoreService] - ERROR submitting rating - Film ID: {}, User: {}, Value: {}, Exception: {}", 
                        filmId, currentUser.getUsername(), value, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("[ScoreService] - Unexpected error submitting rating - Film ID: {}, User: {}, Value: {}, Exception: {}", 
                        filmId, currentUser.getUsername(), value, e.getMessage(), e);
            throw new RuntimeException("Error processing rating", e);
        }
    }

    public Integer getUserScore(Film film, User user) {
        logger.debug("[ScoreService] - Searching user rating - User: {}, Film ID: {}", 
                    user.getUsername(), film.getId());
        return scoreRepository.findByUserAndFilm(user, film)
                .map(score -> {
                    logger.debug("[ScoreService] - Rating found - User: {}, Film ID: {}, Value: {}", 
                               user.getUsername(), film.getId(), score.getValue());
                    return score.getValue();
                })
                .orElseGet(() -> {
                    logger.debug("[ScoreService] - Rating not found - User: {}, Film ID: {}", 
                               user.getUsername(), film.getId());
                    return null;
                });
    }

    // Calculate the average rating of a film
    public Double getAverageScore(Film film) {
        logger.debug("[ScoreService] - Calculating rating average - Film ID: {}, Title: \"{}\"", 
                    film.getId(), film.getTitle());
        
        List<Score> scores = scoreRepository.findByFilm(film);

        if (scores.isEmpty()) {
            logger.debug("[ScoreService] - No ratings found - Film ID: {}", film.getId());
            return 0.0;
        }

        double sum = scores.stream().mapToInt(Score::getValue).sum();
        double average = Math.round((sum / scores.size()) * 10.0) / 10.0;
        
        logger.info("[ScoreService] - Average calculated - Film ID: {}, Title: \"{}\", Average: {}, Total ratings: {}", 
                   film.getId(), film.getTitle(), average, scores.size());
        
        return average;
    }
}