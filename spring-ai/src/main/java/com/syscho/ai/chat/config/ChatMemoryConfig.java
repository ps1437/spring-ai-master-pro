package com.syscho.ai.chat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

/**
 * ChatConfig class configures the ChatClient bean for the application.
 * It connects the chat model with memory and logging advisors.
 * <p>
 * Simple explanation:
 * - ChatClient: The main object you use to talk to the AI.
 * - Advisors: Extra helpers that can log messages or store conversation history.
 * - ChatMemory: Keeps track of previous messages so the AI "remembers" the conversation.
 */
@Configuration
public class ChatMemoryConfig {

    @Bean("chatMemoryClient")
    public ChatClient chatMemoryClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor(), messageChatMemoryAdvisor(chatMemory))
                .build();
    }

    private static MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory)
                .scheduler(Schedulers.boundedElastic()) // better to use a diff thread
                .build();
    }

}
