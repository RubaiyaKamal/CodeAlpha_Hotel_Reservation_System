package com.hotel.storage;

import com.hotel.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileStorage {
    private static final String DATA_DIR         = "data";
    private static final String ROOMS_FILE       = DATA_DIR + "/rooms.csv";
    private static final String RESERVATIONS_FILE= DATA_DIR + "/reservations.csv";

    public FileStorage() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ─── Rooms ────────────────────────────────────────────────────────────────

    public void saveRooms(List<Room> rooms) {
        try (PrintWriter w = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            w.println("roomNumber,type,pricePerNight,available,description");
            for (Room r : rooms) w.println(r.toCsv());
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File file = new File(ROOMS_FILE);
        if (!file.exists()) return rooms;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (!line.trim().isEmpty()) rooms.add(Room.fromCsv(line.trim()));
            }
        } catch (IOException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
        }
        return rooms;
    }

    // ─── Reservations ─────────────────────────────────────────────────────────

    public void saveReservations(List<Reservation> reservations) {
        try (PrintWriter w = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
            w.println("reservationId,customerId,customerName,customerEmail,customerPhone," +
                      "roomNumber,checkIn,checkOut,status,paymentId,paymentMethod," +
                      "amount,processed,transactionId,totalAmount");
            for (Reservation r : reservations) w.println(r.toCsv());
        } catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    public List<Reservation> loadReservations(List<Room> rooms) {
        List<Reservation> reservations = new ArrayList<>();
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) return reservations;

        Map<Integer, Room> roomMap = new HashMap<>();
        for (Room r : rooms) roomMap.put(r.getRoomNumber(), r);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", 15);
                if (p.length < 15) continue;

                Customer customer = new Customer(p[1], p[2], p[3], p[4]);
                Room room = roomMap.get(Integer.parseInt(p[5].trim()));
                if (room == null) continue;

                LocalDate checkIn    = LocalDate.parse(p[6].trim());
                LocalDate checkOut   = LocalDate.parse(p[7].trim());
                ReservationStatus status = ReservationStatus.valueOf(p[8].trim());
                Payment payment = new Payment(
                    p[9].trim(),
                    Double.parseDouble(p[11].trim()),
                    PaymentMethod.valueOf(p[10].trim()),
                    Boolean.parseBoolean(p[12].trim()),
                    p[13].trim()
                );
                double totalAmount = Double.parseDouble(p[14].trim());
                reservations.add(new Reservation(p[0].trim(), customer, room,
                    checkIn, checkOut, status, payment, totalAmount));
            }
        } catch (IOException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
        return reservations;
    }
}
