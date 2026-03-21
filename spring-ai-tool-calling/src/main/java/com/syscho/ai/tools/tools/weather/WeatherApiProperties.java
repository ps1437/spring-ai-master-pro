package com.syscho.ai.tools.tools.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(String key, String baseUrl) {}