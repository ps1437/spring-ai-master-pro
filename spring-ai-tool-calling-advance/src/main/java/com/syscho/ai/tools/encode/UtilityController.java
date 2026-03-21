package com.syscho.ai.tools.encode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Utility Tools", description = "Base64 encoding/decoding and text analysis tools")
@RestController
@RequestMapping("/utils")
@RequiredArgsConstructor
public class UtilityController {

    private final ChatClient chatClient;
    private final Base64Tool base64Tool;
    @Operation(summary = "Base64 and text analysis queries")
    @GetMapping("/ask")
    public String ask(
            @Parameter(
                    description = "Natural language utility query",
                    examples = {
                            @ExampleObject(name = "Encode",        value = "Encode 'Hello Rahul' to base64"),
                            @ExampleObject(name = "Decode",        value = "Decode this base64: SGVsbG8gUmFodWw="),
                            @ExampleObject(name = "URL encode",    value = "Encode 'user@syscho.in' to URL-safe base64"),
                            @ExampleObject(name = "Validate b64",  value = "Is 'SGVsbG8=' a valid base64 string?"),
                            @ExampleObject(name = "Analyse text",  value = "Analyse this text: Spring AI makes tool calling very easy and fun to build"),
                            @ExampleObject(name = "Top words",     value = "What are the top 5 words in: Spring AI is great for building AI tools with Spring Boot"),
                            @ExampleObject(name = "Compare texts", value = "Compare these two texts: 'Spring AI is great' and 'Spring Boot is great'"),
                            @ExampleObject(name = "Multi-tool",    value = "Encode 'hello' to base64 and also analyse the word count of 'Spring AI tool calling is very powerful and easy to use'")
                    }
            )
            @RequestParam(defaultValue = "Encode 'Hello Rahul' to base64") String userMessage,

            @Parameter(description = "Conversation ID", example = "session-001")
            @RequestHeader(defaultValue = "session-001") String conversationId
    ) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(base64Tool)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}