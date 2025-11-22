package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import model.Admin;
import service.AdminService;
import util.FileUtil;

@WebServlet(name = "AdminAuthController", urlPatterns = {"/admin-login", "/admin-logout"})
public class AdminAuthController extends HttpServlet {

    private final AdminService adminService = new AdminService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/admin-login".equals(path)) {
            handleAdminLogin(request, response);
        } else if ("/admin-logout".equals(path)) {
            handleAdminLogout(request, response);
        }
    }

    private void handleAdminLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("admin-username");
        String password = request.getParameter("admin-password");

        try {
            Admin admin = adminService.authenticateAdmin(username, password);

            if (admin != null) {
                HttpSession session = request.getSession();
                session.setAttribute("admin", admin);
                response.sendRedirect("adminDashboard.html");
            } else {
                request.setAttribute("error", "Invalid admin credentials");
                request.getRequestDispatcher("adminLogin.html").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "System error. Please try again.");
            request.getRequestDispatcher("adminLogin.html").forward(request, response);
        }
    }

    private void handleAdminLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("adminLogin.html");
    }
}