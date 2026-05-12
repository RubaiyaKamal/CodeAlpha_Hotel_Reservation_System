package com.hotel.service;

import com.hotel.model.*;
import com.hotel.storage.FileStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReservationService {
    private final List<Reservation> reservations;
    private final FileStorage storage;
    private final RoomService roomService;
    private final PaymentService paymentService;

    public ReservationService(FileStorage storage, RoomService roomService, PaymentService paymentService) {
        this.storage        = storage;
        this.roomService    = roomService;
        this.paymentService = paymentService;
        this.reservations   = storage.loadReservations(roomService.getAllRooms());
    }

    public Reservation makeReservation(Customer customer, Room room,
                                       LocalDate checkIn, LocalDate checkOut,
                                       PaymentMethod method) {
        long nights  = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        double total = nights * room.getPricePerNight();
        Payment payment = paymentService.processPayment(total, method);

        Reservation reservation = new Reservation(customer, room, checkIn, checkOut, payment);
        roomService.setAvailability(room.getRoomNumber(), false);
        reservations.add(reservation);
        storage.saveReservations(reservations);
        return reservation;
    }

    public boolean cancelReservation(String reservationId) {
        Optional<Reservation> opt = reservations.stream()
            .filter(r -> r.getReservationId().equals(reservationId)
                      && r.getStatus() == ReservationStatus.CONFIRMED)
            .findFirst();

        if (opt.isEmpty()) return false;

        Reservation r = opt.get();
        r.cancel();
        roomService.setAvailability(r.getRoom().getRoomNumber(), true);
        storage.saveReservations(reservations);
        return true;
    }

    public Optional<Reservation> findById(String reservationId) {
        return reservations.stream()
            .filter(r -> r.getReservationId().equals(reservationId))
            .findFirst();
    }

    public List<Reservation> findByEmail(String email) {
        return reservations.stream()
            .filter(r -> r.getCustomer().getEmail().equalsIgnoreCase(email))
            .collect(Collectors.toList());
    }

    public List<Reservation> getAll() {
        return Collections.unmodifiableList(reservations);
    }
}
