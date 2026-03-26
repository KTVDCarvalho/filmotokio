package filmotokio.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

// Film migration job listener
// Manages migration process lifecycle
public class FilmMigrationJobListener extends JobExecutionListenerSupport {

    private static final Logger logger = LoggerFactory.getLogger(FilmMigrationJobListener.class);

    // Runs before migration starts
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Log the start of the migration process
        logger.info("[FilmMigrationJobListener] - ===== STARTING MIGRATION PROCESS =====");
        
        // Get the job parameters
        JobParameters parameters = jobExecution.getJobParameters();
        
        // Simulate film count
        int filmsToMigrate = 10;
        // Log film count
        logger.info("[FilmMigrationJobListener] - Total films to migrate: {}", filmsToMigrate);
        
        // Store the film count in the job context for later use
        jobExecution.getExecutionContext().put("filmsToMigrate", filmsToMigrate);
        
        // Log the job parameters for debugging
        logger.info("[FilmMigrationJobListener] - Job parameters: {}", parameters);
    }

    // Runs after migration completes
    @Override
    public void afterJob(JobExecution jobExecution) {
        // Log the end of the migration process
        logger.info("[FilmMigrationJobListener] - ===== MIGRATION PROCESS COMPLETED =====");
        
        // Get the film counts from the job context
        int filmsToMigrate = jobExecution.getExecutionContext().getInt("filmsToMigrate", 0);
        int filmsMigrated = jobExecution.getExecutionContext().getInt("filmsMigrated", 0);
        
        // Log expected film count
        logger.info("[FilmMigrationJobListener] - Films to migrate (start): {}", filmsToMigrate);
        // Log actual migrated count
        logger.info("[FilmMigrationJobListener] - Films migrated (end): {}", filmsMigrated);
        
        // Check if any films were migrated
        if (filmsMigrated == 0) {
            // No films were migrated, they were already done before
            logger.info("[FilmMigrationJobListener] - All films have already been migrated previously");
        } else {
            // Some films were migrated, show success message
            logger.info("[FilmMigrationJobListener] - Migration completed successfully! {} films processed", filmsMigrated);
        }
        
        // Log the final status of the job
        logger.info("[FilmMigrationJobListener] - Job status: {}", jobExecution.getStatus());
    }
}
