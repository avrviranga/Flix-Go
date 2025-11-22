package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean isLoggedIn = (session != null && session.getAttribute("admin") != null);
        boolean isLoginRequest = httpRequest.getRequestURI().endsWith("admin-login");
        boolean isLoginPage = httpRequest.getRequestURI().endsWith("adminLogin.html");

        if (isLoggedIn || isLoginRequest || isLoginPage) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/adminLogin.html");
        }
    }

    // Other filter methods
    public void init(FilterConfig fConfig) throws ServletException {}
    public void destroy() {}
}