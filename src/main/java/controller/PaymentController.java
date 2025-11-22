package controller;

import model.Payment;
import util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaymentController {
    public static void savePayment(Payment payment) throws IOException {
        // Validate payment data
        if (payment.getEmail() == null || payment.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (payment.getCardNumber() == null || payment.getCardNumber().length() != 16 ||
                !payment.getCardNumber().matches("\\d+")) {
            throw new IllegalArgumentException("Invalid card number");
        }

        if (payment.getExpiryDate() == null || !payment.getExpiryDate().matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            throw new IllegalArgumentException("Invalid expiry date format (MM/YY)");
        }

        if (payment.getCvv() == null || payment.getCvv().length() != 3 || !payment.getCvv().matches("\\d+")) {
            throw new IllegalArgumentException("Invalid CVV");
        }

        if (payment.getCardHolder() == null || payment.getCardHolder().trim().isEmpty()) {
            throw new IllegalArgumentException("Card holder name is required");
        }

        List<String> paymentLines = FileUtil.readAllPaymentLines();

        // Remove existing payment for this user if exists
        paymentLines.removeIf(line -> line.startsWith(payment.getEmail() + ","));

        // Add new payment
        paymentLines.add(payment.toFileString());

        FileUtil.writeAllPaymentLines(paymentLines);
    }

    public static Payment getPaymentByEmail(String email) throws IOException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        List<String> paymentLines = FileUtil.readAllPaymentLines();

        for (String line : paymentLines) {
            if (line.startsWith(email + ",")) {
                return Payment.fromFileString(line);
            }
        }
        return null;
    }

    public static void deletePayment(String email) throws IOException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        List<String> paymentLines = FileUtil.readAllPaymentLines();
        boolean paymentFound = false;

        // Remove payment for this user
        for (int i = 0; i < paymentLines.size(); i++) {
            if (paymentLines.get(i).startsWith(email + ",")) {
                paymentLines.remove(i);
                paymentFound = true;
                break;
            }
        }

        // Only write back to file if we actually removed something
        if (paymentFound) {
            FileUtil.writeAllPaymentLines(paymentLines);
        }
    }
}