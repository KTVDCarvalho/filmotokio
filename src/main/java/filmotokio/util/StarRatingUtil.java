package filmotokio.util;

import java.util.ArrayList;
import java.util.List;

// Utility class for star rating display
// Converts numeric ratings to star representations
public class StarRatingUtil {
    
    // Star display state representation
    public static class StarDisplay {
        private boolean full;   // Star is completely filled
        private boolean half;   // Star is half filled (not used in current implementation)
        private boolean empty;  // Star is empty
        
        public StarDisplay(boolean full, boolean half, boolean empty) {
            this.full = full;
            this.half = half;
            this.empty = empty;
        }
        
        public boolean isFull() { return full; }
        public boolean isHalf() { return half; }
        public boolean isEmpty() { return empty; }
    }
    
    // Convert a numeric rating to a list of star displays
    public static List<StarDisplay> getStarDisplay(double rating) {
        List<StarDisplay> stars = new ArrayList<>();
        
        // Traditional rounding for ratings
        int roundedRating = (int) Math.round(rating);
        
        // Create 5 stars (full or empty based on rounded rating)
        for (int i = 1; i <= 5; i++) {
            if (i <= roundedRating) {
                // Full star
                stars.add(new StarDisplay(true, false, false));
            } else {
                // Empty star
                stars.add(new StarDisplay(false, false, true));
            }
        }
        
        return stars;
    }
    
    // Get the CSS class for a specific star position
    public static String getStarClass(double rating, int position) {
        // Traditional rounding for ratings
        int roundedRating = (int) Math.round(rating);
        
        if (position <= roundedRating) {
            return "avg-star"; // Full star CSS class
        } else {
            return "avg-star empty"; // Empty star CSS class
        }
    }
}
