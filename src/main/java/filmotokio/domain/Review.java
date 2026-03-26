package filmotokio.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Film review entity
 * Links users to their written film reviews
 */
@Entity
@Table(name = "review", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "film_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    // Unique identifier for each review in the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Review title
    private String title;

    // Review content - up to 5000 characters
    @Column(length = 5000)
    private String textReview;

    // When the review was written
    private LocalDate date;

    // User who wrote this review
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Film being reviewed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;
}
