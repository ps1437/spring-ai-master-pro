package com.syscho.ai.tools.database.booking.db;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassengerNameIgnoreCase(String passengerName);
    List<Booking> findByStatus(String status);
}