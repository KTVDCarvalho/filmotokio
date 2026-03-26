package filmotokio.controller;

import filmotokio.domain.Film;
import filmotokio.domain.Review;
import filmotokio.domain.User;
import filmotokio.repository.FilmRepository;
import filmotokio.repository.ReviewRepository;
import filmotokio.repository.UserRepository;
import filmotokio.service.FilmService;
import filmotokio.service.ReviewService;
import filmotokio.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/films")
public class ReviewFilmController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewFilmController.class);

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{id}")
    public String getFilm(@PathVariable("id") Long id,
                          @AuthenticationPrincipal UserDetails currentUser,
                          Model model) {

        logger.info("[ReviewFilmController] - Access to film page - ID: {}, User: {}", 
                   id, currentUser != null ? currentUser.getUsername() : "anonymous");

        Optional<Film> optionalFilm = filmRepository.findById(id);
        if (optionalFilm.isEmpty()) {
            logger.warn("[ReviewFilmController] - WARNING: Film not found - ID: {}", id);
            return "redirect:/";
        }

        Film film = optionalFilm.get();
        model.addAttribute("film", film);

        // AVERAGE
        Double averageScore = scoreService.getAverageScore(film);
        model.addAttribute("averageScore", averageScore);
        logger.debug("[ReviewFilmController] - Average calculated - Film ID: \"{}\", Average: {}", 
                    film.getTitle(), averageScore);

        // Reviews - Use efficient method to avoid N+1 queries
        List<Review> reviews = reviewRepository.findByFilmWithUser(film);
        model.addAttribute("reviews", reviews);
        logger.debug("[ReviewFilmController] - Reviews loaded - Film ID: \"{}\", Total: {}", 
                    film.getTitle(), reviews.size());

        boolean userHasReviewed = false;
        Integer userScore = 0;

        if (currentUser != null) {
            Optional<User> userOptional = userRepository.findByUsername(currentUser.getUsername());

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                userHasReviewed = reviewRepository.existsByUserAndFilm(user, film);

                Integer score = scoreService.getUserScore(film, user);
                if (score != null) {
                    userScore = score;
                }
            }
        }

        model.addAttribute("userHasReviewed", userHasReviewed);
        model.addAttribute("userScore", userScore);

        return "film";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFilm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("[ReviewFilmController] - Attempt to delete film - ID: {}, User: {}", 
                   id, SecurityContextHolder.getContext().getAuthentication().getName());
        
        try {
            filmService.deleteFilm(id);
            logger.info("[ReviewFilmController] - Film deleted successfully - ID: {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Film deleted successfully.");
            return "redirect:/";
        } catch (Exception e) {
            logger.error("[ReviewFilmController] - ERROR deleting film - ID: {}, Exception: {}", 
                        id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting the film.");
            return "redirect:/films/" + id;
        }
    }

    @PostMapping("/{id}/score")
    public String submitScore(@PathVariable("id") Long id,
                              @RequestParam("value") Integer value,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes redirectAttributes) {

        logger.info("[ReviewFilmController] - Score submission - Film ID: {}, User: {}, Value: {}", 
                   id, currentUser.getUsername(), value);

        if (currentUser == null) {
            logger.warn("[ReviewFilmController] - WARNING: Score attempt without logged user - Film ID: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "You need to be logged in to rate.");
            return "redirect:/login";
        }

        try {
            scoreService.submitScore(id, currentUser, value);
            logger.info("[ReviewFilmController] - Score submitted successfully - Film ID: {}, User: {}, Value: {}", 
                       id, currentUser.getUsername(), value);
            redirectAttributes.addFlashAttribute("successMessage", "Rating updated!");
            return "redirect:/films/" + id;
        } catch (Exception e) {
            logger.error("[ReviewFilmController] - ERROR submitting score - Film ID: {}, User: {}, Value: {}, Exception: {}", 
                        id, currentUser.getUsername(), value, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing rating.");
            return "redirect:/films/" + id;
        }
    }
    @PostMapping("/{id}/review")
    public String submitReview(@PathVariable("id") Long filmId,
                               @RequestParam("title") String title,
                               @RequestParam("textReview") String textReview,
                               @AuthenticationPrincipal UserDetails currentUser,
                               RedirectAttributes redirectAttributes) {

        logger.info("[ReviewFilmController] - Review submission - Film ID: {}, User: {}, Title: \"{}\"", 
                   filmId, currentUser.getUsername(), title);

        if (currentUser == null) {
            logger.warn("[ReviewFilmController] - WARNING: Review attempt without authenticated user - Film ID: {}", filmId);
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "You need to be authenticated to write a review."
            );
            return "redirect:/login";
        }

        try {
            boolean reviewCreated = reviewService.submitReview(filmId, currentUser, title, textReview);

            if (!reviewCreated) {
                logger.warn("[ReviewFilmController] - WARNING: Review not created - User already made review - Film ID: {}, User: {}", 
                           filmId, currentUser.getUsername());
                redirectAttributes.addFlashAttribute("errorMessage", "You have already made a review for this film.");
            } else {
                logger.info("[ReviewFilmController] - Review created successfully - Film ID: {}, User: {}", 
                           filmId, currentUser.getUsername());
                redirectAttributes.addFlashAttribute("successMessage", "Review saved successfully!");
            }
        } catch (Exception e) {
            logger.error("[ReviewFilmController] - ERROR creating review - Film ID: {}, User: {}, Exception: {}", 
                        filmId, currentUser.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing review.");
        }

        return "redirect:/films/" + filmId;
    }
}
