/*
 * © 2026 Kiniame Tarquinio Vieira Dias de Carvalho
 * Projeto: FILMOTOKIO
 */

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails loggedUser) {
        if (loggedUser != null) {
            model.addAttribute("user", loggedUser);
        }

        List<Film> films = filmRepository.findAll();

        // Calculate average rating for each film
        Map<Long, Double> filmAverages = new HashMap<>();
        for (Film film : films) {
            filmAverages.put(film.getId(), scoreService.getAverageScore(film));
        }

        model.addAttribute("films", films);
        model.addAttribute("filmAverages", filmAverages);

        return "home";
    }
}
