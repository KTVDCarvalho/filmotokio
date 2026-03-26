package filmotokio.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

// Custom error controller
// Handles application error pages
@Controller
public class CustomErrorController implements ErrorController {

    // Handles all error requests and shows appropriate error pages
    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get the HTTP status code from the request
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        
        // Add status code and error details to the model
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("error", request.getAttribute("javax.servlet.error.exception"));
        
        // Show different error messages based on the status code
        if (statusCode != null) {
            if (statusCode == 403) {
                // Access denied error
                model.addAttribute("errorMessage", "Access denied. You don't have permission to access this page.");
            } else if (statusCode == 404) {
                // Page not found error
                model.addAttribute("errorMessage", "Page not found.");
            } else {
                // General error for other status codes
                model.addAttribute("errorMessage", "An unexpected error occurred.");
            }
        }
        
        // Return the error page template
        return "error";
    }
}
