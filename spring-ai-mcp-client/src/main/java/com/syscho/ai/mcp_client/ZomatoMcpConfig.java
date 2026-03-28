//package com.syscho.ai.tools;
//
//import io.modelcontextprotocol.client.McpClient;
//import io.modelcontextprotocol.client.McpSyncClient;
//import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
//import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.time.Duration;
//
//@Configuration
//public class ZomatoMcpConfig {
//
//    @Bean
//    public McpSyncClient zomatoMcpClient() {
//        try {
//            var transport = HttpClientStreamableHttpTransport.builder("https://mcp-server.zomato.com")
//                    .endpoint("/mcp")
//                    .build();
//
//            McpSyncClient client = McpClient.sync(transport)
//                    .requestTimeout(Duration.ofSeconds(30))
//                    .build();
//
//            client.initialize();
//            System.out.println("✅ Zomato MCP connected!");
//            client.listTools().tools()
//                    .forEach(t -> System.out.println("  🍔 " + t.name()));
//            return client;
//
//        } catch (Exception e) {
//            System.out.println("❌ Zomato MCP failed: " + e.getMessage());
//            return null;
//        }
//    }
//}