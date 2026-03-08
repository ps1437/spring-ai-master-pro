package com.syscho.ai.rag.basic.service;

import com.syscho.ai.rag.basic.loader.DocumentLoaderStrategy;
import com.syscho.ai.rag.basic.dto.IngestionResult;
import com.syscho.ai.rag.basic.loader.DocumentLoaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);
    private static final int BATCH_SIZE = 20;

    private final DocumentLoaderFactory loaderFactory;
    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    /*
     * Ollama Embedding Model Token Limits:
     * nomic-embed-text   → max 8192  → recommended 512–1024
     * mxbai-embed-large  → max 512   → recommended 400–450
     * all-minilm         → max 256   → recommended 200–230
     */
    public DocumentIngestionService(DocumentLoaderFactory loaderFactory, VectorStore vectorStore) {
        this.loaderFactory = loaderFactory;
        this.vectorStore = vectorStore;
        this.splitter = new TokenTextSplitter(
                450,   // chunkSize
                100,   // chunkOverlap
                5,     // minChunkLengthToEmbed — skip tiny chunks
                10000, // maxNumChunks — safety cap
                true   // keepSeparator
        );
    }


    /**
     * Ingest an uploaded file. Uses original filename as document name
     * so re-uploading the same file replaces existing chunks.
     */
    public IngestionResult ingest(MultipartFile file) throws IOException {
        String documentName = resolveDocumentName(file);

        Path tempFile = Files.createTempFile("rag-upload-", "-" + documentName);
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        try {
            return ingest(tempFile.toUri().toString(), documentName);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Ingest multiple files in parallel.
     */
    public List<IngestionResult> ingestAll(List<MultipartFile> files) {
        return files.parallelStream()
                .map(file -> {
                    try {
                        return ingest(file);
                    } catch (IOException e) {
                        log.error("Failed to ingest file: {}", file.getOriginalFilename(), e);
                        return IngestionResult.failed(file.getOriginalFilename(), e.getMessage());
                    }
                })
                .toList();
    }


    private IngestionResult ingest(String resourcePath, String documentName) {
        log.info("Ingesting document '{}' from path: {}", documentName, resourcePath);

        deleteChunks(documentName);

        DocumentLoaderStrategy strategy = loaderFactory.getStrategy(resourcePath);
        List<Document> rawDocs = strategy.load(resourcePath);
        List<Document> chunks = splitter.apply(rawDocs);

        for (int i = 0; i < chunks.size(); i += BATCH_SIZE) {
            List<Document> batch = chunks
                    .subList(i, Math.min(i + BATCH_SIZE, chunks.size()))
                    .stream()
                    .map(doc -> tagWithMetadata(doc, documentName))
                    .toList();
            vectorStore.add(batch);
        }

        log.info("Ingested {} chunks for '{}'", chunks.size(), documentName);
        return new IngestionResult(documentName, rawDocs.size(), chunks.size());
    }

    /**
     * Tag a document chunk with documentName metadata for filtered search.
     */
    private Document tagWithMetadata(Document doc, String documentName) {
        Map<String, Object> metadata = new HashMap<>(doc.getMetadata());
        metadata.put("documentName", documentName);
        return new Document(doc.getText(), metadata);
    }

    /**
     * Delete all existing vector chunks for the given document name.
     * Ensures re-upload replaces stale data instead of duplicating it.
     */
    private void deleteChunks(String documentName) {
        Filter.Expression filter = new FilterExpressionBuilder()
                .eq("documentName", documentName)
                .build();
        vectorStore.delete(filter);
        log.info("Deleted existing chunks for '{}'", documentName);
    }


    private String resolveDocumentName(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Uploaded file has no name");
        }
        return name;
    }
}