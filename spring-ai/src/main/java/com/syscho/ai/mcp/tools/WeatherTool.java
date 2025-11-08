package com.syscho.ai.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * MCP Tool that provides weather information for a given city.
 * Uses Spring's modern RestClient for making HTTP calls.
 */
@Component
public class WeatherTool {

    private final RestClient restClient;

    public WeatherTool(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }

    @Tool(name = "get_weather_info", description = "Fetches current weather details for a given city")
    public Map<String, Object> getWeather(String city) {

        String endpoint = "/forecast?latitude=28.61&longitude=77.23&current_weather=true";

        return restClient.get()
                .uri(endpoint)
                .retrieve()
                .body(Map.class);
    }
}
