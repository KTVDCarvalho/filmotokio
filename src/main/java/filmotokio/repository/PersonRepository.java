package filmotokio.repository;

import filmotokio.domain.Person;
import filmotokio.domain.TypePersonEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Person repository for database operations
 * Handles person data persistence
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Find a person by their exact name and surname
    Optional<Person> findByNameAndSurname(String name, String surname);
    
    // Find all people with a specific role type (actor, director, etc.)
    List<Person> findByType(TypePersonEnum type);
    
    // Find people by multiple IDs
    List<Person> findAllByIdIn(Collection<Long> ids);
}
