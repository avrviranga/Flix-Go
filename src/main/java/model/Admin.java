package model;

import java.io.Serializable;

public class Admin implements Serializable {
    private String username;
    private String password;
    private String role; // "superadmin" or "admin"
    private String lastLogin;

    public Admin(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getLastLogin() { return lastLogin; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
}