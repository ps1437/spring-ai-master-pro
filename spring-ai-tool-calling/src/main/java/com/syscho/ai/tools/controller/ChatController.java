package com.syscho.ai.tools.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chat/tools")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    @GetMapping
    public String ask(@RequestParam String userMessage, @RequestHeader String conversionId) {
        return chatClient.prompt()
                .user(userMessage)
                .advisors(advisor -> advisor
                        .param(ChatMemory.CONVERSATION_ID, conversionId)
                )
                .call()
                .content();
    }
}