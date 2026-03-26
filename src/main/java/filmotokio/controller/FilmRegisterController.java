package filmotokio.controller;

import filmotokio.domain.Film;
import filmotokio.domain.Person;
import filmotokio.domain.TypePersonEnum;
import filmotokio.repository.PersonRepository;
import filmotokio.service.FilmRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This controller handles film registration functionality
 * Users and admins can create new films with associated people (actors, directors, etc.)
 */
@Controller
public class FilmRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(FilmRegisterController.class);

    // Service that handles film registration operations
    @Autowired
    private FilmRegisterService filmRegisterService;

    // Repository to access person data (actors, directors, etc.)
    @Autowired
    private PersonRepository personRepository;

    // Shows the form for creating a new film
    // Both users and admins can access this page
    @GetMapping("/film/new")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String newFilmForm(Model model) {
        // Create a new empty Film object to bind to the form
        model.addAttribute("film", new Film());

        // Load all people by their roles to populate form dropdowns
        Set<Person> screenwriters = new HashSet<>(personRepository.findByType(TypePersonEnum.SCREENWRITER));
        Set<Person> actors = new HashSet<>(personRepository.findByType(TypePersonEnum.ACTOR));
        Set<Person> musicians = new HashSet<>(personRepository.findByType(TypePersonEnum.MUSICIAN));
        Set<Person> directors = new HashSet<>(personRepository.findByType(TypePersonEnum.DIRECTOR));
        Set<Person> photographers = new HashSet<>(personRepository.findByType(TypePersonEnum.PHOTOGRAPHER));

        logger.info("[FilmRegisterController] - Access to new film form - User: {}", 
                   SecurityContextHolder.getContext().getAuthentication().getName());

        logger.info("[FilmRegisterController] - People loaded for form - Screenwriters: {}, Actors: {}, Musicians: {}, Directors: {}, Photographers: {}", 
                    screenwriters.size(), actors.size(), musicians.size(), directors.size(), photographers.size());

        model.addAttribute("screenwriters", screenwriters);
        model.addAttribute("actors", actors);
        model.addAttribute("musicians", musicians);
        model.addAttribute("directors", directors);
        model.addAttribute("photographers", photographers);

        return "new-film";
    }
    
    
    @PostMapping("/film/save")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional
    public String newFilm(
            @Valid @ModelAttribute Film film,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile posterFile,
            @RequestParam(required = false) List<Long> directors,
            @RequestParam(required = false) List<Long> screenwriters,
            @RequestParam(required = false) List<Long> actors,
            @RequestParam(required = false) List<Long> musicians,
            @RequestParam(required = false) List<Long> photographers,
            RedirectAttributes redirectAttrs
    ) {
        
        logger.info("[FilmRegisterController] - ===== SAVEFILM METHOD CALLED =====");
        logger.info("[FilmRegisterController] - Starting film creation process");
        logger.info("[FilmRegisterController] - Data received - Title: \"{}\", Year: {}", film.getTitle(), film.getYear());
        logger.info("[FilmRegisterController] - Number of people - Directors: {}, Screenwriters: {}, Actors: {}, Musicians: {}, Photographers: {}", 
                   directors != null ? directors.size() : 0,
                   screenwriters != null ? screenwriters.size() : 0, 
                   actors != null ? actors.size() : 0,
                   musicians != null ? musicians.size() : 0, 
                   photographers != null ? photographers.size() : 0);
        logger.info("[FilmRegisterController] - Validation errors: {}", bindingResult.hasErrors());

        if (bindingResult.hasErrors()) {
            logger.warn("[FilmRegisterController] - Validation errors found: {}", bindingResult.getAllErrors());
            redirectAttrs.addFlashAttribute("error", "Invalid film data");
            return "redirect:/film/new";
        }

        try {
            logger.info("[FilmRegisterController] - Starting film creation: {}", film.getTitle());
            logger.info("[FilmRegisterController] - Directors IDs: {}", directors);
            logger.info("[FilmRegisterController] - Screenwriters IDs: {}", screenwriters);
            logger.info("[FilmRegisterController] - Actors IDs: {}", actors);
            logger.info("[FilmRegisterController] - Musicians IDs: {}", musicians);
            logger.info("[FilmRegisterController] - Photographers IDs: {}", photographers);

            String errorMessage = filmRegisterService.registerFilmWithPersons(
                    film, posterFile, directors, screenwriters, actors, musicians, photographers);

            if (errorMessage != null) {
                logger.error("[FilmRegisterController] - Error creating film: {}", errorMessage);
                redirectAttrs.addFlashAttribute("error", errorMessage);
                return "redirect:/film/new";
            }

            logger.info("[FilmRegisterController] - Film saved successfully! ID: {}", film.getId());
            redirectAttrs.addFlashAttribute("message", "Film created successfully!");
            return "redirect:/film/new";

        } catch (Exception e) {
            logger.error("[FilmRegisterController] - Unexpected error creating film: ", e);
            redirectAttrs.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "redirect:/film/new";
        }
    }
}
