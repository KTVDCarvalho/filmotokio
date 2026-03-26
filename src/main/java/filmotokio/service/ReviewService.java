package filmotokio.service;

import filmotokio.domain.Film;
import filmotokio.domain.Review;
import filmotokio.domain.User;
import filmotokio.dto.ReviewDto;
import filmotokio.repository.FilmRepository;
import filmotokio.repository.ReviewRepository;
import filmotokio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This filmotokio.service handles review-related business logic
 * It manages review creation, validation, and data operations
 */
@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    // Repository for film database operations
    @Autowired
    private FilmRepository filmRepository;

    // Repository for user database operations
    @Autowired
    private UserRepository userRepository;

    // Repository for review database operations
    @Autowired
    private ReviewRepository reviewRepository;

    // Submit a new review for a film
    public boolean submitReview(Long filmId,
                                UserDetails currentUser,
                                String title,
                                String textReview) {

        logger.info("[ReviewService] - Attempting to submit review - Film ID: {}, User: {}, Title: \"{}\"", 
                   filmId, currentUser.getUsername(), title);

        // Input validations
        if (title == null || title.trim().isEmpty()) {
            logger.warn("[ReviewService] - WARNING: Review title is required - User: {}", currentUser.getUsername());
            throw new IllegalArgumentException("Review title is required");
        }
        
        if (textReview == null || textReview.trim().isEmpty()) {
            logger.warn("[ReviewService] - WARNING: Review text is required - User: {}", currentUser.getUsername());
            throw new IllegalArgumentException("Review text is required");
        }
        
        if (title.length() > 255) {
            logger.warn("[ReviewService] - WARNING: Title too long - User: {}, Size: {}, Max: 255", 
                       currentUser.getUsername(), title.length());
            throw new IllegalArgumentException("Title too long (maximum 255 characters)");
        }

        try {
            Film film = filmRepository.findById(filmId)
                    .orElseThrow(() -> new RuntimeException("Film not found"));

            User user = userRepository.findByUsername(currentUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (reviewRepository.existsByUserAndFilm(user, film)) {
                logger.warn("[ReviewService] - WARNING: User already reviewed this film - User: {}, Film ID: {}", 
                           user.getUsername(), filmId);
                return false;
            }

            logger.debug("[ReviewService] - Creating new review - User: {}, Film ID: \"{}\"", 
                        user.getUsername(), film.getTitle());

            Review review = new Review();
            review.setFilm(film);
            review.setUser(user);
            review.setTitle(title);
            review.setTextReview(textReview);
            review.setDate(LocalDate.now());

            Review savedReview = reviewRepository.save(review);
            logger.info("[ReviewService] - Review created successfully - ID: {}, User: {}, Film ID: \"{}\"", 
                       savedReview.getId(), user.getUsername(), film.getTitle());
            
            return true;

        } catch (RuntimeException e) {
            logger.error("[ReviewService] - ERROR creating review - Film ID: {}, User: {}, Exception: {}", 
                        filmId, currentUser.getUsername(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("[ReviewService] - Unexpected error creating review - Film ID: {}, User: {}, Exception: {}", 
                        filmId, currentUser.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Error processing review", e);
        }
    }

    // Methods for REST API
    @Transactional
    public ReviewDto createReviewDto(ReviewDto reviewDto, String username) {
        logger.info("[ReviewService] - Creating new review via API - User: {}, Film: {}", username, reviewDto.getFilmId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Check if user already has a review for this film
        boolean hasExistingReview = reviewRepository.existsByUserIdAndFilmId(user.getId(), reviewDto.getFilmId());
        if (hasExistingReview) {
            throw new RuntimeException("User already has a review for this film");
        }

        // Check if film exists
        if (!filmRepository.existsById(reviewDto.getFilmId())) {
            throw new RuntimeException("Film not found with ID: " + reviewDto.getFilmId());
        }

        Review review = new Review();
        review.setTitle(reviewDto.getTitle());
        review.setTextReview(reviewDto.getTextReview());
        review.setUser(user);
        review.setFilm(filmRepository.findById(reviewDto.getFilmId()).get());
        review.setDate(LocalDate.now());

        Review savedReview = reviewRepository.save(review);
        logger.info("[ReviewService] - Review created successfully via API - ID: {}", savedReview.getId());

        return convertToDto(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByUserId(Long userId) {
        logger.info("[ReviewService] - Getting user reviews via API - ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Review> reviews = reviewRepository.findByUserId(userId);
        
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setFilmId(review.getFilm().getId());
        dto.setTitle(review.getTitle());
        dto.setTextReview(review.getTextReview());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setReviewDate(review.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return dto;
    }
}
