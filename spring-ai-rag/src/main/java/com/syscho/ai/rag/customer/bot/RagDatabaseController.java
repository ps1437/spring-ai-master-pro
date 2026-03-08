package com.syscho.ai.rag.customer.bot;

import com.syscho.ai.rag.basic.dto.IngestionResult;
import com.syscho.ai.rag.customer.bot.service.CustomerDataIngestionService;
import com.syscho.ai.rag.customer.bot.service.RagDatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag/database")
public class RagDatabaseController {

    private final RagDatabaseService ragDatabaseService;
    private final CustomerDataIngestionService ingestionService;

    public RagDatabaseController(RagDatabaseService ragDatabaseService,
                                 CustomerDataIngestionService ingestionService) {
        this.ragDatabaseService = ragDatabaseService;
        this.ingestionService = ingestionService;
    }

    /**
     * Ask a question about a specific customer.
     * GET /rag/database?question=What is in my cart?&customerId=CUST001
     */
    @GetMapping
    public String ask(@RequestParam String question,
                      @RequestParam String customerId,
                      @RequestHeader String conversionId) {
        return ragDatabaseService.ask(question, customerId, conversionId);
    }

    /**
     * Ingest all customers from DB into vector store.
     * POST /rag/database/ingest
     */
    @PostMapping("/ingest")
    public ResponseEntity<List<IngestionResult>> ingestAll() {
        List<IngestionResult> results = ingestionService.ingestAll();
        return ResponseEntity.ok(results);
    }
}

