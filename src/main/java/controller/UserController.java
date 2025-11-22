package controller;

import model.User;
import util.FileUtil;

import java.io.IOException;
import java.util.List;

public class UserController {
    public static boolean deleteUser(String email) throws IOException {
        if (email == null || email.isEmpty()) {
            return false;
        }

        List<String> lines = FileUtil.readAllLines();
        boolean userFound = false;

        // Remove user
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");

            if (parts.length >= 3 && parts[2].equals(email)) {
                lines.remove(i);
                userFound = true;
                break;
            }
        }

        if (userFound) {
            // Save user file without the deleted user
            FileUtil.writeAllLines(lines);

            // Delete associated payment information
            try {
                PaymentController.deletePayment(email);
            } catch (IOException e) {
                // Log the error but don't abort the user deletion
                System.err.println("Error deleting payment for user " + email + ": " + e.getMessage());
            }

            return true;
        }

        return false;
    }
}