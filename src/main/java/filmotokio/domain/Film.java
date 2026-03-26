package filmotokio.domain;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film entity for the movie database
 * Stores film information like title, year, duration, and synopsis
 */
@Entity
@Table(name = "films")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    // Unique identifier for each film in the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Film title - required
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Column(nullable = false)
    private String title;

    // Release year - 1888 to 2100
    @Min(value = 1888, message = "Year must be 1888 or higher")
    @Max(value = 2100, message = "Year must be 2100 or lower")
    @Column(name = "release_year")
    private int year;
    
    // Film duration in minutes - 1 to 600
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration cannot exceed 600 minutes")
    private int duration;

    // Film summary
    @Column
    private String synopsis;

    // Poster image URL/path
    private String poster;
    
    // Migration flag from other system
    private Boolean migrate;
    
    // Migration date to our system
    private LocalDate migrationDate;

    // One film has many reviews (cascade delete)
    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // Film-screenwriter many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "film_screenwriters",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"film_id", "person_id"})
    )
    private Set<Person> screenwriters = new HashSet<>();

    // Film-actor many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "film_actors",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"film_id", "person_id"})
    )
    private Set<Person> actors = new HashSet<>();

    // Film-musician many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "film_musicians",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"film_id", "person_id"})
    )
    private Set<Person> musicians = new HashSet<>();

    // Film-director many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "film_directors",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"film_id", "person_id"})
    )
    private Set<Person> directors = new HashSet<>();

    // Film-photographer many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "film_photographers",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"film_id", "person_id"})
    )
    private Set<Person> photographers = new HashSet<>();
}
