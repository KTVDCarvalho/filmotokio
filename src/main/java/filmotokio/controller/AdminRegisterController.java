package filmotokio.controller;

import filmotokio.domain.User;
import filmotokio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;

/**
 * Admin user registration controller
 * Only accessible by users with ADMIN role
 */
@Controller
@RequestMapping("/admin")
public class AdminRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRegisterController.class);

    // Service that handles user-related operations
    @Autowired
    private UserService userService;

    // Shows the form for creating a new user
    // Only admins can access this page
    @GetMapping("/user/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateUserForm(Model model) {
        // Get current admin username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("[AdminRegisterController] - Access to user creation form - User: {}", username);
        
        // Create empty User for form binding
        model.addAttribute("user", new User());
        return "admin-register";
    }

    // Handles the submission of the user creation form
    // Only admins can create new users
    @PostMapping("/user/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        logger.info("[AdminRegisterController] - Attempting to create new user - Admin user: {}, New user: {}", 
                   SecurityContextHolder.getContext().getAuthentication().getName(), user.getUsername());

        // Debug received data
        logger.info("[AdminRegisterController] - Received data - Name: {}, Surname: {}, Username: {}, Email: {}, Password: {}, BirthDate: {}", 
                   user.getName(), user.getSurname(), user.getUsername(), user.getEmail(), user.getPassword(), user.getBirthDate());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("[AdminRegisterController] - Validation errors: {}", bindingResult.getAllErrors());
            logger.warn("[AdminRegisterController] - Number of errors: {}", bindingResult.getErrorCount());
            
            // Add error info to model
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("errorMessage", "Invalid data. Please check the fields and try again.");
            model.addAttribute("user", user);
            return "admin-register";
        }

        // Create user using service
        String errorMessage = userService.adminRegister(user);

        // Handle user creation error
        if (errorMessage != null) {
            logger.warn("[AdminRegisterController] - Failed to create user - Username: {}, Reason: {}", 
                       user.getUsername(), errorMessage);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("user", user);
            return "admin-register";
        }

        // User was created successfully
        logger.info("[AdminRegisterController] - User created successfully - Username: {}", user.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully!");
        return "redirect:/admin/user/new";
    }
}