package com.hotel.service;

import com.hotel.model.*;
import com.hotel.storage.FileStorage;

import java.util.*;
import java.util.stream.Collectors;

public class RoomService {
    private final List<Room> rooms;
    private final FileStorage storage;

    public RoomService(FileStorage storage) {
        this.storage = storage;
        this.rooms   = storage.loadRooms();
        if (rooms.isEmpty()) seedDefaultRooms();
    }

    private void seedDefaultRooms() {
        rooms.add(new Room(101, RoomType.STANDARD,  80.0, true, "Queen bed with garden view"));
        rooms.add(new Room(102, RoomType.STANDARD,  80.0, true, "Twin beds near the pool"));
        rooms.add(new Room(103, RoomType.STANDARD,  85.0, true, "Corner room with queen bed and city view"));
        rooms.add(new Room(201, RoomType.DELUXE,   150.0, true, "King bed with city panorama and mini-bar"));
        rooms.add(new Room(202, RoomType.DELUXE,   155.0, true, "King bed with pool view and balcony"));
        rooms.add(new Room(203, RoomType.DELUXE,   160.0, true, "Corner room with balcony and sea view"));
        rooms.add(new Room(301, RoomType.SUITE,    300.0, true, "Luxury suite with living area and jacuzzi"));
        rooms.add(new Room(302, RoomType.SUITE,    350.0, true, "Presidential suite with private terrace and butler"));
        storage.saveRooms(rooms);
    }

    public List<Room> getAllRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<Room> getAvailableRooms() {
        return rooms.stream().filter(Room::isAvailable).collect(Collectors.toList());
    }

    public List<Room> searchByType(RoomType type) {
        return rooms.stream()
            .filter(r -> r.getType() == type && r.isAvailable())
            .collect(Collectors.toList());
    }

    public List<Room> searchByMaxPrice(double maxPrice) {
        return rooms.stream()
            .filter(r -> r.getPricePerNight() <= maxPrice && r.isAvailable())
            .collect(Collectors.toList());
    }

    public Optional<Room> findByNumber(int roomNumber) {
        return rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst();
    }

    public void setAvailability(int roomNumber, boolean available) {
        findByNumber(roomNumber).ifPresent(r -> {
            r.setAvailable(available);
            storage.saveRooms(rooms);
        });
    }
}
