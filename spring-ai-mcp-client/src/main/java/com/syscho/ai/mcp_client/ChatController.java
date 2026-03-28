package com.syscho.ai.mcp_client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private McpServerRegistry mcpServerRegistry;

    @GetMapping("/ask")
    public String ask(@RequestParam String q) {
        return mcpServerRegistry.ask(q);
    }
}