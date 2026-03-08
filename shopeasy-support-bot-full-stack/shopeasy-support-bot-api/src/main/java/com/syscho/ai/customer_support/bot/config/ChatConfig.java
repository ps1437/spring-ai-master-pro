package com.syscho.ai.customer_support.bot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


/**
 * Central configuration for Spring AI Chat clients and memory management.
 *
 * <p>This configuration sets up two distinct {@link ChatClient} instances:
 * <ul>
 *   <li><b>chatClient</b>        — General-purpose assistant using {@code system-prompt.st}</li>
 *   <li><b>supportChatClient</b> — Customer support agent using {@code customer-support.st}</li>
 * </ul>
 *
 * <p>Both clients share a single {@link ChatMemory} instance backed by a
 * {@link ChatMemoryRepository} (H2 in dev, PostgreSQL in production).
 * Per-session isolation is achieved via {@code conversationId} at runtime.
 *
 * <pre>
 * Request
 *    │
 *    ▼
 * ChatClient
 *    │── defaultSystem(prompt)         → sets agent persona
 *    │── MessageChatMemoryAdvisor      → injects/saves conversation history
 *    └── SimpleLoggerAdvisor           → logs prompts and responses
 *    │
 *    ▼
 * Ollama LLM
 * </pre>
 *
 * @see ChatClient
 * @see ChatMemory
 * @see MessageChatMemoryAdvisor
 */
@Configuration
public class ChatConfig {

    @Value("classpath:prompts/customer-support.st")
    private Resource systemCustomerPromptResource;

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient supportChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultSystem(systemCustomerPromptResource)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

}