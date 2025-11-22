package controller;
import model.User;
import service.UserService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.json.JSONObject;
@WebServlet("/update-user")
public class UpdateUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        UserService userService = new UserService();

        try {
            // Parse JSON request
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject json = new JSONObject(sb.toString());

            // Verify current password
            if (json.has("currentPassword")) {
                if (!currentUser.getPassword().equals(json.getString("currentPassword"))) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Current password is incorrect");
                    return;
                }
            }

            // Update user
            boolean updated = userService.updateUser(
                    currentUser.getEmail(),
                    json.optString("newPassword", null),
                    json.optString("fullName", null)
            );

            if (updated) {
                // Update session with new data
                if (json.has("fullName")) {
                    currentUser.setFullName(json.getString("fullName"));
                }
                if (json.has("newPassword")) {
                    currentUser.setPassword(json.getString("newPassword"));
                }

                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Profile updated successfully\"}");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Update failed");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}