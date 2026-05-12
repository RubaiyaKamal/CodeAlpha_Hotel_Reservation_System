package com.hotel.model;

import java.util.UUID;

public class Payment {
    private final String paymentId;
    private final double amount;
    private final PaymentMethod method;
    private boolean processed;
    private String  transactionId;

    public Payment(double amount, PaymentMethod method) {
        this.paymentId     = "PAY-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.amount        = amount;
        this.method        = method;
        this.processed     = false;
        this.transactionId = "";
    }

    public Payment(String paymentId, double amount, PaymentMethod method, boolean processed, String transactionId) {
        this.paymentId     = paymentId;
        this.amount        = amount;
        this.method        = method;
        this.processed     = processed;
        this.transactionId = transactionId;
    }

    public void process() {
        this.processed     = true;
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String        getPaymentId()    { return paymentId; }
    public double        getAmount()       { return amount; }
    public PaymentMethod getMethod()       { return method; }
    public boolean       isProcessed()     { return processed; }
    public String        getTransactionId(){ return transactionId; }

    @Override
    public String toString() {
        return String.format("Payment[%s] $%.2f via %s | %s | TXN: %s",
            paymentId, amount, method.getDisplayName(),
            processed ? "Processed" : "Pending",
            processed ? transactionId : "N/A");
    }
}
