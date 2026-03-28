package com.syscho.ai.mcp_client;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class McpServerRegistry {

    private final ChatClient chatClient;

    public McpServerRegistry(ChatClient.Builder builder,
                             List<McpSyncClient> mcpClients) {
        List<SyncMcpToolCallbackProvider> tools = mcpClients.stream()
                .map(SyncMcpToolCallbackProvider::new)
                .toList();

        SyncMcpToolCallbackProvider[] array = tools.toArray(new SyncMcpToolCallbackProvider[0]);
        this.chatClient = builder
                .defaultToolCallbacks(array)
                .build();
    }

    public String ask(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }


}