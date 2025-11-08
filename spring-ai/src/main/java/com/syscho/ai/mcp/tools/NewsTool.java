package com.syscho.ai.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class NewsTool {
    private final RestClient restClient;

    public NewsTool(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://newsapi.org/v2")
                .build();
    }

    @Tool(name = "get_latest_news", description = "Fetches top news headlines for a given country")
    public Map<String, Object> getNews(String country) {
        String endpoint = "/top-headlines?country=" + country + "&apiKey=demo";
        return restClient.get()
                .uri(endpoint)
                .retrieve()
                .body(Map.class);
    }
}
