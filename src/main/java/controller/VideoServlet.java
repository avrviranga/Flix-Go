package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name="VideoServlet", urlPatterns="/videos/*", asyncSupported=true)
public class VideoServlet extends HttpServlet {
    // Correct absolute path to your Movies directory
    private static final String MOVIES_DIR = "C:/your-path/FLIXGO/videos/";
    private static final int BUFFER_SIZE = 16384; // Smaller buffer size for more frequent flushes

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get requested filename
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String fileName = pathInfo.substring(1);
        System.out.println("[DEBUG] Requested video file: " + fileName);

        // Security check
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file path");
            return;
        }

        // Create file object with correct path
        File file = new File(MOVIES_DIR + fileName);
        System.out.println("[DEBUG] Looking for file at: " + file.getAbsolutePath());

        // Verify file exists
        if (!file.exists() || !file.isFile()) {
            System.err.println("[ERROR] File not found at: " + file.getAbsolutePath());
            System.err.println("[DEBUG] Directory contents:");
            File dir = new File(MOVIES_DIR);
            if (dir.exists()) {
                for (String f : dir.list()) {
                    System.err.println("- " + f);
                }
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video file not found");
            return;
        }

        // Set proper headers
        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            mimeType = "video/mp4"; // Default to mp4
        }

        long fileLength = file.length();
        long rangeStart = 0;
        long rangeEnd = fileLength - 1;
        boolean isPartial = false;

        // Handle range requests - crucial for proper video streaming
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            // Parse the range header
            List<Range> ranges = parseRange(rangeHeader, fileLength);

            if (ranges.isEmpty()) {
                response.setHeader("Content-Range", "bytes */" + fileLength);
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }

            // For simplicity, we'll just handle the first range
            Range range = ranges.get(0);
            rangeStart = range.start;
            rangeEnd = range.end;
            isPartial = true;

            // Set partial content status and Content-Range header
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
            System.out.println("[DEBUG] Serving range: " + rangeStart + "-" + rangeEnd + "/" + fileLength);
        }

        // Set content headers
        response.setContentType(mimeType);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD");
        response.setHeader("Connection", "keep-alive"); // Add this line
        response.setHeader("Content-Length", String.valueOf((rangeEnd - rangeStart) + 1));
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        response.setBufferSize(BUFFER_SIZE);

        // Stream the content
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             OutputStream out = response.getOutputStream()) {

            // Seek to the requested part of the file
            if (rangeStart > 0) {
                raf.seek(rangeStart);
            }

            // Create a buffer
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = (rangeEnd - rangeStart) + 1;
            int read;

            // Stream the file data
            while (remaining > 0) {
                read = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1) {
                    break;
                }
                out.write(buffer, 0, read);
                out.flush(); // Flush after each write to ensure data is sent immediately
                remaining -= read;
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Video streaming failed: " + e.getMessage());
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving video");
            }
        }
    }

    // Class to represent a byte range
    private static class Range {
        long start;
        long end;

        Range(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    // Parse Range header
    private List<Range> parseRange(String rangeHeader, long fileLength) {
        List<Range> ranges = new ArrayList<>();

        if (rangeHeader.startsWith("bytes=")) {
            rangeHeader = rangeHeader.substring(6);
            String[] rangesArray = rangeHeader.split(",");

            for (String rangeStr : rangesArray) {
                rangeStr = rangeStr.trim();
                Matcher matcher = Pattern.compile("([0-9]*)-([0-9]*)").matcher(rangeStr);

                if (matcher.matches()) {
                    String startStr = matcher.group(1);
                    String endStr = matcher.group(2);

                    try {
                        long start, end;

                        if (startStr.isEmpty()) {
                            // If no start is specified, it means the last n bytes are requested
                            start = fileLength - Long.parseLong(endStr);
                            end = fileLength - 1;
                        } else {
                            start = Long.parseLong(startStr);

                            if (endStr.isEmpty()) {
                                end = fileLength - 1;
                            } else {
                                end = Long.parseLong(endStr);
                            }
                        }

                        // Validate range
                        if (start >= fileLength || end >= fileLength || start > end) {
                            continue; // Skip invalid range
                        }

                        ranges.add(new Range(start, end));
                    } catch (NumberFormatException e) {
                        // Skip invalid range
                    }
                }
            }
        }

        return ranges;
    }
}