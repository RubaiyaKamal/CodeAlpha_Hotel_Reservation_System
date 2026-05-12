package com.hotel.model;

public enum RoomType {
    STANDARD("Standard", 80.0),
    DELUXE("Deluxe", 150.0),
    SUITE("Suite", 300.0);

    private final String displayName;
    private final double basePrice;

    RoomType(String displayName, double basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() { return displayName; }
    public double getBasePrice()   { return basePrice; }

    public static RoomType fromString(String s) {
        for (RoomType t : values()) {
            if (t.name().equalsIgnoreCase(s) || t.displayName.equalsIgnoreCase(s)) return t;
        }
        throw new IllegalArgumentException("Unknown room type: " + s);
    }
}
