package filmotokio.batch;

import filmotokio.domain.Film;
import filmotokio.domain.FilmMigration;
import filmotokio.repository.FilmMigrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

// Film migration write listener
// Tracks CSV export operations
public class FilmMigrationWriteListener implements ItemWriteListener<Film> {

    private static final Logger logger = LoggerFactory.getLogger(FilmMigrationWriteListener.class);

    @Autowired
    private FilmMigrationRepository filmMigrationRepository;

    // Runs before CSV write operation
    @Override
    public void beforeWrite(List<? extends Film> items) {
        // Log film count for CSV write
        logger.info("[FilmMigrationWriteListener] - Preparing to write {} films to CSV", items.size());
    }

    // Runs after successful CSV write
    @Override
    public void afterWrite(List<? extends Film> items) {
        // Log successful CSV write
        logger.info("[FilmMigrationWriteListener] - {} films written to CSV successfully", items.size());
        
        // Mark films as migrated
        for (Film film : items) {
            try {
                // Check if film already marked as migrated
                if (!filmMigrationRepository.existsByFilmId(film.getId())) {
                    // Create migration record
                    FilmMigration migration = new FilmMigration(film);
                    migration.setMigrationDate(LocalDate.now());
                    migration.setMigrated(true);
                    
                    // Save migration record
                    filmMigrationRepository.save(migration);
                    // Log migration success
                    logger.info("[FilmMigrationWriteListener] - Film {} marked as migrated on {}", 
                               film.getTitle(), migration.getMigrationDate());
                }
            } catch (Exception e) {
                // Log migration error
                logger.error("[FilmMigrationWriteListener] - Error marking film {} as migrated: {}", 
                    film.getTitle(), e.getMessage());
            }
        }
    }

    // Handles CSV write errors
    @Override
    public void onWriteError(Exception exception, List<? extends Film> items) {
        // Log CSV write error
        logger.error("[FilmMigrationWriteListener] - Error writing films to CSV: {}", exception.getMessage());
        // Log failed films
        for (Film film : items) {
            logger.error("[FilmMigrationWriteListener] - Film with error: {}", film.getTitle());
        }
    }
}
