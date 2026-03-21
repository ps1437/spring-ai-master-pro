package com.syscho.ai.tools.systeminfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.*;

@Tag(name = "System Info", description = "AI-powered system health and resource monitoring")
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemInfoController {

    private final ChatClient chatClient;
    private final SystemInfoTool systemInfoTool;

    @Operation(summary = "Ask about system health",
               description = "Natural language queries about CPU, memory, disk and OS")
    @GetMapping("/health")
    public String ask(
            @Parameter(
                    description = "Natural language system query",
                    examples = {
                            @ExampleObject(name = "Full summary",  value = "Give me a full system health summary"),
                            @ExampleObject(name = "CPU",           value = "How is the CPU doing?"),
                            @ExampleObject(name = "Memory",        value = "How much memory is available?"),
                            @ExampleObject(name = "Disk",          value = "How much disk space is left?"),
                            @ExampleObject(name = "OS",            value = "What OS is this server running?"),
                            @ExampleObject(name = "All at once",   value = "Show me CPU, memory and disk usage all at once")
                    }
            )
            @RequestParam(defaultValue = "Give me a full system health summary") String userMessage,

            @Parameter(description = "Conversation ID", example = "session-001")
            @RequestHeader(defaultValue = "session-001") String conversationId
    ) {
        return chatClient
                .prompt()
                .toolCallbacks(ToolCallbacks.from(systemInfoTool))
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}