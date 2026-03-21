package com.syscho.ai.tools.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Fun Tools", description = "Jokes, roasts and fun stuff powered by JokeAPI")
@RestController
@RequestMapping("/fun")
@RequiredArgsConstructor
public class FunController {

    private final ChatClient chatClient;
    private final FunTool funTool;

    @Operation(summary = "Ask for jokes and roasts")
    @GetMapping("/joke")
    public String joke(
            @Parameter(
                    description = "What kind of joke or roast do you want?",
                    examples = {
                            @ExampleObject(name = "Random joke",        value = "Tell me a random joke"),
                            @ExampleObject(name = "Programming joke",   value = "Tell me a programming joke"),
                            @ExampleObject(name = "Pun",                value = "Tell me a pun"),
                            @ExampleObject(name = "Roast Java",         value = "Roast Java developers"),
                            @ExampleObject(name = "Roast Python",       value = "Roast Python developers"),
                            @ExampleObject(name = "Roast JavaScript",   value = "Roast JavaScript developers"),
                            @ExampleObject(name = "Roast a person",     value = "Roast my friend Rahul"),
                            @ExampleObject(name = "Dark joke",          value = "Tell me a dark joke"),
                            @ExampleObject(name = "Christmas joke",     value = "Tell me a Christmas joke")
                    }
            )
            @RequestParam(defaultValue = "Tell me a programming joke") String userMessage,

            @Parameter(description = "Conversation ID", example = "session-001")
            @RequestHeader(defaultValue = "session-001") String conversationId
    ) {
        return chatClient.prompt()
                .user(userMessage)
                .tools(funTool)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}