package filmotokio.repository;

import filmotokio.domain.FilmMigration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmMigrationRepository extends JpaRepository<FilmMigration, Long> {
    
    boolean existsByFilmId(Long filmId);
    
    void deleteByFilmId(Long filmId);
}
