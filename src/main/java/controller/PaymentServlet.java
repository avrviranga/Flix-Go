package controller;

import model.Payment;
import util.FileUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PaymentServlet", value = "/payment-methods")
public class PaymentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String email = request.getParameter("email");
            if (email == null || email.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email parameter is required");
                return;
            }

            Payment payment = PaymentController.getPaymentByEmail(email);

            response.setContentType("application/json");
            if (payment != null) {
                // Mask card number for security
                String maskedCard = "•••• •••• •••• " + payment.getCardNumber().substring(payment.getCardNumber().length() - 4);

                String jsonResponse = String.format(
                        "{\"cardNumber\":\"%s\",\"expiryDate\":\"%s\",\"cardHolder\":\"%s\"}",
                        maskedCard, payment.getExpiryDate(), payment.getCardHolder()
                );
                response.getWriter().write(jsonResponse);
            } else {
                response.getWriter().write("{}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            String email = request.getParameter("email");
            String cardNumber = request.getParameter("cardNumber");
            String expiryDate = request.getParameter("expiryDate");
            String cvv = request.getParameter("cvv");
            String cardHolder = request.getParameter("cardHolder");

            if (email == null || email.isEmpty() ||
                    cardNumber == null || cardNumber.isEmpty() ||
                    expiryDate == null || expiryDate.isEmpty() ||
                    cvv == null || cvv.isEmpty() ||
                    cardHolder == null || cardHolder.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"All payment fields are required\"}");
                return;
            }

            // Validate card number (remove spaces and check length)
            String cleanCardNumber = cardNumber.replaceAll("\\s+", "");
            if (cleanCardNumber.length() != 16 || !cleanCardNumber.matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid card number\"}");
                return;
            }

            Payment payment = new Payment(email, cleanCardNumber, expiryDate, cvv, cardHolder);
            PaymentController.savePayment(payment);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Payment method saved successfully\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error saving payment method: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            String email = request.getParameter("email");
            if (email == null || email.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Email parameter is required\"}");
                return;
            }

            PaymentController.deletePayment(email);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Payment method deleted successfully\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error deleting payment method: " + e.getMessage() + "\"}");
        }
    }
}