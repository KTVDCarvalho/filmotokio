package filmotokio.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User entity for authentication and profiles
 * Handles registration, login, and reviews
 */
@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    // Unique identifier for each user in the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username for login - unique, 3-50 characters
    @Column(name = "username", nullable = false, unique = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    // User's password - minimum 6 characters
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // User's first name - required
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    // User's last name/surname - required
    @Column(name = "surname", nullable = false)
    @NotBlank(message = "Surname is required")
    @Size(max = 100, message = "Surname must be at most 100 characters")
    private String surname;

    // User's email address - unique, valid format
    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    // URL or path to user's profile picture
    @Column(name = "image")
    private String image;

    // User's date of birth - must be in past
    @Column(name = "birthDate", nullable = false)
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // Account creation date
    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate;

    // Last login timestamp
    @Column(name = "lastLoginDate", nullable = true)
    private LocalDateTime lastLoginDate;

    // Account status (active/disabled)
    @Column(name = "active", nullable = false)
    private boolean active;

    // User's system role (admin, user, etc.)
    @Column(name = "role", nullable = false)
    private String role;
}
