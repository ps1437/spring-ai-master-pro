package com.syscho.ai.rag.basic.loader;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory that resolves the correct DocumentLoaderStrategy based on file extension.
 * <p>
 * Design Patterns used:
 * - Factory Pattern      : resolves strategy by file type
 * - Strategy Pattern     : each loader is an interchangeable strategy
 * - Open/Closed Principle: add new loaders without modifying this class
 */
@Component
public class DocumentLoaderFactory {

    private final Map<String, DocumentLoaderStrategy> strategyMap;
    private final TikaDocumentLoaderStrategy tikaStrategy;

    public DocumentLoaderFactory(List<DocumentLoaderStrategy> strategies,
                                 TikaDocumentLoaderStrategy tikaStrategy) {
        this.tikaStrategy = tikaStrategy;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        DocumentLoaderStrategy::supportedExtension,
                        Function.identity()
                ));
    }

    public DocumentLoaderStrategy getStrategy(String filePath) {
        String extension = extractExtension(filePath);

        // Check Tika multi-extension support first
        if (tikaStrategy.supportsExtension(extension)) {
            return tikaStrategy;
        }

        DocumentLoaderStrategy strategy = strategyMap.get(extension);
        if (strategy == null) {
            throw new UnsupportedOperationException(
                    "No document loader found for file type: ." + extension +
                            ". Supported types: " + getSupportedExtensions()
            );
        }

        return strategy;
    }

    public List<String> getSupportedExtensions() {
        return strategyMap.keySet().stream().sorted().toList();
    }

    private String extractExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filePath.length() - 1) {
            throw new IllegalArgumentException("Cannot determine file extension from: " + filePath);
        }
        return filePath.substring(dotIndex + 1).toLowerCase();
    }
}