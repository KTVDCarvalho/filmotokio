package filmotokio.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// Film review DTO
// Transfers review data between API and clients
@Data
public class ReviewDto {
    // Unique ID of the review (null when creating)
    private Long id;

    // ID of the film being reviewed - required
    @NotNull(message = "Film ID is required")
    private Long filmId;

    // Review title - required, max 200 characters
    @NotBlank(message = "Review title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    // Review text content - required, max 2000 characters
    @NotBlank(message = "Review text is required")
    @Size(max = 2000, message = "Text cannot exceed 2000 characters")
    private String textReview;

    // ID of user who wrote the review (auto-filled)
    private Long userId;
    // Username of review author (auto-filled)
    private String username;
    // Date when review was written (auto-filled)
    private String reviewDate;

    // Default constructor
    public ReviewDto() {}

}
