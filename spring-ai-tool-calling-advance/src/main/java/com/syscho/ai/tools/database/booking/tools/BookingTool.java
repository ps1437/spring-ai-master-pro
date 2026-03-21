package com.syscho.ai.tools.database.booking.tools;

import com.syscho.ai.tools.database.booking.db.Booking;
import com.syscho.ai.tools.database.booking.db.BookingRepository;
import com.syscho.ai.tools.database.booking.db.CreateBookingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingTool {

    private final BookingRepository repo;

    @Tool(name = "createBooking", description = "Create a new flight booking for a passenger")
    public String createBooking(
            @ToolParam(description = "Booking details including passenger name, from, to and flight number")
            CreateBookingRequest request
    ) {
        log.info("[tool=createBooking] request={}", request);

        Booking booking = repo.save(Booking.builder()
                .passengerName(request.passengerName())
                .from(request.from())
                .to(request.to())
                .flightNumber(request.flightNumber())
                .status("CONFIRMED")
                .build());

        return "Booking created successfully. ID: " + booking.getId()
                + " | Flight: " + request.flightNumber()
                + " | " + request.from() + " -> " + request.to()
                + " | Status: CONFIRMED";
    }

    @Tool(name = "getBooking", description = "Get booking details by booking ID")
    public String getBooking(
            @ToolParam(description = "Booking ID") Long bookingId
    ) {
        log.info("[tool=getBooking] id={}", bookingId);

        return repo.findById(bookingId)
                .map(b -> "Booking #" + b.getId()
                        + " | Passenger: " + b.getPassengerName()
                        + " | Flight: " + b.getFlightNumber()
                        + " | Route: " + b.getFrom() + " -> " + b.getTo()
                        + " | Status: " + b.getStatus())
                .orElse("No booking found with ID: " + bookingId);
    }

    @Tool(name = "getBookingsByPassenger", description = "Get all bookings for a passenger by name")
    public String getBookingsByPassenger(
            @ToolParam(description = "Passenger full name") String passengerName
    ) {
        log.info("[tool=getBookingsByPassenger] passenger={}", passengerName);

        List<Booking> bookings = repo.findByPassengerNameIgnoreCase(passengerName);

        if (bookings.isEmpty()) {
            return "No bookings found for passenger: " + passengerName;
        }

        StringBuilder sb = new StringBuilder("Bookings for " + passengerName + ":\n");
        for (Booking b : bookings) {
            sb.append("  #").append(b.getId())
              .append(" | ").append(b.getFlightNumber())
              .append(" | ").append(b.getFrom()).append(" -> ").append(b.getTo())
              .append(" | ").append(b.getStatus())
              .append("\n");
        }
        return sb.toString();
    }

    @Tool(name = "cancelBooking", description = "Cancel an existing booking by ID")
    public String cancelBooking(
            @ToolParam(description = "Booking ID to cancel") Long bookingId
    ) {
        log.info("[tool=cancelBooking] id={}", bookingId);

        return repo.findById(bookingId)
                .map(b -> {
                    if ("CANCELLED".equals(b.getStatus())) {
                        return "Booking #" + bookingId + " is already cancelled.";
                    }
                    b.setStatus("CANCELLED");
                    repo.save(b);
                    return "Booking #" + bookingId + " has been cancelled successfully.";
                })
                .orElse("No booking found with ID: " + bookingId);
    }

    @Tool(name = "updateBookingStatus", description = "Update the status of a booking")
    public String updateBookingStatus(
            @ToolParam(description = "Booking ID") Long bookingId,
            @ToolParam(description = "New status: CONFIRMED, CANCELLED, or PENDING") String status
    ) {
        log.info("[tool=updateBookingStatus] id={} status={}", bookingId, status);

        if (!List.of("CONFIRMED", "CANCELLED", "PENDING").contains(status.toUpperCase())) {
            return "Invalid status. Must be one of: CONFIRMED, CANCELLED, PENDING";
        }

        return repo.findById(bookingId)
                .map(b -> {
                    b.setStatus(status.toUpperCase());
                    repo.save(b);
                    return "Booking #" + bookingId + " status updated to: " + status.toUpperCase();
                })
                .orElse("No booking found with ID: " + bookingId);
    }
}