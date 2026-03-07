package com.syscho.ai.rag.service;

import com.syscho.ai.rag.dto.IngestionResult;
import com.syscho.ai.rag.loader.DocumentLoaderFactory;
import com.syscho.ai.rag.loader.DocumentLoaderStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final DocumentLoaderFactory loaderFactory;
    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public DocumentIngestionService(DocumentLoaderFactory loaderFactory, VectorStore vectorStore) {
        this.loaderFactory = loaderFactory;
        this.vectorStore = vectorStore;
        this.splitter = new TokenTextSplitter();
    }


    public IngestionResult ingest(String resourcePath) {
        log.info("Ingesting document from path: {}", resourcePath);

        DocumentLoaderStrategy strategy = loaderFactory.getStrategy(resourcePath);
        List<Document> rawDocs = strategy.load(resourcePath);
        List<Document> chunks = splitter.apply(rawDocs);

        vectorStore.add(chunks);

        log.info("Ingested {} chunks from {}", chunks.size(), resourcePath);
        return new IngestionResult(resourcePath, rawDocs.size(), chunks.size());
    }


    public IngestionResult ingest(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        if (originalName.isBlank()) {
            throw new IllegalArgumentException("Uploaded file has no name");
        }

        // Save to temp location so ResourceLoader can access it
        Path tempFile = Files.createTempFile("rag-upload-", "-" + originalName);
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        try {
            return ingest("file:" + tempFile.toAbsolutePath());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Ingest multiple files in batch.
     */
    public List<IngestionResult> ingestAll(List<MultipartFile> files) {
        return files.stream()
                .map(file -> {
                    try {
                        return ingest(file);
                    } catch (IOException e) {
                        return IngestionResult.failed(file.getOriginalFilename(), e.getMessage());
                    }
                })
                .toList();
    }


}