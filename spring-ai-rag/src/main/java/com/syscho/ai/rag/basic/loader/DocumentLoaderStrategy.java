package com.syscho.ai.rag.basic.loader;

import org.springframework.ai.document.Document;
import java.util.List;

/**
 * Strategy interface for all document loaders.
 * Each implementation handles a specific file type.
 */
public interface DocumentLoaderStrategy {

    /**
     * Load and return documents from the given resource path.
     */
    List<Document> load(String resourcePath);

    /**
     * Returns the file extension this strategy supports (e.g. "pdf", "txt").
     */
    String supportedExtension();
}