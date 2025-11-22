package controller;

import util.FileUtil;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class TicketController {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+\\.\\d{2}$");

    public static void saveTicket(String email, String ticketId, String price) throws IOException {
        // Validate all input parameters
        validateInput(email, ticketId, price);

        List<String> ticketLines = FileUtil.readAllTicketLines();

        // Check for duplicate tickets
        if (hasTicket(email)) {
            throw new IllegalStateException("User already has a ticket");
        }

        // Create ticket record with timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String ticketRecord = String.join(",",
                email,
                ticketId,
                price,
                timestamp
        );

        ticketLines.add(ticketRecord);
        FileUtil.writeAllTicketLines(ticketLines);
    }

    public static boolean hasTicket(String email) throws IOException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        List<String> ticketLines = FileUtil.readAllTicketLines();
        return ticketLines.stream()
                .anyMatch(line -> line.startsWith(email + ","));
    }

    private static void validateInput(String email, String ticketId, String price) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (ticketId == null || ticketId.isEmpty()) {
            throw new IllegalArgumentException("Ticket ID is required");
        }

        if (price == null || price.isEmpty()) {
            throw new IllegalArgumentException("Price is required");
        }

        if (!PRICE_PATTERN.matcher(price).matches()) {
            throw new IllegalArgumentException("Price must be in format 0000.00");
        }
    }

    public static String getTicketDetails(String email) throws IOException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        List<String> ticketLines = FileUtil.readAllTicketLines();
        return ticketLines.stream()
                .filter(line -> line.startsWith(email + ","))
                .findFirst()
                .orElse(null);
    }
}