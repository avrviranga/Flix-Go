package controller;

import model.Movie;
import Stack.MovieHistoryStack;
import model.User;
import service.MovieService;
import service.WatchedMovieService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/watched-movies/*")
public class WatchedMovieController extends HttpServlet {
    private final WatchedMovieService watchedMovieService = new WatchedMovieService();
    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, out, "User not logged in", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("user");
        MovieHistoryStack watchedMovies = watchedMovieService.getWatchedMovies(user);
        String[] movieIds = watchedMovies.getAllMovies();

        // Convert to JSON array
        out.print("[");
        for (int i = 0; i < movieIds.length; i++) {
            if (i > 0) out.print(",");
            Movie movie = movieService.getMovieById(movieIds[i]).orElse(null);
            if (movie != null) {
                // Update to include full poster path
                out.print(toJson(movie));
            }
        }
        out.print("]");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, out, "User not logged in", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String movieId = request.getParameter("movieId");
        if (movieId == null || movieId.isEmpty()) {
            sendError(response, out, "Movie ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = (User) session.getAttribute("user");
        Movie movie = movieService.getMovieById(movieId).orElse(null);

        if (movie == null) {
            sendError(response, out, "Movie not found", HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        watchedMovieService.addWatchedMovie(user, movie);
        out.print("{\"message\":\"Movie added to watched history\"}");
    }

    private void sendError(HttpServletResponse response, PrintWriter out, String message, int status) {
        response.setStatus(status);
        out.print("{\"error\":\"" + message + "\"}");
    }

    private String toJson(Movie movie) {
        // Update to use relative path that can be served by your web server
        String posterPath = "/api/movie-posters/" + movie.getPosterPath();
        return String.format(
                "{\"id\":\"%s\",\"title\":\"%s\",\"year\":%d,\"posterPath\":\"%s\"}",
                movie.getId(),
                escapeJson(movie.getTitle()),
                movie.getYear(),
                escapeJson(posterPath)
        );
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}