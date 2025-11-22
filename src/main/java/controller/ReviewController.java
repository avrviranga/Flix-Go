package controller;

import com.google.gson.Gson;
import model.Movie;
import model.Review;
import service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/reviews/*")
public class ReviewController extends HttpServlet {
    private ReviewService reviewService = new ReviewService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String userEmail = req.getParameter("userEmail");
        String query = req.getParameter("query");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                if (userEmail != null) {
                    // Get user's reviews
                    List<Review> reviews = reviewService.getUserReviews(userEmail);
                    sendJsonResponse(resp, reviews);
                } else if (query != null) {
                    // Search movies
                    List<Movie> movies = reviewService.searchMovies(query);
                    sendJsonResponse(resp, movies);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
                }
            } else if (pathInfo.equals("/all")) {
                // Get all reviews - New endpoint for the home page
                List<Review> allReviews = reviewService.getAllReviews();
                sendJsonResponse(resp, allReviews);
            } else {
                // Get single review by ID
                String reviewId = pathInfo.substring(1);
                Review review = reviewService.getReviewById(reviewId);
                if (review != null) {
                    sendJsonResponse(resp, review);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Review not found");
                }
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Review review = gson.fromJson(req.getReader(), Review.class);
            review = reviewService.addReview(review);
            sendJsonResponse(resp, review);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Review review = gson.fromJson(req.getReader(), Review.class);
            review = reviewService.updateReview(review);
            sendJsonResponse(resp, review);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String reviewId = req.getParameter("id");
            if (reviewId == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Review ID is required");
                return;
            }

            boolean success = reviewService.deleteReview(reviewId);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Review not found");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(data));
    }
}