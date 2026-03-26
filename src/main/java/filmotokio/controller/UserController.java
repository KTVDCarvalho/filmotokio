/*
 * © 2026 Kiniame Tarquinio Vieira Dias de Carvalho
 * Projeto: FILMOTOKIO
 */

package filmotokio.controller;

import filmotokio.domain.User;
import filmotokio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        if (error != null) model.addAttribute("error", "Invalid username or password");
        if (logout != null) model.addAttribute("message", "Logout successful");

        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               RedirectAttributes redirectAttributes) {

        String errorMessage = userService.registerUser(user);

        if (errorMessage != null) {
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("message", "Account created successfully! Please login.");
        return "redirect:/login";
    }

}
