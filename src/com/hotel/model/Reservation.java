package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Reservation {
    private final String reservationId;
    private final Customer customer;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private ReservationStatus status;
    private final Payment payment;
    private final double totalAmount;

    public Reservation(Customer customer, Room room, LocalDate checkIn, LocalDate checkOut, Payment payment) {
        this.reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.customer      = customer;
        this.room          = room;
        this.checkIn       = checkIn;
        this.checkOut      = checkOut;
        this.status        = ReservationStatus.CONFIRMED;
        this.payment       = payment;
        this.totalAmount   = getNights() * room.getPricePerNight();
    }

    public Reservation(String reservationId, Customer customer, Room room,
                       LocalDate checkIn, LocalDate checkOut,
                       ReservationStatus status, Payment payment, double totalAmount) {
        this.reservationId = reservationId;
        this.customer      = customer;
        this.room          = room;
        this.checkIn       = checkIn;
        this.checkOut      = checkOut;
        this.status        = status;
        this.payment       = payment;
        this.totalAmount   = totalAmount;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public String            getReservationId() { return reservationId; }
    public Customer          getCustomer()       { return customer; }
    public Room              getRoom()           { return room; }
    public LocalDate         getCheckIn()        { return checkIn; }
    public LocalDate         getCheckOut()       { return checkOut; }
    public ReservationStatus getStatus()         { return status; }
    public Payment           getPayment()        { return payment; }
    public double            getTotalAmount()    { return totalAmount; }

    public void printDetails() {
        String div = "  +------------------------------------------------------+";
        System.out.println(div);
        System.out.println("  |          BOOKING CONFIRMATION DETAILS                |");
        System.out.println(div);
        System.out.printf( "  |  Reservation ID  : %-33s|%n", reservationId);
        System.out.printf( "  |  Status          : %-33s|%n", status);
        System.out.println(div);
        System.out.printf( "  |  Guest Name      : %-33s|%n", customer.getName());
        System.out.printf( "  |  Email           : %-33s|%n", customer.getEmail());
        System.out.printf( "  |  Phone           : %-33s|%n", customer.getPhone());
        System.out.println(div);
        System.out.printf( "  |  Room Number     : %-33s|%n", room.getRoomNumber());
        System.out.printf( "  |  Room Type       : %-33s|%n", room.getType().getDisplayName());
        System.out.printf( "  |  Description     : %-33s|%n", truncate(room.getDescription(), 33));
        System.out.printf( "  |  Check-In        : %-33s|%n", checkIn);
        System.out.printf( "  |  Check-Out       : %-33s|%n", checkOut);
        System.out.printf( "  |  Nights          : %-33s|%n", getNights());
        System.out.printf( "  |  Rate / Night    : $%-32.2f|%n", room.getPricePerNight());
        System.out.println(div);
        System.out.printf( "  |  Payment Method  : %-33s|%n", payment.getMethod().getDisplayName());
        System.out.printf( "  |  Transaction ID  : %-33s|%n",
            payment.isProcessed() ? payment.getTransactionId() : "Pending");
        System.out.printf( "  |  TOTAL AMOUNT    : $%-32.2f|%n", totalAmount);
        System.out.println(div);
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    public String toCsv() {
        return String.join(",",
            reservationId,
            customer.getCustomerId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone(),
            String.valueOf(room.getRoomNumber()),
            checkIn.toString(),
            checkOut.toString(),
            status.name(),
            payment.getPaymentId(),
            payment.getMethod().name(),
            String.valueOf(payment.getAmount()),
            String.valueOf(payment.isProcessed()),
            payment.getTransactionId(),
            String.valueOf(totalAmount)
        );
    }
}
