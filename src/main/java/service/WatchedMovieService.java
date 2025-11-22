package service;

import model.Movie;
import Stack.MovieHistoryStack;
import model.User;
import util.FileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatchedMovieService {
    private static final String DELIMITER = "||";

    public void addWatchedMovie(User user, Movie movie) throws IOException {
        String[] allLines = FileUtil.readAllWatchedMovieLines();
        List<String> linesList = new ArrayList<>(Arrays.asList(allLines));
        boolean userFound = false;

        for (int i = 0; i < linesList.size(); i++) {
            String line = linesList.get(i);
            if (line.startsWith(user.getEmail() + DELIMITER)) {
                MovieHistoryStack stack = MovieHistoryStack.fromString(line.substring(line.indexOf(DELIMITER) + DELIMITER.length()));
                stack.push(movie.getId());
                linesList.set(i, user.getEmail() + DELIMITER + stack.toString());
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            MovieHistoryStack stack = new MovieHistoryStack();
            stack.push(movie.getId());
            linesList.add(user.getEmail() + DELIMITER + stack.toString());
        }

        FileUtil.writeAllWatchedMovieLines(linesList.toArray(new String[0]));
    }

    public MovieHistoryStack getWatchedMovies(User user) throws IOException {
        String[] allLines = FileUtil.readAllWatchedMovieLines();

        for (String line : allLines) {
            if (line.startsWith(user.getEmail() + DELIMITER)) {
                return MovieHistoryStack.fromString(line.substring(line.indexOf(DELIMITER) + DELIMITER.length()));
            }
        }

        return new MovieHistoryStack();
    }

    public List<Movie> getWatchedMoviesAsList(User user, MovieService movieService) throws IOException {
        MovieHistoryStack stack = getWatchedMovies(user);
        List<Movie> movies = new ArrayList<>();

        for (String movieId : stack.getAllMovies()) {
            movieService.getMovieById(movieId).ifPresent(movies::add);
        }

        return movies;
    }
}