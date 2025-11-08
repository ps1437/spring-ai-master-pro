package com.syscho.ai.chat.prompt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(
        name = "AI Chat & Response API",
        description = "Unified endpoints for simple chat, memory-based chat, prompt-based interactions, and structured AI responses"
)
public class PromptUserController {

    private final ChatClient chatClient;

    public PromptUserController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/prompt-templates/user-email-Template.st")
    Resource userPromptTemplate;

    @Operation(
            summary = "Generate a professional customer email response",
            description = """
            This endpoint uses an AI model to generate a **customer support email reply** based on the provided customer name and message.
            <br><br>
            - It uses a predefined system prompt that defines the assistant as a professional email responder.  
            - The user template (`user-email-Template.st`) is parameterized with your input.  
            - The AI returns a polished and human-like email response string.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully generated an AI-crafted email response",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Missing or invalid parameters",
                            content = @Content
                    )
            }
    )
    @GetMapping("/email")
    public String emailResponse(
            @Parameter(
                    description = "Name of the customer to address in the generated email",
                    example = "John Doe"
            )
            @RequestParam("customerName") String customerName,

            @Parameter(
                    description = "Customer's message or complaint text to which AI should draft a reply",
                    example = "Hi, I’m facing an issue with my recent order delivery."
            )
            @RequestParam("customerMessage") String customerMessage
    ) {
        return chatClient
                .prompt()
                .system("""
                    You are a professional customer service assistant who helps draft polite and well-structured
                    email responses to customer inquiries and complaints.
                    """)
                .user(promptTemplateSpec ->
                        promptTemplateSpec.text(userPromptTemplate)
                                .param("customerName", customerName)
                                .param("customerMessage", customerMessage))
                .call()
                .content();
    }
}