package filmotokio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Film migration controller for administrators
 * Handles CSV export functionality
 */
@Controller
@RequestMapping("/admin/migration")
public class MigrationController {

    private static final Logger logger = LoggerFactory.getLogger(MigrationController.class);

    // Spring Batch JobLauncher for starting batch jobs
    @Autowired
    private JobLauncher jobLauncher;

    // Migration job for film processing and CSV generation
    @Autowired
    private Job migrateFilmsJob;

    /**
     * Displays the migration management page
     * Shows information about the migration process and controls
     */
    @GetMapping
    public String migrationPage() {
        return "admin/migration";
    }

    /**
     * Starts the film migration batch job
     * Creates unique job parameters and launches the migration process
     */
    @PostMapping("/run")
    public String runMigration(RedirectAttributes redirectAttributes) {
        try {
            logger.info("[MigrationController] - Starting film migration process");
            
            // Create unique job parameters
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            // Launch the migration job
            jobLauncher.run(migrateFilmsJob, jobParameters);
            
            logger.info("[MigrationController] - Migration process started successfully");
            redirectAttributes.addFlashAttribute("success", "Migration process started successfully!");
            
        } catch (Exception e) {
            logger.error("[MigrationController] - Error starting migration process: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error starting migration process: " + e.getMessage());
        }
        
        return "redirect:/admin/migration";
    }
}
