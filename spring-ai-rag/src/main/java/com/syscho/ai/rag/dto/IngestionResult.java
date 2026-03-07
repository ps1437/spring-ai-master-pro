package com.syscho.ai.rag.dto;

/**
 * Result record for each ingestion operation.
 */
public record IngestionResult(
        String source,
        int rawDocuments,
        int chunks,
        boolean success,
        String errorMessage
) {
    public IngestionResult(String source, int rawDocuments, int chunks) {
        this(source, rawDocuments, chunks, true, null);
    }

    public static IngestionResult failed(String source, String errorMessage) {
        return new IngestionResult(source, 0, 0, false, errorMessage);
    }
}