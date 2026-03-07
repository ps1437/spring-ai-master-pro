package com.syscho.ai.rag.controller;

import com.syscho.ai.rag.dto.IngestionResult;
import com.syscho.ai.rag.service.DocumentIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Document Ingestion", description = "Upload and ingest documents into the RAG vector store")
@RestController
@RequestMapping("/api/documents")
public class DocumentIngestionController {

    private final DocumentIngestionService ingestionService;

    public DocumentIngestionController(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Operation(
            summary = "Upload a document",
            description = "Upload any supported file for RAG ingestion. Supported: pdf, txt, md, docx, pptx, xlsx, json, html"
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngestionResult> upload(
            @Parameter(description = "File to upload and ingest", required = true)
            @RequestPart("file") MultipartFile file) throws IOException {
        IngestionResult result = ingestionService.ingest(file);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/documents/upload-batch
     * Upload and ingest multiple files in a single request
     */
    @Operation(
            summary = "Batch upload multiple documents",
            description = "Upload and ingest multiple files in one request. Supported: pdf, txt, md, docx, pptx, xlsx, json, html"
    )
    @PostMapping(value = "/upload-batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<IngestionResult>> uploadBatch(
            @Parameter(description = "Multiple files to upload and ingest", required = true)
            @RequestPart("files") List<MultipartFile> files) {
        List<IngestionResult> results = ingestionService.ingestAll(files);
        return ResponseEntity.ok(results);
    }


}