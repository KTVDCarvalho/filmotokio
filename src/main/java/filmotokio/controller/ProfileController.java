package filmotokio.controller;

import filmotokio.domain.User;
import filmotokio.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User profile management controller
 * Handles profile viewing and picture uploads
 */
@Controller
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    // Directory where uploaded profile pictures are stored
    private static final String UPLOAD_DIR = "uploads/";

    // Service for handling user-related operations
    @Autowired
    private UserService userService;

    /**
     * Displays the user's profile page
     * Automatically updates the last login date if it's null
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        if (currentUser != null) {
            logger.info("[ProfileController] - Accessing user profile: {}", currentUser.getUsername());
            User user = userService.findByUsername(currentUser.getUsername());
            
            // Auto-update last login date when accessing profile
            if (user.getLastLoginDate() == null) {
                logger.info("[ProfileController] - Updating null lastLoginDate for user: {}", user.getUsername());
                user.setLastLoginDate(LocalDateTime.now());
                userService.save(user);
                logger.info("[ProfileController] - LastLoginDate updated: {}", user.getLastLoginDate());
            }
            
            // Add user information to the model for the template
            logger.info("[ProfileController] - User found - ID: {}, LastLoginDate: {}, Username: {}", 
                       user.getId(), user.getLastLoginDate(), user.getUsername());
            model.addAttribute("user", user);
            return "profile";
        }
        // Redirect to login if no authenticated user
        return "redirect:/login";
    }

    /**
     * Handles profile picture upload functionality
     * Validates file type, creates unique filename, and saves to upload directory
     */
    @PostMapping("/profile/upload-photo")
    public String uploadProfilePhoto(@AuthenticationPrincipal UserDetails currentUser,
                                   @RequestParam("photo") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        // Redirect to login if user is not authenticated
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(currentUser.getUsername());
            
            // Check if file is selected
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a photo.");
                return "redirect:/profile";
            }

            // Validate that the uploaded file is an image
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                redirectAttributes.addFlashAttribute("error", "Only image files are allowed.");
                return "redirect:/profile";
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String uniqueFilename = "profile_" + user.getId() + "_" + UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            // Update user profile
            user.setImage("/" + filePath.toString().replace("\\", "/"));
            userService.save(user);

            logger.info("[ProfileController] - Profile photo updated successfully - User: {}, File: {}", 
                       user.getUsername(), uniqueFilename);
            
            redirectAttributes.addFlashAttribute("success", "Profile photo updated successfully!");
            
        } catch (IOException e) {
            logger.error("[ProfileController] - Error uploading photo - User: {}, Error: {}", 
                        currentUser.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error uploading photo. Please try again.");
        } catch (Exception e) {
            logger.error("[ProfileController] - Unexpected error uploading - User: {}, Error: {}", 
                        currentUser.getUsername(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Unexpected error. Please try again.");
        }

        return "redirect:/profile";
    }
}
