package com.hotel.model;

public class Room {
    private int roomNumber;
    private RoomType type;
    private double pricePerNight;
    private boolean available;
    private String description;

    public Room(int roomNumber, RoomType type, double pricePerNight, boolean available, String description) {
        this.roomNumber   = roomNumber;
        this.type         = type;
        this.pricePerNight = pricePerNight;
        this.available    = available;
        this.description  = description;
    }

    public int     getRoomNumber()   { return roomNumber; }
    public RoomType getType()        { return type; }
    public double  getPricePerNight(){ return pricePerNight; }
    public boolean isAvailable()     { return available; }
    public String  getDescription()  { return description; }
    public void    setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("Room %-4d | %-10s | $%.2f/night | %-9s | %s",
            roomNumber, type.getDisplayName(), pricePerNight,
            available ? "Available" : "Occupied", description);
    }

    public String toCsv() {
        return roomNumber + "," + type.name() + "," + pricePerNight + "," + available + "," + description;
    }

    public static Room fromCsv(String csv) {
        String[] p = csv.split(",", 5);
        return new Room(
            Integer.parseInt(p[0].trim()),
            RoomType.valueOf(p[1].trim()),
            Double.parseDouble(p[2].trim()),
            Boolean.parseBoolean(p[3].trim()),
            p[4].trim()
        );
    }
}
