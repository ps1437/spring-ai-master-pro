package com.syscho.ai.rag.loader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextDocumentLoaderStrategy implements DocumentLoaderStrategy {

    @Override
    public List<Document> load(String resourcePath) {
        Resource resource = new DefaultResourceLoader().getResource(resourcePath);
        return new TextReader(resource).get();
    }

    @Override
    public String supportedExtension() {
        return "txt";
    }
}