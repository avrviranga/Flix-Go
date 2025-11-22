package model;

import java.util.UUID;
import java.util.Base64;

public class Movie {
    private String id;
    private String title;
    private int year;
    private double rating;
    private String genre;
    private String description;
    private String posterPath;
    private String filePath;
    private String trailerPath;

    public Movie() {
        this.id = UUID.randomUUID().toString();
    }

    public Movie(String id, String title, int year, double rating, String genre,
                 String description, String posterPath, String filePath, String trailerPath) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.title = title;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
        this.description = description;
        this.posterPath = posterPath;
        this.filePath = filePath;
        this.trailerPath = trailerPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTrailerPath() {
        return trailerPath;
    }

    public void setTrailerPath(String trailerPath) {
        this.trailerPath = trailerPath;
    }

    public String toFileString() {
        // Use a custom delimiter that's unlikely to appear in normal text: "||"
        // Also base64 encode the description to avoid any delimiter issues
        String encodedDescription = "";
        if (description != null && !description.isEmpty()) {
            encodedDescription = Base64.getEncoder().encodeToString(description.getBytes());
        }

        return String.join("||",
                id,
                title != null ? title : "",
                String.valueOf(year),
                String.valueOf(rating),
                genre != null ? genre : "",
                encodedDescription,
                posterPath != null ? posterPath : "",
                filePath != null ? filePath : "",
                trailerPath != null ? trailerPath : ""
        );
    }

    public static Movie fromFileString(String line) {
        try {
            // Use the custom delimiter "||" for splitting
            String[] parts = line.split("\\|\\|");

            if (parts.length < 8) {
                throw new IllegalArgumentException("Invalid movie data format: not enough fields");
            }

            String id = parts[0];
            String title = parts[1];
            int year = Integer.parseInt(parts[2]);
            double rating = Double.parseDouble(parts[3]);
            String genre = parts[4];

            // Decode the base64 encoded description
            String description = "";
            if (parts[5] != null && !parts[5].isEmpty()) {
                description = new String(Base64.getDecoder().decode(parts[5]));
            }

            String posterPath = parts[6];
            String filePath = parts[7];
            String trailerPath = parts.length > 8 ? parts[8] : "";

            return new Movie(id, title, year, rating, genre, description, posterPath, filePath, trailerPath);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse movie data: " + e.getMessage());
        }
    }
    public static Movie fromString(String str) {
        return fromFileString(str);
    }
}