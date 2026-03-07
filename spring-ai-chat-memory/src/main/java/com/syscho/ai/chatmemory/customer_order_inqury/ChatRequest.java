package com.syscho.ai.chatmemory.customer_order_inqury;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@Schema(description = "Chat request payload")
public class ChatRequest {

    @NotBlank(message = "customerId must not be blank")
    @Schema(description = "Unique customer ID", example = "cust-001", requiredMode = REQUIRED)
    private String customerId;

    @NotBlank(message = "message must not be blank")
    @Size(max = 1000, message = "message must not exceed 1000 characters")
    @Schema(description = "Customer message", example = "Where is my order?", requiredMode = REQUIRED)
    private String message;
}