package Stack;

public class MovieHistoryStack {
    private static final int MAX_SIZE = 5;
    private String[] movies;
    private int top;

    public MovieHistoryStack() {
        movies = new String[MAX_SIZE];
        top = -1;
    }

    public void push(String movieId) {
        // First remove if already exists (to avoid duplicates)
        removeMovie(movieId);

        // If stack is full, remove oldest
        if (isFull()) {
            removeOldest();
        }

        // Add to top
        movies[++top] = movieId;
    }

    public String pop() {
        if (isEmpty()) return null;
        return movies[top--];
    }

    public String peek() {
        if (isEmpty()) return null;
        return movies[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == MAX_SIZE - 1;
    }

    public String[] getAllMovies() {
        String[] result = new String[size()];
        for (int i = 0; i <= top; i++) {
            result[i] = movies[i];
        }
        return result;
    }

    public int size() {
        return top + 1;
    }

    private void removeOldest() {
        for (int i = 0; i < top; i++) {
            movies[i] = movies[i + 1];
        }
        top--;
    }

    private void removeMovie(String movieId) {
        for (int i = 0; i <= top; i++) {
            if (movies[i] != null && movies[i].equals(movieId)) {
                // Shift remaining elements
                for (int j = i; j < top; j++) {
                    movies[j] = movies[j + 1];
                }
                top--;
                break;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= top; i++) {
            if (i > 0) sb.append(",");
            sb.append(movies[i]);
        }
        return sb.toString();
    }

    public static MovieHistoryStack fromString(String str) {
        MovieHistoryStack stack = new MovieHistoryStack();
        if (str == null || str.isEmpty()) return stack;

        String[] movieIds = str.split(",");
        for (String movieId : movieIds) {
            if (!movieId.isEmpty()) {
                stack.push(movieId);
            }
        }
        return stack;
    }
}