package com.hotel.service;

import com.hotel.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PaymentService {

    public Payment processPayment(double amount, PaymentMethod method) {
        Payment payment = new Payment(amount, method);
        System.out.println("\nProcessing payment via " + method.getDisplayName() + "...");
        simulateDelay();
        payment.process();
        System.out.println("Payment authorized successfully!");
        return payment;
    }

    /**
     * Refund policy:
     *   >= 7 days before check-in  → 100% refund
     *   3–6 days before check-in   →  50% refund
     *   < 3 days before check-in   →   0% refund
     */
    public double calculateRefund(Reservation reservation) {
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), reservation.getCheckIn());
        if (daysUntil >= 7) return reservation.getTotalAmount();
        if (daysUntil >= 3) return reservation.getTotalAmount() * 0.50;
        return 0.0;
    }

    private void simulateDelay() {
        try { Thread.sleep(1200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
