package controller;

import model.User;
import service.UserService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.json.JSONObject;

@WebServlet(name = "UserCountServlet", urlPatterns = {"/get-user-count"})
public class UserCountServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is authenticated as admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
            return;
        }

        try {
            int userCount = userService.getAllUsers().size();

            // Create JSON response
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("userCount", userCount);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching user count: " + e.getMessage());
        }
    }
}