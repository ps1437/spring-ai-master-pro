package com.syscho.ai.rag.controller;

import com.syscho.ai.rag.service.RagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping
    public String ask(@RequestParam String question , @RequestHeader String conversionId) {
        return ragService.ask(question,conversionId);
    }
}