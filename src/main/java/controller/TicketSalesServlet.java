package controller;

import util.FileUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.json.JSONObject;
import java.util.List;

@WebServlet(name = "TicketSalesServlet", urlPatterns = {"/get-ticket-sales"})
public class TicketSalesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
            return;
        }

        try {
            // Calculate total ticket sales
            double totalSales = calculateTotalSales();

            // Calculate weekly sales (last 7 days)
            double weeklySales = calculateWeeklySales();

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("totalSales", totalSales);
            jsonResponse.put("weeklySales", weeklySales);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching ticket sales: " + e.getMessage());
        }
    }

    private double calculateTotalSales() throws IOException {
        List<String> ticketLines = FileUtil.readAllTicketLines();
        double totalSales = 0.0;

        for (String line : ticketLines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                try {
                    double price = Double.parseDouble(parts[2]);
                    totalSales += price;
                } catch (NumberFormatException e) {
                    // Skip invalid price formats
                    System.err.println("Invalid price format in ticket: " + line);
                }
            }
        }

        return totalSales;
    }

    private double calculateWeeklySales() throws IOException {
        List<String> ticketLines = FileUtil.readAllTicketLines();
        double weeklySales = 0.0;

        // Get current time in milliseconds
        long currentTime = System.currentTimeMillis();
        // One week in milliseconds
        long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000;

        for (String line : ticketLines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                try {
                    // Parse the timestamp
                    String timestamp = parts[3];
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date date = dateFormat.parse(timestamp);

                    // Check if the ticket was purchased within the last week
                    if (currentTime - date.getTime() <= oneWeekInMillis) {
                        double price = Double.parseDouble(parts[2]);
                        weeklySales += price;
                    }
                } catch (Exception e) {
                    // Skip entries with invalid format
                    System.err.println("Error processing date for ticket: " + line);
                }
            }
        }

        return weeklySales;
    }
}