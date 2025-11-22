package model;

public class Payment {
    private String email;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardHolder;

    public Payment(String email, String cardNumber, String expiryDate, String cvv, String cardHolder) {
        this.email = email;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardHolder = cardHolder;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    // Convert to file format
    public String toFileString() {
        return String.join(",",
                email,
                cardNumber,
                expiryDate,
                cvv,
                cardHolder
        );
    }

    // Create from file string
    public static Payment fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        if (parts.length != 5) {
            return null;
        }
        return new Payment(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim()
        );
    }
}