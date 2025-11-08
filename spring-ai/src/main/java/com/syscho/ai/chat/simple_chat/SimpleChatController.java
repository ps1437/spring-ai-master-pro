package com.syscho.ai.chat.simple_chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@Tag(
        name = "AI Chat & Response API",
        description = "Unified endpoints for simple chat, memory-based chat, prompt-based interactions, and structured AI responses"
)
public class SimpleChatController {

    private final ChatClient chatClient;

    public SimpleChatController(@Qualifier("chatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Operation(summary = "Ask a question to the AI model",
            description = "Send a question via query parameter 'q' and get a text response")
    @GetMapping("/simple")
    public ResponseEntity<String> ask(
            @RequestParam(value = "question", defaultValue = "Hello, Ai!") String message
    ) {

        return ResponseEntity.ok(chatClient.prompt().user(message)
                .call().content());
    }


}
