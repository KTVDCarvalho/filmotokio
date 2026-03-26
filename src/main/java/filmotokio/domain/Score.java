package filmotokio.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Score entity for film ratings
 * Links users to their film ratings (1-5 stars)
 */
@Entity
@Table(name = "scores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "film_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Score {

    // Unique identifier for each score in the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    // Rating value (1-5 stars)
    @Column(name = "rating_value", nullable = false)
    private Integer value; // 1 to 5 stars

    // User who gave this score
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Film being scored
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;
}