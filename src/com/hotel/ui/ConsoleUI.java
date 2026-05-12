package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.service.*;
import com.hotel.storage.FileStorage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ConsoleUI() {
        scanner = new Scanner(System.in);
        FileStorage storage = new FileStorage();
        paymentService    = new PaymentService();
        roomService       = new RoomService(storage);
        reservationService= new ReservationService(storage, roomService, paymentService);
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            switch (readInt("  Your choice: ")) {
                case 1 -> searchAndBook();
                case 2 -> viewMyReservations();
                case 3 -> cancelReservation();
                case 4 -> viewAllRooms();
                case 5 -> { running = false; goodbye(); }
                default -> System.out.println("  Invalid choice. Please enter 1-5.");
            }
        }
        scanner.close();
    }

    // ─── Menus ────────────────────────────────────────────────────────────────

    private void printBanner() {
        System.out.println();
        System.out.println("  +======================================================+");
        System.out.println("  |       GRAND HORIZON HOTEL  -  RESERVATION SYSTEM    |");
        System.out.println("  |              *  Welcome to Luxury Living  *          |");
        System.out.println("  +======================================================+");
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("  +---------------------- MAIN MENU ----------------------+");
        System.out.println("  |   1.  Search & Book a Room                           |");
        System.out.println("  |   2.  View My Reservations                           |");
        System.out.println("  |   3.  Cancel a Reservation                           |");
        System.out.println("  |   4.  View All Rooms                                 |");
        System.out.println("  |   5.  Exit                                           |");
        System.out.println("  +------------------------------------------------------+");
    }

    private void goodbye() {
        System.out.println();
        System.out.println("  Thank you for choosing Grand Horizon Hotel. See you soon!");
        System.out.println();
    }

    // ─── Feature 1 : Search & Book ────────────────────────────────────────────

    private void searchAndBook() {
        System.out.println("\n  --- SEARCH AVAILABLE ROOMS ---");
        System.out.println("  Filter by type:");
        System.out.println("    1. All Types");
        System.out.println("    2. Standard   ($80+ /night) - Comfortable & affordable");
        System.out.println("    3. Deluxe     ($150+/night) - Spacious with premium amenities");
        System.out.println("    4. Suite      ($300+/night) - Ultimate luxury experience");
        int typeChoice = readInt("  Select: ");

        List<Room> available;
        switch (typeChoice) {
            case 2  -> available = roomService.searchByType(RoomType.STANDARD);
            case 3  -> available = roomService.searchByType(RoomType.DELUXE);
            case 4  -> available = roomService.searchByType(RoomType.SUITE);
            default -> available = roomService.getAvailableRooms();
        }

        if (available.isEmpty()) {
            System.out.println("  No available rooms match your criteria.");
            return;
        }

        printRoomTable(available, true);

        int roomNo = readInt("\n  Enter room number to book (0 = back): ");
        if (roomNo == 0) return;

        Optional<Room> selected = roomService.findByNumber(roomNo);
        if (selected.isEmpty() || !selected.get().isAvailable()) {
            System.out.println("  Invalid room number or room is not available.");
            return;
        }
        Room room = selected.get();

        // Guest details
        System.out.println("\n  --- GUEST INFORMATION ---");
        String name  = readNonEmpty("  Full Name  : ");
        String email = readNonEmpty("  Email      : ");
        String phone = readNonEmpty("  Phone      : ");
        Customer customer = new Customer(name, email, phone);

        // Dates
        System.out.println("\n  --- STAY DATES ---");
        LocalDate checkIn, checkOut;
        while (true) {
            checkIn  = readDate("  Check-In  (YYYY-MM-DD): ");
            checkOut = readDate("  Check-Out (YYYY-MM-DD): ");
            if (checkOut.isAfter(checkIn)) break;
            System.out.println("  Check-out must be after check-in. Please try again.");
        }

        long   nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        double total  = nights * room.getPricePerNight();

        System.out.println("\n  --- BOOKING SUMMARY ---");
        System.out.printf("  Room    : %d (%s)%n", room.getRoomNumber(), room.getType().getDisplayName());
        System.out.printf("  Dates   : %s  ->  %s  (%d night%s)%n",
            checkIn, checkOut, nights, nights == 1 ? "" : "s");
        System.out.printf("  Rate    : $%.2f / night%n", room.getPricePerNight());
        System.out.printf("  TOTAL   : $%.2f%n", total);

        // Payment method
        System.out.println("\n  --- PAYMENT METHOD ---");
        System.out.println("    1. Credit Card");
        System.out.println("    2. Debit Card");
        System.out.println("    3. Cash");
        System.out.println("    4. Online Transfer");
        int payChoice = readInt("  Select: ");
        PaymentMethod method = switch (payChoice) {
            case 2  -> PaymentMethod.DEBIT_CARD;
            case 3  -> PaymentMethod.CASH;
            case 4  -> PaymentMethod.ONLINE_TRANSFER;
            default -> PaymentMethod.CREDIT_CARD;
        };

        System.out.print("\n  Confirm booking? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("  Booking cancelled.");
            return;
        }

        Reservation reservation = reservationService.makeReservation(customer, room, checkIn, checkOut, method);
        System.out.println("\n  [OK] Booking confirmed! Please keep your Reservation ID safe.");
        System.out.println();
        reservation.printDetails();
    }

    // ─── Feature 2 : View Reservations ────────────────────────────────────────

    private void viewMyReservations() {
        System.out.println("\n  --- MY RESERVATIONS ---");
        String email = readNonEmpty("  Enter your email: ");
        List<Reservation> list = reservationService.findByEmail(email);

        if (list.isEmpty()) {
            System.out.println("  No reservations found for: " + email);
            return;
        }
        System.out.println("\n  Found " + list.size() + " reservation(s) for " + email + ":");
        for (Reservation r : list) {
            System.out.println();
            r.printDetails();
        }
    }

    // ─── Feature 3 : Cancel Reservation ───────────────────────────────────────

    private void cancelReservation() {
        System.out.println("\n  --- CANCEL RESERVATION ---");
        String id = readNonEmpty("  Enter Reservation ID (e.g. RES-ABC123): ").toUpperCase();

        Optional<Reservation> opt = reservationService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("  Reservation not found: " + id);
            return;
        }

        Reservation r = opt.get();
        if (r.getStatus() == ReservationStatus.CANCELLED) {
            System.out.println("  This reservation has already been cancelled.");
            return;
        }

        System.out.println();
        r.printDetails();

        double refund = paymentService.calculateRefund(r);
        System.out.println();
        if (refund > 0) {
            System.out.printf("  Refund policy: $%.2f will be returned to your %s.%n",
                refund, r.getPayment().getMethod().getDisplayName());
        } else {
            System.out.println("  Refund policy: No refund (cancellation within 3 days of check-in).");
        }

        System.out.print("\n  Confirm cancellation? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("  Cancellation aborted.");
            return;
        }

        reservationService.cancelReservation(id);
        System.out.println("  Reservation " + id + " has been cancelled.");
        if (refund > 0) {
            System.out.printf("  Refund of $%.2f is being processed.%n", refund);
        }
    }

    // ─── Feature 4 : View All Rooms ───────────────────────────────────────────

    private void viewAllRooms() {
        System.out.println("\n  --- ALL ROOMS ---");
        printRoomTable(roomService.getAllRooms(), false);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void printRoomTable(List<Room> rooms, boolean availableOnly) {
        System.out.println();
        System.out.printf("  %-6s %-12s %-14s %-11s %s%n",
            "Room#", "Type", "Price/Night", "Status", "Description");
        System.out.println("  " + "-".repeat(72));
        for (Room r : rooms) {
            System.out.printf("  %-6d %-12s $%-13.2f %-11s %s%n",
                r.getRoomNumber(),
                r.getType().getDisplayName(),
                r.getPricePerNight(),
                r.isAvailable() ? "Available" : "Occupied",
                r.getDescription());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number.");
            }
        }
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String val = scanner.nextLine().trim();
            if (!val.isEmpty()) return val;
            System.out.println("  This field cannot be empty.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid date. Use format YYYY-MM-DD (e.g. 2026-06-15).");
            }
        }
    }
}
