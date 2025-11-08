package com.syscho.ai.chat.simple_chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * Simple Chat Controller that demonstrates how to use Spring AI's ChatClient
 * with memory-enabled conversation support.
 *
 * <p>This controller allows you to chat with an AI model (e.g., OpenAI GPT)
 * and automatically remembers previous messages for each user conversation.</p>
 *
 * <p>Each user is identified by a unique {@code username} header, which acts as
 * the memory ID for that user's conversation context.</p>
 *
 * <p>Example:
 * <pre>
 * GET /chat/simple/memory?q=Hello!&username=alice
 * </pre>
 * The model will remember Alice's previous messages and respond accordingly.
 * </p>
 */
@Tag(
        name = "AI Chat & Response API",
        description = "Unified endpoints for simple chat, memory-based chat, prompt-based interactions, and structured AI responses"
)
@RestController
@RequestMapping("/chat")
public class SimpleMemoryChatController {

    private final ChatClient chatClient;

    /**
     * Injects the ChatClient configured with memory support.
     * The qualifier "chatMemoryClient" must be defined in your configuration
     * (e.g., a bean using a memory-based chat template).
     */
    public SimpleMemoryChatController(@Qualifier("chatMemoryClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Send a message to the AI model and get a response.
     * The conversation memory is maintained based on the username.
     *
     * <p>For example:
     * <ul>
     *   <li><b>Request 1:</b> q=Hello! username=John</li>
     *   <li><b>Response:</b> "Hi John! How can I help you today?"</li>
     *   <li><b>Request 2:</b> q=Remind me what I said before username=John</li>
     *   <li><b>Response:</b> "You said 'Hello!' earlier."</li>
     * </ul>
     * </p>
     */
    @Operation(
            summary = "Ask a question to the AI model (with memory)",
            description = """
                    This endpoint sends a message to the AI model (like GPT-4 or Gemini) 
                    and remembers the past conversation for each unique username.

                    Use the 'username' header to identify your chat session.
                    Each unique username gets its own conversation memory.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful AI response",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(example = "Hi there! How can I help you today?"))),
                    @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content)
            }
    )
    @GetMapping("/simple/memory")
    public ResponseEntity<String> ask(
            @Parameter(
                    description = "Unique username for this conversation (used as memory ID)",
                    example = "alice",
                    required = true
            )
            @RequestHeader(name = "username", defaultValue = "user") String username,

            @Parameter(
                    description = "The question or message to send to the AI model",
                    example = "What's the weather like today?"
            )
            @RequestParam(value = "q", defaultValue = "Hello, Memory AI!") String message
    ) {

        String response = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .call()
                .content();

        return ResponseEntity.ok(response);
    }

}
