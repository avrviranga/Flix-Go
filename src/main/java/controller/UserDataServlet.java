package controller;

import model.User;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/user-data")
public class UserDataServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            // Make sure to properly escape special characters in the name if needed
            String fullName = user.getFullName() != null ? user.getFullName() : "";
            String email = user.getEmail() != null ? user.getEmail() : "";

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    String.format("{\"fullName\":\"%s\",\"email\":\"%s\"}",
                            fullName.replace("\"", "\\\""),
                            email.replace("\"", "\\\""))
            );
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}