package service;

import model.Movie;
import util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieService {
    private static final String MOVIES_FILE = "movies.txt";

    public List<Movie> getAllMovies() throws IOException {
        List<String> lines = FileUtil.readAllMovieLines();
        List<Movie> movies = new ArrayList<>();

        for (String line : lines) {
            try {
                movies.add(Movie.fromFileString(line));
            } catch (Exception e) {
                System.err.println("Error parsing movie data: " + e.getMessage());
                System.err.println("Problematic line: " + line);
                e.printStackTrace();
            }
        }

        return movies;
    }

    public List<Movie> searchMovies(String searchTerm) throws IOException {
        List<Movie> allMovies = getAllMovies();
        if (searchTerm == null || searchTerm.isEmpty()) {
            return allMovies;
        }

        String term = searchTerm.toLowerCase();
        return allMovies.stream()
                .filter(movie -> {
                    String title = movie.getTitle() != null ? movie.getTitle().toLowerCase() : "";
                    String genre = movie.getGenre() != null ? movie.getGenre().toLowerCase() : "";
                    String desc = movie.getDescription() != null ? movie.getDescription().toLowerCase() : "";

                    return title.contains(term) || genre.contains(term) || desc.contains(term);
                })
                .collect(Collectors.toList());
    }

    public Optional<Movie> getMovieById(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }

        return getAllMovies().stream()
                .filter(movie -> id.equals(movie.getId()))
                .findFirst();
    }

    public Movie addMovie(Movie movie) throws IOException {
        if (movie == null) {
            throw new IllegalArgumentException("Movie cannot be null");
        }

        List<Movie> movies = getAllMovies();

        // Set a new ID if not provided
        if (movie.getId() == null || movie.getId().isEmpty()) {
            movie.setId(java.util.UUID.randomUUID().toString());
        }

        movies.add(movie);
        saveAllMovies(movies);
        return movie;
    }

    public Movie updateMovie(Movie updatedMovie) throws IOException {
        if (updatedMovie == null || updatedMovie.getId() == null || updatedMovie.getId().isEmpty()) {
            throw new IllegalArgumentException("Movie ID is required for update");
        }

        List<Movie> movies = getAllMovies();
        boolean found = false;

        for (int i = 0; i < movies.size(); i++) {
            if (updatedMovie.getId().equals(movies.get(i).getId())) {
                movies.set(i, updatedMovie);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Movie with ID " + updatedMovie.getId() + " not found");
        }

        saveAllMovies(movies);
        return updatedMovie;
    }

    public boolean deleteMovie(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return false;
        }

        List<Movie> movies = getAllMovies();
        boolean removed = movies.removeIf(movie -> id.equals(movie.getId()));

        if (removed) {
            saveAllMovies(movies);
        }

        return removed;
    }

    private void saveAllMovies(List<Movie> movies) throws IOException {
        if (movies == null) {
            throw new IllegalArgumentException("Movies list cannot be null");
        }

        List<String> lines = new ArrayList<>();

        for (Movie movie : movies) {
            try {
                String line = movie.toFileString();
                lines.add(line);
            } catch (Exception e) {
                System.err.println("Error converting movie to file string: " + e.getMessage());
                if (movie != null && movie.getTitle() != null) {
                    System.err.println("Problem movie: " + movie.getTitle());
                }
                e.printStackTrace();
            }
        }

        try {
            FileUtil.writeAllMovieLines(lines);
        } catch (IOException e) {
            System.err.println("Failed to write all movie lines: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}