package com.syscho.ai.chatmemory.customer_order_inqury;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/support")
@Validated
@Tag(name = "Customer Support", description = "AI-powered customer support chat API")
public class SupportController {

    private final SupportChatService chatService;

    public SupportController(SupportChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Send a support message",
            description = "Send a message to the AI support agent. " +
                    "Chat memory is maintained per customer session."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "AI response from support agent",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "Hi John! Your order ORD-123 is out for delivery today!"
                            )
                    )
            ),
    })
    @PostMapping("/chat")
    public ResponseEntity<String> chat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Customer ID and message",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Track Order",
                                            summary = "Ask about order status",
                                            value = """
                                                    {
                                                      "customerId": "cust-001",
                                                      "message": "Where is my order?"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Cancel Order",
                                            summary = "Request cancellation",
                                            value = """
                                                    {
                                                      "customerId": "cust-001",
                                                      "message": "I want to cancel my order"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Confirm Action",
                                            summary = "Confirm a previous action",
                                            value = """
                                                    {
                                                      "customerId": "cust-001",
                                                      "message": "Yes please"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody @Valid ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(request));
    }


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