package controller;

import model.Admin;
import service.AdminService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;


@WebServlet(name = "AdminManagementServlet",
        urlPatterns = {"/get-all-admins", "/admin-add-admin", "/admin-update-admin", "/admin-delete-admin"})
public class AdminManagementServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
            return;
        }

        if ("/get-all-admins".equals(path)) {
            getAllAdmins(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
            return;
        }

        if ("/admin-add-admin".equals(path)) {
            addAdmin(request, response);
        } else if ("/admin-update-admin".equals(path)) {
            updateAdmin(request, response);
        } else if ("/admin-delete-admin".equals(path)) {
            deleteAdmin(request, response);
        }
    }

    private void getAllAdmins(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Admin> admins = adminService.getAllAdmins();
            JSONArray jsonArray = new JSONArray();

            for (Admin admin : admins) {
                JSONObject adminJson = new JSONObject();
                adminJson.put("username", admin.getUsername());
                adminJson.put("role", admin.getRole());
                adminJson.put("lastLogin", admin.getLastLogin());
                jsonArray.put(adminJson);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching admins: " + e.getMessage());
        }
    }

    private void addAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());
            String username = json.getString("username");
            String password = json.getString("password");
            String role = json.getString("role");

            if (username == null || username.trim().isEmpty() ||
                    password == null || password.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid admin data");
                return;
            }

            Admin newAdmin = new Admin(username, password, role);
            boolean isAdded = adminService.addAdmin(newAdmin);

            if (isAdded) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", true);
                response.setContentType("application/json");
                response.getWriter().write(responseJson.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", false);
                responseJson.put("message", "Username already exists");
                response.setContentType("application/json");
                response.getWriter().write(responseJson.toString());
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding admin: " + e.getMessage());
        }
    }

    private void updateAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());
            String originalUsername = json.getString("originalUsername");
            String username = json.getString("username");
            String password = json.optString("password", null);
            String role = json.getString("role");

            if (originalUsername == null || username == null || username.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid admin data");
                return;
            }

            Admin updatedAdmin = new Admin(username, password != null ? password : "", role);
            boolean isUpdated = adminService.updateAdmin(originalUsername, updatedAdmin);

            if (isUpdated) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", true);
                response.setContentType("application/json");
                response.getWriter().write(responseJson.toString());
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", false);
                responseJson.put("message", "Username already exists or admin not found");
                response.setContentType("application/json");
                response.getWriter().write(responseJson.toString());
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating admin: " + e.getMessage());
        }
    }

    private void deleteAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            JSONObject json = new JSONObject(sb.toString());
            String username = json.getString("username");

            if (username == null || username.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid username");
                return;
            }

            boolean isDeleted = adminService.deleteAdmin(username);

            if (isDeleted) {
                JSONObject responseJson = new JSONObject();
                responseJson.put("success", true);
                response.setContentType("application/json");
                response.getWriter().write(responseJson.toString());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Admin not found");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting admin: " + e.getMessage());
        }
    }
}