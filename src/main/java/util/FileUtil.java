package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static final String BASE_PATH = "C:/your-path/FLIXGO/data/";
    private static final String USERS_FILE = "users.txt";
    private static final String ADMINS_FILE = "admins.txt";
    private static final String PAYMENTS_FILE = "payments.txt";
    private static final String TICKETS_FILE = "tickets.txt";
    private static final String MOVIES_FILE = "movies.txt";
    private static final String REVIEWS_FILE = "reviews.txt";
    private static final String WATCHED_MOVIES_FILE = "watched_movies.txt";

    static {
        try {
            File dir = new File(BASE_PATH);
            if (!dir.exists()) {
                boolean dirsCreated = dir.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("Failed to create directories: " + BASE_PATH);
                }
            }

            createFileIfNotExists(PAYMENTS_FILE);
            createFileIfNotExists(USERS_FILE);
            createFileIfNotExists(ADMINS_FILE);
            createFileIfNotExists(TICKETS_FILE);
            createFileIfNotExists(MOVIES_FILE);
            createFileIfNotExists(REVIEWS_FILE);
            createFileIfNotExists(WATCHED_MOVIES_FILE);

            initializeAdminData();
        } catch (IOException e) {
            System.err.println("Error initializing data directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createFileIfNotExists(String filename) throws IOException {
        File file = new File(BASE_PATH + filename);
        if (!file.exists()) {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                throw new IOException("Failed to create file: " + filename);
            }
        }
    }

    public static List<String> readAllPaymentLines() throws IOException {
        return readAllLines(PAYMENTS_FILE);
    }

    public static void writeAllPaymentLines(List<String> lines) throws IOException {
        writeAllLines(PAYMENTS_FILE, lines);
    }

    public static List<String> readAllLines() throws IOException {
        return readAllLines(USERS_FILE);
    }

    public static void writeAllLines(List<String> lines) throws IOException {
        writeAllLines(USERS_FILE, lines);
    }

    public static List<String> readAllTicketLines() throws IOException {
        return readAllLines(TICKETS_FILE);
    }

    public static void writeAllTicketLines(List<String> lines) throws IOException {
        writeAllLines(TICKETS_FILE, lines);
    }

    public static List<String> readAllMovieLines() throws IOException {
        return readAllLines(MOVIES_FILE);
    }

    public static void writeAllMovieLines(List<String> lines) throws IOException {
        writeAllLines(MOVIES_FILE, lines);
    }

    public static List<String> readAllReviewLines() throws IOException {
        return readAllLines(REVIEWS_FILE);
    }

    public static void writeAllReviewLines(List<String> lines) throws IOException {
        writeAllLines(REVIEWS_FILE, lines);
    }

    public static List<String> readAllLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(BASE_PATH + filename);

        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public static void writeAllLines(String filename, List<String> lines) throws IOException {
        File file = new File(BASE_PATH + filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static void initializeAdminData() throws IOException {
        List<String> adminLines = readAllLines(ADMINS_FILE);

        if (adminLines.isEmpty()) {
            List<String> defaultAdmins = new ArrayList<>();
            defaultAdmins.add("admin,admin123,superadmin");
            writeAllLines(ADMINS_FILE, defaultAdmins);
        }
    }

    public static String getFilePath() {
        return BASE_PATH + USERS_FILE;
    }

    public static String getAdminFilePath() {
        return BASE_PATH + ADMINS_FILE;
    }

    public static String getPaymentsFilePath() {
        return BASE_PATH + PAYMENTS_FILE;
    }

    public static String getTicketsFilePath() {
        return BASE_PATH + TICKETS_FILE;
    }

    public static String getMoviesFilePath() {
        return BASE_PATH + MOVIES_FILE;
    }

    public static String getReviewsFilePath() {
        return BASE_PATH + REVIEWS_FILE;
    }

    public static String[] readAllWatchedMovieLines() throws IOException {
        return readAllLinesAsArray(WATCHED_MOVIES_FILE);
    }

    public static void writeAllWatchedMovieLines(String[] lines) throws IOException {
        File file = new File(BASE_PATH + WATCHED_MOVIES_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static String getWatchedMoviesFilePath() {
        return BASE_PATH + WATCHED_MOVIES_FILE;
    }

    private static String[] readAllLinesAsArray(String filename) throws IOException {
        String content = readFileContent(filename);
        if (content.isEmpty()) {
            return new String[0];
        }
        return content.split("\n");
    }

    private static void writeAllLinesFromArray(String filename, String[] lines) throws IOException {
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append("\n");
        }
        writeFileContent(filename, content.toString());
    }

    private static String readFileContent(String filename) throws IOException {
        File file = new File(BASE_PATH + filename);
        if (!file.exists()) {
            return "";
        }

        StringBuilder content = new StringBuilder();
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    content.append(line).append("\n");
                }
            }
        }
        return content.toString();
    }

    private static void writeFileContent(String filename, String content) throws IOException {
        File file = new File(BASE_PATH + filename);
        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(content);
        }
    }
}