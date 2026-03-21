package com.syscho.ai.tools.tools.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
public class FetchGetTool {

    private final RestClient restClient;

    public FetchGetTool() {
        this.restClient = RestClient.builder()
                .build();
    }

    @Tool(description = "Execute a GET API call. Provide the full URL including query params.")
    public String executeGet(@ToolParam(description = "Full URL with query params") String url) {
        log.info("[tool=get] [status=invoked] [url={}]", url);
        try {
            return restClient.get().uri(url).retrieve().body(String.class);
        } catch (RestClientException e) {
            log.error("[tool=get] [status=failed] [url={}] [error={}]", url, e.getMessage());
            return "GET request failed: " + e.getMessage();
        }
    }
}
