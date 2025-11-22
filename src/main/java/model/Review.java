package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Review {
    private String id;
    private String userEmail;
    private String movieId;
    private String movieTitle;
    private int rating;
    private String reviewText;
    private String date;

    public Review() {
        this.id = UUID.randomUUID().toString();
        this.date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Review(String id, String userEmail, String movieId, String movieTitle,
                  int rating, String reviewText, String date) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.userEmail = userEmail;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.rating = rating;
        this.reviewText = reviewText;
        this.date = date != null ? date : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return id + "||" + userEmail + "||" + movieId + "||" + movieTitle + "||" +
                rating + "||" + reviewText + "||" + date;
    }

    public static Review fromString(String str) {
        String[] parts = str.split("\\|\\|");
        if (parts.length >= 7) {
            return new Review(parts[0], parts[1], parts[2], parts[3],
                    Integer.parseInt(parts[4]), parts[5], parts[6]);
        }
        return null;
    }
}