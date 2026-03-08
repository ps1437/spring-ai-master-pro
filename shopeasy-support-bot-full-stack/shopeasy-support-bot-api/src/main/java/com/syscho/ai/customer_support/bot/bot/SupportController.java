package com.syscho.ai.customer_support.bot.bot;

import com.syscho.ai.customer_support.bot.bot.dto.ChatRequest;
import com.syscho.ai.customer_support.bot.bot.service.SupportChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/support")
@Validated
@Tag(name = "Customer Support", description = "AI-powered customer support chat API")
@RequiredArgsConstructor
public class SupportController {

    private final SupportChatService chatService;

    @Operation(
            summary = "Stream a support message (SSE)",
            description = "Returns AI response token-by-token as Server-Sent Events. " +
                    "Use EventSource in browser or curl --no-buffer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Token stream — text/event-stream"),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found")
    })
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(
            @Parameter(description = "Customer ID", example = "cust-001")
            @RequestParam String customerId,
            @Parameter(description = "Customer message", example = "Where is my order?")
            @RequestParam String message) {

        ChatRequest request = new ChatRequest();
        request.setCustomerId(customerId);
        request.setMessage(message);
        return chatService.stream(request);
    }

}