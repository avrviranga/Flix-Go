package util;

import model.Movie;
import java.util.List;

public class MovieSorter {
    public static void bubbleSortByRating(List<Movie> movies) {
        if (movies == null || movies.size() <= 1) {
            return;
        }

        boolean swapped;
        int n = movies.size();

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (movies.get(j).getRating() < movies.get(j + 1).getRating()) {
                    // Swap movies
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                    swapped = true;
                }
            }

            // If no two elements were swapped in inner loop, then the list is sorted
            if (!swapped) {
                break;
            }
        }
    }
}