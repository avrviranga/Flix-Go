package service;

import model.Review;
import model.Movie;
import util.FileUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewService {
    private static final String REVIEWS_FILE = "reviews.txt";
    private static final String MOVIES_FILE = "movies.txt";

    public List<Review> getAllReviews() throws IOException {
        List<String> lines = FileUtil.readAllLines(REVIEWS_FILE);
        return lines.stream()
                .map(Review::fromString)
                .filter(review -> review != null)
                .collect(Collectors.toList());
    }

    public Review getReviewById(String reviewId) throws IOException {
        return getAllReviews().stream()
                .filter(review -> review.getId().equals(reviewId))
                .findFirst()
                .orElse(null);
    }

    public List<Review> getUserReviews(String userEmail) throws IOException {
        return getAllReviews().stream()
                .filter(review -> review.getUserEmail().equalsIgnoreCase(userEmail))
                .collect(Collectors.toList());
    }

    public List<Movie> searchMovies(String query) throws IOException {
        List<String> lines = FileUtil.readAllLines(MOVIES_FILE);
        return lines.stream()
                .map(Movie::fromString)
                .filter(movie -> movie != null &&
                        movie.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Review addReview(Review review) throws IOException {
        List<Review> reviews = getAllReviews();
        reviews.add(review);
        saveReviews(reviews);
        return review;
    }

    public Review updateReview(Review updatedReview) throws IOException {
        List<Review> reviews = getAllReviews();
        reviews = reviews.stream()
                .map(review -> review.getId().equals(updatedReview.getId()) ? updatedReview : review)
                .collect(Collectors.toList());
        saveReviews(reviews);
        return updatedReview;
    }

    public boolean deleteReview(String reviewId) throws IOException {
        List<Review> reviews = getAllReviews();
        boolean exists = reviews.stream().anyMatch(r -> r.getId().equals(reviewId));
        if (!exists) return false;

        reviews = reviews.stream()
                .filter(review -> !review.getId().equals(reviewId))
                .collect(Collectors.toList());
        saveReviews(reviews);
        return true;
    }

    private void saveReviews(List<Review> reviews) throws IOException {
        List<String> lines = reviews.stream()
                .map(Review::toString)
                .collect(Collectors.toList());
        FileUtil.writeAllLines(REVIEWS_FILE, lines);
    }
}