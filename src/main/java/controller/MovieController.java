package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Movie;
import service.MovieService;
import util.MovieSorter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/api/movies/*")
public class MovieController extends HttpServlet {
    private final MovieService movieService = new MovieService();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Handle GET /api/movies - returns all movies or filtered movies if search parameter exists
                String searchTerm = request.getParameter("search");
                List<Movie> movies;

                if (searchTerm != null && !searchTerm.isEmpty()) {
                    // If search parameter exists, return filtered movies
                    movies = movieService.searchMovies(searchTerm);
                } else {
                    // Otherwise return all movies
                    movies = movieService.getAllMovies();
                }
                out.print(gson.toJson(movies));
            } else if (pathInfo.equals("/sorted-by-rating")) {
                // Handle GET /api/movies/sorted-by-rating - returns movies sorted by rating
                List<Movie> movies = movieService.getAllMovies();
                MovieSorter.bubbleSortByRating(movies);
                out.print(gson.toJson(movies));
            } else {
                // Handle GET /api/movies/{id} - returns a specific movie by ID
                String id = pathInfo.substring(1); // Remove leading slash
                Optional<Movie> movie = movieService.getMovieById(id);

                if (movie.isPresent()) {
                    out.print(gson.toJson(movie.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(new ErrorResponse("Movie not found")));
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Error processing request: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Parse movie data from request body
            Movie movie = gson.fromJson(request.getReader(), Movie.class);

            // Validate required fields
            if (movie.getTitle() == null || movie.getTitle().isEmpty() ||
                    movie.getPosterPath() == null || movie.getPosterPath().isEmpty() ||
                    movie.getFilePath() == null || movie.getFilePath().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ErrorResponse("Missing required fields")));
                return;
            }

            // Add movie
            Movie addedMovie = movieService.addMovie(movie);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(addedMovie));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Error processing request: " + e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ErrorResponse("Movie ID is required")));
                return;
            }

            String id = pathInfo.substring(1); // Remove leading slash
            Movie movie = gson.fromJson(request.getReader(), Movie.class);

            // Ensure ID in URL matches ID in body
            if (!id.equals(movie.getId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ErrorResponse("ID in URL does not match ID in request body")));
                return;
            }

            // Update movie
            Movie updatedMovie = movieService.updateMovie(movie);
            out.print(gson.toJson(updatedMovie));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print(gson.toJson(new ErrorResponse(e.getMessage())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Error processing request: " + e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ErrorResponse("Movie ID is required")));
                return;
            }

            String id = pathInfo.substring(1); // Remove leading slash
            boolean deleted = movieService.deleteMovie(id);

            if (deleted) {
                out.print(gson.toJson(new SuccessResponse("Movie deleted successfully")));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ErrorResponse("Movie not found")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Error processing request: " + e.getMessage())));
        }
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    private static class SuccessResponse {
        private final String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}