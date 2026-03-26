package filmotokio.service;

import filmotokio.domain.Film;
import filmotokio.domain.FilmMigration;
import filmotokio.repository.FilmMigrationRepository;
import filmotokio.repository.FilmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MigrationService {

    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmMigrationRepository filmMigrationRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job migrateFilmsJob;

    public MigrationResult migrateFilmsToCsv() {
        logger.info("[MigrationService] - Starting film migration process");
        
        try {
            // Get films to migrate
            List<Film> filmsToMigrate = getFilmsNotMigrated();
            
            if (filmsToMigrate.isEmpty()) {
                logger.info("[MigrationService] - No films to migrate");
                return new MigrationResult("No films to migrate", 0, true);
            }
            
            // Launch batch job
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("operation", "migrateFilms")
                    .toJobParameters();
            
            jobLauncher.run(migrateFilmsJob, jobParameters);
            
            logger.info("[MigrationService] - Migration job started with {} films", filmsToMigrate.size());
            
            // Mark films as migrated (this will also be done by the batch listener, but we do it here for immediate response)
            int migratedCount = 0;
            for (Film film : filmsToMigrate) {
                try {
                    if (!filmMigrationRepository.existsByFilmId(film.getId())) {
                        FilmMigration migration = new FilmMigration(film);
                        
                        filmMigrationRepository.save(migration);
                        migratedCount++;
                    }
                } catch (Exception e) {
                    logger.error("[MigrationService] - Error marking film {} as migrated: {}", film.getTitle(), e.getMessage());
                }
            }
            
            String message = String.format("Migration completed successfully! %d films processed", migratedCount);
            logger.info("[MigrationService] - {}", message);
            
            return new MigrationResult(message, migratedCount, true);
            
        } catch (Exception e) {
            logger.error("[MigrationService] - Error during migration: {}", e.getMessage(), e);
            return new MigrationResult("Error during migration: " + e.getMessage(), 0, false);
        }
    }

    public Map<String, Object> getMigrationStatus() {
        try {
            long totalFilms = filmRepository.count();
            long migratedFilms = filmMigrationRepository.count();
            int filmsToMigrate = (int) (totalFilms - migratedFilms);
            
            // Get last migration date
            String lastMigration = getLastMigrationDate();
            
            Map<String, Object> status = new HashMap<>();
            status.put("totalFilms", totalFilms);
            status.put("filmsMigrated", migratedFilms);
            status.put("filmsToMigrate", filmsToMigrate);
            status.put("lastMigration", lastMigration);
            status.put("migrationNeeded", filmsToMigrate > 0);
            status.put("message", filmsToMigrate > 0 ? 
                "There are " + filmsToMigrate + " films pending migration" : "All films have already been migrated");
            
            logger.info("[MigrationService] - Status: Total={}, Migrated={}, To migrate={}, Last migration={}", 
                       totalFilms, migratedFilms, filmsToMigrate, lastMigration);
            
            return status;
        } catch (Exception e) {
            logger.error("[MigrationService] - Error getting migration status: {}", e.getMessage(), e);
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("totalFilms", 0);
            errorStatus.put("filmsMigrated", 0);
            errorStatus.put("filmsToMigrate", 0);
            errorStatus.put("lastMigration", "Error");
            errorStatus.put("migrationNeeded", false);
            errorStatus.put("message", "Error checking status");
            return errorStatus;
        }
    }
    
    private String getLastMigrationDate() {
        try {
            // Get the most recent migration
            List<FilmMigration> migrations = filmMigrationRepository.findAll();
            
            if (migrations.isEmpty()) {
                return "No migration performed";
            }
            
            // Find the most recent migration date
            LocalDate lastMigration = migrations.stream()
                    .map(FilmMigration::getMigrationDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);
            
            if (lastMigration != null) {
                return lastMigration.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            }
            
            return "No migration performed";
        } catch (Exception e) {
            logger.error("[MigrationService] - Error getting last migration date: {}", e.getMessage());
            return "Unavailable";
        }
    }

    private List<Film> getFilmsNotMigrated() {
        try {
            // Get all films and filter out those already migrated
            List<Film> allFilms = filmRepository.findAll();
            
            return allFilms.stream()
                    .filter(film -> !filmMigrationRepository.existsByFilmId(film.getId()))
                    .toList();
                    
        } catch (Exception e) {
            logger.error("[MigrationService] - Error getting non-migrated films: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public static class MigrationResult {
        private final String message;
        private final int filmsMigrated;
        private final boolean success;

        public MigrationResult(String message, int filmsMigrated, boolean success) {
            this.message = message;
            this.filmsMigrated = filmsMigrated;
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public int getFilmsMigrated() {
            return filmsMigrated;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
