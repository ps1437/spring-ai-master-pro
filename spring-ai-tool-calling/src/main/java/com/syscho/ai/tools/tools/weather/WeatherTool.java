package com.syscho.ai.tools.tools.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class WeatherTool {

    private final RestClient restClient;
    private final String apiKey;

    public WeatherTool(WeatherApiProperties props) {
        this.apiKey = props.key();
        this.restClient = RestClient.builder()
                .baseUrl(props.baseUrl())
                .build();
        log.info("[tool=WeatherTool] [status=initialized] [baseUrl={}]", props.baseUrl());
    }

    @Tool(description = "Get current weather for a location. Location can be city name, lat/lon, zip code or IP address.")
    public String getCurrentWeather(
            @ToolParam(description = "City name like Hyderabad") String location) {

        log.info("[tool=getCurrentWeather] [status=invoked] [location={}]", location);

        try {
            String response = restClient.get()
                    .uri("/current.json?key={key}&q={q}&aqi=no", apiKey, location)
                    .retrieve()
                    .body(String.class);

            log.info("[tool=getCurrentWeather] [status=success] [location={}]", location);
            return response;

        } catch (RestClientException e) {
            log.error("[tool=getCurrentWeather] [status=failed] [location={}] [error={}]",
                    location, e.getMessage());
            return "Unable to fetch current weather for: " + location;
        }
    }

    @Tool(description = "Get weather forecast for a location. days = number of forecast days (1-14).")
    public String getWeatherForecast(
            @ToolParam(description = "City name like Hyderabad") String location,
            @ToolParam(description = "Number of forecast days between 1 and 14") int days) {

        log.info("[tool=getWeatherForecast] [status=invoked] [location={}] [days={}]", location, days);

        if (days < 1 || days > 14) {
            log.warn("[tool=getWeatherForecast] [status=invalid-input] [days={}] — must be between 1 and 14", days);
            return "Invalid days value: " + days + ". Must be between 1 and 14.";
        }

        try {
            String response = restClient.get()
                    .uri("/forecast.json?key={key}&q={q}&days={days}&aqi=no&alerts=no",
                            apiKey, location, days)
                    .retrieve()
                    .body(String.class);

            log.info("[tool=getWeatherForecast] [status=success] [location={}] [days={}]", location, days);
            return response;

        } catch (RestClientException e) {
            log.error("[tool=getWeatherForecast] [status=failed] [location={}] [days={}] [error={}]",
                    location, days, e.getMessage());
            return "Unable to fetch forecast for: " + location;
        }
    }
}