package com.syscho.ai;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Tag(name = "Streaming Tools", description = "Tool calling with real-time SSE streaming response")
@RestController
@RequestMapping("/stream")
@RequiredArgsConstructor
public class StreamingToolController {

    private final ChatClient chatClientWithTools;

    @Operation(
            summary = "Stream tool response",
            description = """
                    Same as /chat/tools but streams the final response token by token via SSE.
                    Tool execution still happens internally — only the final LLM response streams.
                    
                    Test in browser or curl:
                    curl -N http://localhost:8089/stream/ask?userMessage=What+is+the+weather+in+Hyderabad
                    """
    )
    @GetMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> ask(
            @Parameter(
                    description = "Natural language query",
                    examples = {
                            @ExampleObject(name = "Booking",       value = "Book flight AI-202 from BOM to DEL for Rahul"),
                            @ExampleObject(name = "System info",   value = "Give me a full system health summary"),
                            @ExampleObject(name = "Joke",          value = "Tell me a programming joke"),
                    }
            )
            @RequestParam(defaultValue = "Tell me a programming joke") String userMessage,

            @Parameter(description = "Conversation ID", example = "session-001")
            @RequestHeader(defaultValue = "session-001") String conversationId
    ) {
        return chatClientWithTools.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }



}