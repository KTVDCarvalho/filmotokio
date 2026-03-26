package filmotokio.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * This entity tracks which films have been migrated to CSV format
 * It prevents duplicate migrations and records migration dates
 */
@Entity
@Data
@Table(name = "film_migration")
public class FilmMigration {
    
    // Unique identifier for each migration record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Film that was migrated - one-to-one relationship
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", unique = true)
    private Film film;
    
    // Date when the film was migrated to CSV
    @Column(name = "migration_date")
    private LocalDate migrationDate;
    
    // Flag indicating if the migration was completed successfully
    @Column(name = "migrated")
    private boolean migrated = false;

    // Default constructor for JPA
    public FilmMigration() {}

    // Constructor for creating a new migration record
    public FilmMigration(Film film) {
        this.film = film;
        this.migrationDate = LocalDate.now();
        this.migrated = true;
    }
}
