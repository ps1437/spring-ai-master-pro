package com.syscho.ai.tools.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TravelTools {

    @Tool(name = "searchFlight", description = "Search available flights between two cities")
    public String searchFlights(String from, String to) {
        log.info("Searching flights from {} to {}", from, to);

        return "Flights from " + from + " to " + to + ": AI-202 at 10:30, AI-504 at 18:00";

    }

}