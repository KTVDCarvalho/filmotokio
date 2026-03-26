package filmotokio.batch;

import filmotokio.domain.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

/**
 * Spring Batch configuration for film migration
 * Sets up batch job to export films to CSV
 */
@Configuration
@EnableBatchProcessing
public class FilmMigrationBatchConfig {

    private static final Logger logger = LoggerFactory.getLogger(FilmMigrationBatchConfig.class);

    // Factory for creating batch jobs
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    // Factory for creating batch steps
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    // Database connection for reading film data
    @Autowired
    private DataSource dataSource;

    // Creates a reader that reads films from database that haven't been migrated yet
    @Bean
    public JdbcCursorItemReader<Film> filmReader() {
        // Log reader configuration
        logger.info("[FilmMigrationBatchConfig] - Configuring reader for non-migrated films");
        
        JdbcCursorItemReader<Film> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        // SQL query for non-migrated films
        reader.setSql("SELECT f.id, f.title, f.release_year, f.duration, f.synopsis, f.poster FROM films f " +
                      "WHERE f.id NOT IN (SELECT fm.film_id FROM film_migration fm)");
        reader.setRowMapper((rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setTitle(rs.getString("title"));
            film.setYear(rs.getInt("release_year"));
            film.setDuration(rs.getInt("duration"));
            film.setSynopsis(rs.getString("synopsis"));
            film.setPoster(rs.getString("poster"));
            return film;
        });
        
        return reader;
    }

    // Creates CSV file writer
    @Bean
    public FlatFileItemWriter<Film> filmWriter() {
        // Log writer configuration
        logger.info("[FilmMigrationBatchConfig] - Configuring writer for CSV");
        
        // Build CSV writer
        return new FlatFileItemWriterBuilder<Film>()
                .name("filmWriter")
                // Set output file location
                .resource(new FileSystemResource("films_migration.csv"))
                // Use CSV format
                .delimited()
                // Set comma delimiter
                .delimiter(",")
                // Define Film fields to write
                .names(new String[]{"id", "title", "year", "duration", "synopsis", "poster"})
                // Write CSV header
                .headerCallback(writer -> writer.write("ID,TITLE,YEAR,DURATION,SYNOPSIS,POSTER"))
                .build();
    }

    // Creates a step that processes films in chunks of 10
    @Bean
    public Step migrateFilmsStep() {
        // Log step configuration
        logger.info("[FilmMigrationBatchConfig] - Configuring migration step");
        
        // Build migration step
        return stepBuilderFactory.get("migrateFilmsStep")
                // Process films in chunks of 10
                .<Film, Film>chunk(10)
                // Use film reader
                .reader(filmReader())
                // Use film writer
                .writer(filmWriter())
                // Add write listener
                .listener(filmMigrationWriteListener())
                .build();
    }

    // Creates job with migration step
    @Bean
    public Job migrateFilmsJob() {
        // Log job configuration
        logger.info("[FilmMigrationBatchConfig] - Configuring film migration job");
        
        // Build migration job
        return jobBuilderFactory.get("migrateFilmsJob")
                // Use new ID for each run
                .incrementer(new RunIdIncrementer())
                // Add listener to track job execution
                .listener(filmMigrationJobListener())
                // Add the migration step to the job
                .flow(migrateFilmsStep())
                // End the job flow
                .end()
                .build();
    }

    // Creates the job listener instance
    @Bean
    public FilmMigrationJobListener filmMigrationJobListener() {
        return new FilmMigrationJobListener();
    }

    // Creates the write listener instance
    @Bean
    public FilmMigrationWriteListener filmMigrationWriteListener() {
        return new FilmMigrationWriteListener();
    }
}
