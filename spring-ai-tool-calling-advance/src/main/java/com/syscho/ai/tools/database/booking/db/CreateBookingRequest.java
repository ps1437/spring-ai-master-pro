package com.syscho.ai.tools.database.booking.db;

import org.springframework.ai.tool.annotation.ToolParam;

public record CreateBookingRequest(
        @ToolParam(description = "Full name of the passenger") String passengerName,
        @ToolParam(description = "Departure city IATA code e.g. BOM") String from,
        @ToolParam(description = "Arrival city IATA code e.g. DEL") String to,
        @ToolParam(description = "Flight number e.g. AI-202") String flightNumber
) {}