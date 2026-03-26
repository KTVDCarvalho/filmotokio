package filmotokio.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Person entity for cast and crew
 * Represents actors, directors, and film roles
 */
@Entity
@Table(
        name = "persons",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "surname", "type"})
        }
)
@Data
@EqualsAndHashCode(of = {"name", "surname", "type"})
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    // Unique identifier for each person in the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // First name - required field
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // Last name/surname - required field
    @Column(name = "surname", nullable = false, length = 255)
    private String surname;

    // Role type for this person (actor, director, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TypePersonEnum type;
}
