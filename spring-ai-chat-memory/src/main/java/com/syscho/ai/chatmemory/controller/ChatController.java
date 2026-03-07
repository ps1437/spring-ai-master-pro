package com.syscho.ai.chatmemory.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public String ask(@RequestParam String userMessage, @RequestHeader String conversionId) {
        return chatClient.prompt()
                .system(spec -> spec
                        .param("current_date", LocalDate.now().toString())
                )
                .user(userMessage)
                .advisors(advisor -> advisor
                        .param(ChatMemory.CONVERSATION_ID, conversionId)
                )
                .call()
                .content();
    }
}