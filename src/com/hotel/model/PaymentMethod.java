package com.hotel.model;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    CASH("Cash"),
    ONLINE_TRANSFER("Online Transfer");

    private final String displayName;

    PaymentMethod(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }

    public static PaymentMethod fromString(String s) {
        for (PaymentMethod m : values()) {
            if (m.name().equalsIgnoreCase(s) || m.displayName.equalsIgnoreCase(s)) return m;
        }
        throw new IllegalArgumentException("Unknown payment method: " + s);
    }
}
