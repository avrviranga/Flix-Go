package controller;

import model.User;
import service.UserService;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "AuthController", urlPatterns = {"/register", "/login"})
public class AuthController extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/register".equals(path)) {
            handleRegister(request, response);
        } else if ("/login".equals(path)) {
            handleLogin(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/register.html").forward(request, response);
            return;
        }

        User newUser = new User(fullName, email, password);
        boolean isRegistered = userService.registerUser(newUser);

        if (isRegistered) {
            response.sendRedirect("login.html?registered=true");
        } else {
            request.setAttribute("error", "Email already registered");
            request.getRequestDispatcher("/register.html").forward(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = userService.authenticateUser(email, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("UserDashboard.html");
            } else {
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("/login.html").forward(request, response);
            }
        } catch (IOException e) {
            request.setAttribute("error", "System error. Please try again.");
            request.getRequestDispatcher("/login.html").forward(request, response);
        }
    }

}