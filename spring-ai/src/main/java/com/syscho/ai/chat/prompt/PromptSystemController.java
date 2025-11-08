package com.syscho.ai.chat.prompt;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(
        name = "AI Chat & Response API",
        description = "Unified endpoints for simple chat, memory-based chat, prompt-based interactions, and structured AI responses"
)
public class PromptSystemController {

    private final ChatClient chatClient;

    public PromptSystemController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/prompt-templates/system-hospital-PromptTemplate.st")
    Resource systemPromptTemplate;

    /**
     * Sends a user message to the hospital assistant AI and returns a response.
     * The AI only responds with hospital services information (fees, treatments, room charges, labs, etc.)
     *
     * @param message The user's query related to hospital services
     * @return AI-generated response based on the hospital services system prompt
     */
    @Operation(
            summary = "Ask Hospital Assistant",
            description = "Send a message to the AI hospital assistant. The assistant only provides information on hospital services such as fees, treatments, room charges, lab tests, and visiting hours."
    )
    @GetMapping("/prompt-stuffing")
    public String promptStuffing(@RequestParam("message") String message) {
        return chatClient
                .prompt()
                .system(systemPromptTemplate)
                .user(message)
                .call().content();
    }
}
