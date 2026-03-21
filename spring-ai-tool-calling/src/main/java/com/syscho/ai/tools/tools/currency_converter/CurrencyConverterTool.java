package com.syscho.ai.tools.tools.currency_converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class CurrencyConverterTool {

    private final RestClient restClient;

    public CurrencyConverterTool(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.exchangerate-api.com/v4/latest")
                .build();
    }

    @Tool(name = "currencyConverter", description = "Convert currency amount using real-time exchange rates")
    public String convertCurrency(String from, String to, double amount) {

        log.info("Currency conversion requested: {} {} -> {}", amount, from, to);

        try {
            Map response = restClient.get()
                    .uri("/{base}", from.toUpperCase())
                    .retrieve()
                    .body(Map.class);

            log.debug("API response received: {}", response);

            if (response == null || !response.containsKey("rates")) {
                log.warn("Invalid API response for base currency: {}", from);
                return "Failed to fetch exchange rates.";
            }

            Map<String, Double> rates = (Map<String, Double>) response.get("rates");

            Double rate = rates.get(to.toUpperCase());

            if (rate == null) {
                log.warn("Unsupported target currency: {}", to);
                return "Currency not supported: " + to;
            }

            log.debug("Exchange rate {} -> {} = {}", from, to, rate);

            double convertedAmount = amount * rate;

            String result = String.format("%.2f %s = %.2f %s",
                    amount, from.toUpperCase(),
                    convertedAmount, to.toUpperCase());

            log.info("Conversion successful: {}", result);

            return result;

        } catch (Exception e) {
            log.error("Error during currency conversion: {} -> {}", from, to, e);
            return "Error while converting currency: " + e.getMessage();
        }
    }
}