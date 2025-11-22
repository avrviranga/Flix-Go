package service;

import model.Admin;
import util.FileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    private static final String ADMINS_FILE = "admins.txt";

    public Admin authenticateAdmin(String username, String password) throws IOException {
        List<String> lines = FileUtil.readAllLines(ADMINS_FILE);

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 3 && parts[0].equals(username) && parts[1].equals(password)) {
                return new Admin(parts[0], parts[1], parts[2]);
            }
        }
        return null;
    }

    public List<Admin> getAllAdmins() throws IOException {
        List<String> lines = FileUtil.readAllLines(ADMINS_FILE);
        List<Admin> admins = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                Admin admin = new Admin(parts[0], parts[1], parts[2]);
                if (parts.length > 3) {
                    admin.setLastLogin(parts[3]);
                }
                admins.add(admin);
            }
        }
        return admins;
    }

    public boolean addAdmin(Admin admin) throws IOException {
        List<String> lines = FileUtil.readAllLines(ADMINS_FILE);

        // Check if admin already exists
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(admin.getUsername())) {
                return false;
            }
        }

        // Add new admin
        lines.add(adminToString(admin));
        FileUtil.writeAllLines(ADMINS_FILE, lines);
        return true;
    }

    public boolean updateAdmin(String originalUsername, Admin updatedAdmin) throws IOException {
        List<String> lines = FileUtil.readAllLines(ADMINS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equals(originalUsername)) {
                // Check if new username already exists (unless it's the same admin)
                if (!originalUsername.equals(updatedAdmin.getUsername())) {
                    for (String otherLine : lines) {
                        String[] otherParts = otherLine.split(",");
                        if (otherParts[0].equals(updatedAdmin.getUsername())) {
                            return false;
                        }
                    }
                }
                updatedLines.add(adminToString(updatedAdmin));
                found = true;
            } else {
                updatedLines.add(line);
            }
        }

        if (found) {
            FileUtil.writeAllLines(ADMINS_FILE, updatedLines);
            return true;
        }
        return false;
    }

    public boolean deleteAdmin(String username) throws IOException {
        List<String> lines = FileUtil.readAllLines(ADMINS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        for (String line : lines) {
            String[] parts = line.split(",");
            if (!parts[0].equals(username)) {
                updatedLines.add(line);
            } else {
                found = true;
            }
        }

        if (found) {
            FileUtil.writeAllLines(ADMINS_FILE, updatedLines);
            return true;
        }
        return false;
    }

    private String adminToString(Admin admin) {
        return admin.getUsername() + "," + admin.getPassword() + "," + admin.getRole() +
                (admin.getLastLogin() != null ? "," + admin.getLastLogin() : "");
    }
}