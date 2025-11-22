package controller;

import model.User;
import model.Admin;
import service.UserService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "AdminUserManagementServlet", urlPatterns = {"/get-all-users", "/admin-add-user", "/admin-delete-user"})
public class AdminUserManagementServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        // Check if user is authenticated as admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
            return;
        }

        if ("/get-all-users".equals(path)) {
            getAllUsers(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        // Check if user is authenticated as admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
            return;
        }

        if ("/admin-add-user".equals(path)) {
            addUser(request, response);
        } else if ("/admin-delete-user".equals(path)) {
            deleteUser(request, response);
        }
    }

    private void getAllUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<User> users = userService.getAllUsers();
            JSONArray jsonArray = new JSONArray();

            for (User user : users) {
                JSONObject userJson = new JSONObject();
                userJson.put("fullName", user.getFullName());
                userJson.put("email", user.getEmail());
                // We would add joinedDate if it were available in the User model
                // For now, we'll just omit it

                jsonArray.put(userJson);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching users: " + e.getMessage());
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Parse request body as JSON
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());

            // Extract user data
            String fullName = json.getString("fullName");
            String email = json.getString("email");
            String password = json.getString("password");

            // Validate data
            if (fullName == null || fullName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    password == null || password.length() < 8) {

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user data");
                return;
            }

            // Create and register user
            User newUser = new User(fullName, email, password);
            boolean isRegistered = userService.registerUser(newUser);

            if (isRegistered) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", true);
                responseJson.put("message", "User added successfully");

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(responseJson.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", false);
                responseJson.put("message", "Email already registered");

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(responseJson.toString());
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding user: " + e.getMessage());
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Parse request body as JSON
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());

            // Extract email
            String email = json.getString("email");

            // Validate email
            if (email == null || email.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid email");
                return;
            }

            // Delete user
            boolean isDeleted = userService.deleteUser(email);

            // Also delete user's payment information
            try {
                PaymentController.deletePayment(email);
            } catch (Exception e) {
                // Log the error but continue with user deletion response
                System.err.println("Error deleting payment for user " + email + ": " + e.getMessage());
            }

            if (isDeleted) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", true);
                responseJson.put("message", "User deleted successfully");

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(responseJson.toString());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting user: " + e.getMessage());
        }
    }
}