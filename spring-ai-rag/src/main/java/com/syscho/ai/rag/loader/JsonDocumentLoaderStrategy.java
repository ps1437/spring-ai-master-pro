package com.syscho.ai.rag.loader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonDocumentLoaderStrategy implements DocumentLoaderStrategy {

    // Default JSON fields to extract as document content
    private static final String[] DEFAULT_KEYS = {"content", "text", "description", "body", "title"};

    @Override
    public List<Document> load(String resourcePath) {
        Resource resource = new DefaultResourceLoader().getResource(resourcePath);
        return new JsonReader(resource, DEFAULT_KEYS).get();
    }

    @Override
    public String supportedExtension() {
        return "json";
    }
}