package filmotokio.service;

import filmotokio.domain.Film;
import filmotokio.domain.Person;
import filmotokio.repository.FilmRepository;
import filmotokio.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class FilmRegisterService {

    private static final Logger logger = LoggerFactory.getLogger(FilmRegisterService.class);
    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private PersonRepository personRepository;

    private List<Long> sanitize(List<Long> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    public String registerFilmWithPersons(Film film,
                                           MultipartFile posterFile,
                                           List<Long> directorsIds,
                                           List<Long> screenwritersIds,
                                           List<Long> actorsIds,
                                           List<Long> musiciansIds,
                                           List<Long> photographersIds) {

        logger.info("[FilmRegisterService] - Attempting to create new film - Title: \"{}\", Year: {}", 
                   film.getTitle(), film.getYear());

        // Clean lists to remove duplicates and nulls
        directorsIds = sanitize(directorsIds);
        screenwritersIds = sanitize(screenwritersIds);
        actorsIds = sanitize(actorsIds);
        musiciansIds = sanitize(musiciansIds);
        photographersIds = sanitize(photographersIds);

        logger.debug("[FilmRegisterService] - Processed IDs - Directors: {}, Screenwriters: {}, Actors: {}, Musicians: {}, Photographers: {}", 
                    directorsIds.size(), screenwritersIds.size(), actorsIds.size(), musiciansIds.size(), photographersIds.size());

        // Basic validation
        if (film.getTitle() == null || film.getTitle().isBlank()) {
            logger.warn("[FilmRegisterService] - WARNING: Film title is required");
            return "Film title is required";
        }

        if (film.getYear() <= 0 || film.getYear() > 2100) {
            logger.warn("[FilmRegisterService] - WARNING: Invalid film year - Year: {}", film.getYear());
            return "Invalid film year";
        }

        if (filmRepository.findByTitle(film.getTitle()).isPresent()) {
            logger.warn("[FilmRegisterService] - WARNING: Film with title already exists - Title: \"{}\"", film.getTitle());
            return "A film with this title already exists";
        }

        try {
            // Process poster upload
            if (posterFile != null && !posterFile.isEmpty()) {
                String original = posterFile.getOriginalFilename();
                
                // File name security validation
                if (original == null || original.contains("..") || original.contains("/") || original.contains("\\")) {
                    throw new RuntimeException("Invalid file name");
                }
                
                // Sanitize file name
                String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
                String filename = UUID.randomUUID() + "_" + safeName;
                String uploadDir = System.getProperty("user.dir") + "/" + UPLOAD_DIR;
                Path uploadPath = Paths.get(uploadDir);

                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(filename);
                posterFile.transferTo(filePath.toFile());

                film.setPoster("/uploads/" + filename);
            }

            // Validate and associate directors
            if (!directorsIds.isEmpty()) {
                Set<Long> ids = new HashSet<>(directorsIds);
                List<Person> persons = personRepository.findAllByIdIn(ids);
                
                if (persons.size() != ids.size()) {
                    throw new RuntimeException("Some directors do not exist");
                }
                
                film.setDirectors(new HashSet<>(persons));
            }
            
            // Validate and associate screenwriters
            if (!screenwritersIds.isEmpty()) {
                Set<Long> ids = new HashSet<>(screenwritersIds);
                List<Person> persons = personRepository.findAllByIdIn(ids);
                
                if (persons.size() != ids.size()) {
                    throw new RuntimeException("Some screenwriters do not exist");
                }
                
                film.setScreenwriters(new HashSet<>(persons));
            }
            
            // Validate and associate actors
            if (!actorsIds.isEmpty()) {
                logger.debug("[FilmRegisterService] - Processing actors - IDs: {}", actorsIds);
                Set<Long> ids = new HashSet<>(actorsIds);
                List<Person> persons = personRepository.findAllByIdIn(ids);
                logger.debug("[FilmRegisterService] - Actors found - Quantity: {}", persons.size());
                
                if (persons.size() != ids.size()) {
                    logger.warn("[FilmRegisterService] - WARNING: Invalid actor IDs - Expected: {}, Found: {}", 
                               ids.size(), persons.size());
                    throw new RuntimeException("Some actors do not exist");
                }
                
                film.setActors(new HashSet<>(persons));
            }
            
            // Validate and associate musicians
            if (!musiciansIds.isEmpty()) {
                Set<Long> ids = new HashSet<>(musiciansIds);
                List<Person> persons = personRepository.findAllByIdIn(ids);
                
                if (persons.size() != ids.size()) {
                    throw new RuntimeException("Some musicians do not exist");
                }
                
                film.setMusicians(new HashSet<>(persons));
            }
            
            // Validate and associate photographers
            if (!photographersIds.isEmpty()) {
                Set<Long> ids = new HashSet<>(photographersIds);
                List<Person> persons = personRepository.findAllByIdIn(ids);
                
                if (persons.size() != ids.size()) {
                    throw new RuntimeException("Some photographers do not exist");
                }
                
                film.setPhotographers(new HashSet<>(persons));
            }

            filmRepository.save(film);
            logger.info("[FilmRegisterService] - Film created successfully - ID: {}, Title: \"{}\"", 
                       film.getId(), film.getTitle());
            return null;

        } catch (Exception e) {
            logger.error("[FilmRegisterService] - ERROR creating film - Title: \"{}\", Exception: {}", 
                        film.getTitle(), e.getMessage(), e);
            return "Error saving film: " + e.getMessage();
        }
    }
}
