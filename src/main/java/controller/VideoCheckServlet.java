package controller;

import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/api/check-video")
public class VideoCheckServlet extends HttpServlet {
    private static final String MOVIES_DIR = System.getProperty("user.dir") +
            "/src/main/resources/Movies/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String filename = request.getParameter("file");
        File file = new File(MOVIES_DIR + filename);

        JsonObject json = new JsonObject();
        json.addProperty("exists", file.exists());
        json.addProperty("path", file.getAbsolutePath());
        json.addProperty("size", file.length());
        json.addProperty("readable", file.canRead());

        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().print(json.toString());
    }
}