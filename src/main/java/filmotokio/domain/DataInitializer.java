package filmotokio.domain;

import filmotokio.repository.FilmRepository;
import filmotokio.repository.PersonRepository;
import filmotokio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

// Database initialization component
// Creates default users and sample data on startup
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    // Repository for user database operations
    private final UserRepository userRepository;
    // Encoder for hashing passwords
    private final BCryptPasswordEncoder passwordEncoder;

    // Repository for film database operations
    @Autowired
    private FilmRepository filmRepository;

    // Repository for person database operations
    @Autowired
    private PersonRepository personRepository;

    // Constructor to inject required dependencies
    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Runs on application startup
    // Initializes database with default data
    @Override
    public void run(String... args) throws Exception {
        logger.info("[DataInitializer] - Initializing default system data");

        String defaultUsername = "tokioschool";
        String defaultPassword = "Tokioschool";
        String defaultEmail = "tokioschool@email.com";

        // Check for case-insensitive username existence
        boolean exists = userRepository.existsByEmail(defaultEmail)
                || userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(defaultUsername));

        if (!exists) {
            logger.info("[DataInitializer] - Creating default administrator user - Username: {}, Email: {}", 
                       defaultUsername, defaultEmail);

            User user = new User();
            user.setUsername(defaultUsername);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setEmail(defaultEmail);
            user.setName("Tokio");
            user.setSurname("School");
            user.setRole("ADMIN");
            user.setActive(true);
            user.setCreationDate(LocalDate.now());
            user.setBirthDate(LocalDate.of(2000, 1, 1));
            user.setImage("/uploads/profile.jpg");

            User savedUser = userRepository.save(user);
            logger.info("[DataInitializer] - Administrator user created successfully - ID: {}, Username: {}, Photo: {}", 
                       savedUser.getId(), savedUser.getUsername(), savedUser.getImage());
        } else {
            logger.info("[DataInitializer] - Default administrator user already exists - Username: {}", defaultUsername);
            
            // Update existing user with profile photo if not set
            Optional<User> existingUser = userRepository.findByUsername(defaultUsername);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                if (user.getImage() == null || user.getImage().isEmpty()) {
                    user.setImage("/uploads/profile.jpg");
                    userRepository.save(user);
                    logger.info("[DataInitializer] - Profile photo set for existing user - Username: {}, Photo: {}", 
                               user.getUsername(), user.getImage());
                }
            }
        }

        // Create default films if they don't exist
        createDefaultFilms();

        logger.info("[DataInitializer] - Data initialization completed");
    }

    private void createDefaultFilms() {
        logger.info("[DataInitializer] - Checking default films");

        List<String> defaultFilmTitles = Arrays.asList(
            "Interstellar",
            "Dark", 
            "From",
            "The Shawshank Redemption"
        );

        long existingFilmsCount = filmRepository.count();
        logger.info("[DataInitializer] - Existing films: {}", existingFilmsCount);

        // Always create/update default films to ensure they have correct poster paths
        logger.info("[DataInitializer] - Checking and updating default films");

        // Create default persons first
        createDefaultPersons();

        // Create Interstellar (2014)
        createFilmIfNotExists("Interstellar", 2014, 
            "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
            169, Arrays.asList("Christopher Nolan"), Arrays.asList("Jonathan Nolan", "Christopher Nolan"),
            Arrays.asList("Matthew McConaughey", "Anne Hathaway", "Jessica Chastain"),
            Arrays.asList("Hans Zimmer"), Arrays.asList("Hoyte van Hoytema"),
            "/uploads/Interstellar.jpg");

        // Create Dark (2017) - Netflix
        createFilmIfNotExists("Dark", 2017,
            "A family saga with a supernatural twist, set in a German town, where the disappearance of two young children exposes the broken relationships among four families.",
            60, Arrays.asList("Baran bo Odar"), Arrays.asList("Jantje Friese"),
            Arrays.asList("Louis Hofmann", "Karoline Eichhorn", "Lisa Vicari"),
            Arrays.asList("Ben Frost"), Arrays.asList("Nikolaus Summerer"),
            "/uploads/Dark.jpg");

        // Create From (2022) - TV Series
        createFilmIfNotExists("From", 2022,
            "Unravel the mystery of a nightmarish town in middle America that traps all those who enter.",
            45, Arrays.asList("Jack Bender"), Arrays.asList("John Griffin"),
            Arrays.asList("Harvey Guillén", "Eion Bailey", "Catalina Sandino Moreno"),
            Arrays.asList("Chris Bacon"), Arrays.asList("Galen Miles"),
            "/uploads/From.jpg");

        // Create Shawshank Redemption (1994)
        createFilmIfNotExists("The Shawshank Redemption", 1994,
            "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
            142, Arrays.asList("Frank Darabont"), Arrays.asList("Frank Darabont"),
            Arrays.asList("Tim Robbins", "Morgan Freeman"),
            Arrays.asList("Thomas Newman"), Arrays.asList("Roger Deakins"),
            "/uploads/The Shawshank Redemption.jpg");

        // Debug: Log all films with their poster paths
        logger.info("[DataInitializer] - Checking film posters after update:");
        filmRepository.findAll().forEach(film -> {
            logger.info("[DataInitializer] - Film: {}, Poster: {}", film.getTitle(), film.getPoster());
        });
    }

    private void createDefaultPersons() {
        logger.info("[DataInitializer] - Creating default persons for films");

        // Directors
        createPersonIfNotExists("Christopher", "Nolan", TypePersonEnum.DIRECTOR);
        createPersonIfNotExists("Baran bo", "Odar", TypePersonEnum.DIRECTOR);
        createPersonIfNotExists("Jack", "Bender", TypePersonEnum.DIRECTOR);
        createPersonIfNotExists("Frank", "Darabont", TypePersonEnum.DIRECTOR);

        // Screenwriters
        createPersonIfNotExists("Jonathan", "Nolan", TypePersonEnum.SCREENWRITER);
        createPersonIfNotExists("Jantje", "Friese", TypePersonEnum.SCREENWRITER);
        createPersonIfNotExists("John", "Griffin", TypePersonEnum.SCREENWRITER);

        // Actors
        createPersonIfNotExists("Matthew", "McConaughey", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Anne", "Hathaway", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Jessica", "Chastain", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Louis", "Hofmann", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Karoline", "Eichhorn", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Lisa", "Vicari", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Harvey", "Guillén", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Eion", "Bailey", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Catalina", "Sandino Moreno", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Tim", "Robbins", TypePersonEnum.ACTOR);
        createPersonIfNotExists("Morgan", "Freeman", TypePersonEnum.ACTOR);

        // Musicians
        createPersonIfNotExists("Hans", "Zimmer", TypePersonEnum.MUSICIAN);
        createPersonIfNotExists("Ben", "Frost", TypePersonEnum.MUSICIAN);
        createPersonIfNotExists("Chris", "Bacon", TypePersonEnum.MUSICIAN);
        createPersonIfNotExists("Thomas", "Newman", TypePersonEnum.MUSICIAN);

        // Photographers
        createPersonIfNotExists("Hoyte van", "Hoytema", TypePersonEnum.PHOTOGRAPHER);
        createPersonIfNotExists("Nikolaus", "Summerer", TypePersonEnum.PHOTOGRAPHER);
        createPersonIfNotExists("Eben", "Bolter", TypePersonEnum.PHOTOGRAPHER);
        createPersonIfNotExists("Galen", "Miles", TypePersonEnum.PHOTOGRAPHER);
        createPersonIfNotExists("Roger", "Deakins", TypePersonEnum.PHOTOGRAPHER);
    }

    private void createPersonIfNotExists(String name, String surname, TypePersonEnum type) {
        if (!personRepository.findByNameAndSurname(name, surname).isPresent()) {
            Person person = new Person();
            person.setName(name);
            person.setSurname(surname);
            person.setType(type);
            personRepository.save(person);
            logger.debug("[DataInitializer] - Person created: {} {} ({})", name, surname, type);
        }
    }

    private void createFilmIfNotExists(String title, int year, String synopsis, int duration,
                                     List<String> directors, List<String> screenwriters,
                                     List<String> actors, List<String> musicians,
                                     List<String> photographers, String posterPath) {
        
        Optional<Film> existingFilm = filmRepository.findByTitle(title);
        
        if (!existingFilm.isPresent()) {
            // Create new film
            Film film = new Film();
            film.setTitle(title);
            film.setYear(year);
            film.setSynopsis(synopsis);
            film.setDuration(duration);
            film.setPoster(posterPath);

            // Set directors
            for (String director : directors) {
                String[] parts = director.split(" ", 2);
                String name = parts[0];
                String surname = parts.length > 1 ? parts[1] : "";
                personRepository.findByNameAndSurname(name, surname)
                    .ifPresent(person -> film.getDirectors().add(person));
            }

            // Set screenwriters
            for (String screenwriter : screenwriters) {
                String[] parts = screenwriter.split(" ", 2);
                String name = parts[0];
                String surname = parts.length > 1 ? parts[1] : "";
                personRepository.findByNameAndSurname(name, surname)
                    .ifPresent(person -> film.getScreenwriters().add(person));
            }

            // Set actors
            for (String actor : actors) {
                String[] parts = actor.split(" ", 2);
                String name = parts[0];
                String surname = parts.length > 1 ? parts[1] : "";
                personRepository.findByNameAndSurname(name, surname)
                    .ifPresent(person -> film.getActors().add(person));
            }

            // Set musicians
            for (String musician : musicians) {
                String[] parts = musician.split(" ", 2);
                String name = parts[0];
                String surname = parts.length > 1 ? parts[1] : "";
                personRepository.findByNameAndSurname(name, surname)
                    .ifPresent(person -> film.getMusicians().add(person));
            }

            // Set photographers
            for (String photographer : photographers) {
                String[] parts = photographer.split(" ", 2);
                String name = parts[0];
                String surname = parts.length > 1 ? parts[1] : "";
                personRepository.findByNameAndSurname(name, surname)
                    .ifPresent(person -> film.getPhotographers().add(person));
            }

            filmRepository.save(film);
            logger.info("[DataInitializer] - Film created: {} ({})", title, year);
        } else {
            // Update existing film with poster (force update for default films)
            Film film = existingFilm.get();
            film.setPoster(posterPath);
            filmRepository.save(film);
            logger.info("[DataInitializer] - Film updated with poster: {} ({})", title, year);
        }
    }
}
