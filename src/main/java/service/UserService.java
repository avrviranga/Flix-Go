package service;

import model.User;
import util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // Register a new user
    public boolean registerUser(User user) throws IOException {
        List<String> lines = FileUtil.readAllLines();

        // Check if user already exists by email
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[1].equals(user.getEmail())) {
                return false; // User already exists
            }
        }

        // Add new user
        lines.add(user.toString());
        FileUtil.writeAllLines(lines);
        return true;
    }

    // Authenticate user by email and password
    public User authenticateUser(String email, String password) throws IOException {
        List<String> lines = FileUtil.readAllLines();

        for (String line : lines) {
            User user = User.fromString(line);
            if (user != null && user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Update user's name and/or password
    public boolean updateUser(String email, String newPassword, String newName) throws IOException {
        List<String> lines = FileUtil.readAllLines();
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        for (String line : lines) {
            User user = User.fromString(line);
            if (user != null && user.getEmail().equals(email)) {
                if (newName != null && !newName.isEmpty()) {
                    user.setFullName(newName);
                }
                if (newPassword != null && !newPassword.isEmpty()) {
                    user.setPassword(newPassword);
                }
                found = true;
                updatedLines.add(user.toString());
            } else {
                updatedLines.add(line);
            }
        }

        if (found) {
            FileUtil.writeAllLines(updatedLines);
            return true;
        }
        return false;
    }
    // Add this method to UserService.java
    public boolean deleteUser(String email) throws IOException {
        List<String> lines = FileUtil.readAllLines();
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        for (String line : lines) {
            User user = User.fromString(line);
            if (user != null && !user.getEmail().equals(email)) {
                updatedLines.add(line);
            } else if (user != null && user.getEmail().equals(email)) {
                found = true;
            }
        }

        if (found) {
            FileUtil.writeAllLines(updatedLines);
            return true;
        }
        return false;
    }
    public List<User> getAllUsers() throws IOException {
        List<String> lines = FileUtil.readAllLines();
        List<User> users = new ArrayList<>();

        for (String line : lines) {
            User user = User.fromString(line);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }

}
