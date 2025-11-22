package controller;

import service.AdminService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.json.JSONObject;

@WebServlet(name = "AdminCountServlet", urlPatterns = {"/get-admin-count"})
public class AdminCountServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Admin authentication required");
            return;
        }

        try {
            int adminCount = adminService.getAllAdmins().size();

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("adminCount", adminCount);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching admin count: " + e.getMessage());
        }
    }
}