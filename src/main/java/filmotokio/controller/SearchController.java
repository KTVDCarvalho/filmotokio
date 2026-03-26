package filmotokio.controller;

import filmotokio.domain.Film;
import filmotokio.repository.FilmRepository;
import filmotokio.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/film")
@Controller
public class SearchController {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/search")
    public String searchForm(Model model, @AuthenticationPrincipal UserDetails loggedUser) {
        if (loggedUser != null) {
            model.addAttribute("user", loggedUser);
        }
        return "search-film";
    }

    @GetMapping("/search-results")
    public String search(@RequestParam(value = "title", required = false) String title, Model model, @AuthenticationPrincipal UserDetails loggedUser) {
        if (loggedUser != null) {
            model.addAttribute("user", loggedUser);
        }
        
        if (title == null || title.isBlank()) {
            model.addAttribute("films", List.of());
            model.addAttribute("searchTerm", "");
            model.addAttribute("message", "Please enter a search term");
            model.addAttribute("filmAverages", new HashMap<>());
            return "searched-film";
        }
        
        List<Film> films = filmRepository.findByTitleContainingIgnoreCase(title.trim());
        
        // Calculate average for each film
        Map<Long, Double> filmAverages = new HashMap<>();
        for (Film film : films) {
            filmAverages.put(film.getId(), scoreService.getAverageScore(film));
        }
        
        model.addAttribute("films", films);
        model.addAttribute("searchTerm", title);
        model.addAttribute("filmAverages", filmAverages);
        return "searched-film";
    }
}
