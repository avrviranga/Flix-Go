package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import model.User;
import service.UserService;
import service.AdminService;
import util.FileUtil;

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get current user's email from session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
                return;
            }

            User user = (User) session.getAttribute("user");
            UserService userService = new UserService();

            // First delete payment information
            try {
                PaymentController.deletePayment(user.getEmail());
                // Log success but continue even if no payment was found
                System.out.println("Payment information deleted for user: " + user.getEmail());
            } catch (Exception e) {
                // Log error but continue with user deletion
                System.err.println("Error deleting payment for user " + user.getEmail() + ": " + e.getMessage());
            }

            // Then delete the user account
            if (userService.deleteUser(user.getEmail())) {
                // Invalidate session
                session.invalidate();

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Account deleted successfully\"}");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete account");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting account: " + e.getMessage());
        }
    }
}