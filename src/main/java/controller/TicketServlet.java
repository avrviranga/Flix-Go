package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.json.JSONObject;
import java.io.PrintWriter;

@WebServlet(name = "TicketServlet", value = "/purchase-ticket")
public class TicketServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject jsonResponse = new JSONObject();
        PrintWriter out = response.getWriter();

        try {
            // Read the request body
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                requestBody.append(line);
            }

            // Parse JSON request
            JSONObject jsonRequest = new JSONObject(requestBody.toString());
            String email = jsonRequest.optString("email");
            String ticketId = jsonRequest.optString("ticketId");
            String price = jsonRequest.optString("price");

            // Process the ticket purchase
            TicketController.saveTicket(email, ticketId, price);

            // Success response
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Ticket purchased successfully");
            jsonResponse.put("ticketId", ticketId);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (IllegalArgumentException e) {
            // Client-side errors (400 Bad Request)
            jsonResponse.put("status", "error");
            jsonResponse.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        } catch (IllegalStateException e) {
            // Conflict errors (409 Conflict)
            jsonResponse.put("status", "error");
            jsonResponse.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);

        } catch (Exception e) {
            // Server errors (500 Internal Server Error)
            jsonResponse.put("status", "error");
            jsonResponse.put("error", "An unexpected error occurred");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(); // Log the error

        } finally {
            out.print(jsonResponse.toString());
            out.flush();
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        JSONObject jsonResponse = new JSONObject();
        PrintWriter out = response.getWriter();

        try {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email parameter is required");
            }

            String ticketDetails = TicketController.getTicketDetails(email);

            if (ticketDetails != null) {
                String[] parts = ticketDetails.split(",");
                jsonResponse.put("email", parts[0]);
                jsonResponse.put("ticketId", parts[1]);
                jsonResponse.put("price", parts[2]);
                jsonResponse.put("purchaseDate", parts.length > 3 ? parts[3] : "");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.put("message", "No ticket found for this user");
            }

        } catch (IllegalArgumentException e) {
            jsonResponse.put("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            jsonResponse.put("error", "An error occurred while fetching ticket details");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } finally {
            out.print(jsonResponse.toString());
            out.flush();
            out.close();
        }
    }
}