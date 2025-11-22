package model;

import java.io.Serializable;

public class User implements Serializable {
    private String fullName;
    private String email;
    private String password;

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return fullName + "," + email + "," + password;
    }

    public static User fromString(String userStr) {
        String[] parts = userStr.split(",");
        if (parts.length == 3) {
            return new User(parts[0], parts[1], parts[2]);
        }
        return null;
    }
}