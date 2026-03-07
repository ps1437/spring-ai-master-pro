package com.syscho.ai.rag.loader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarkdownDocumentLoaderStrategy implements DocumentLoaderStrategy {

    @Override
    public List<Document> load(String resourcePath) {
        Resource resource = new DefaultResourceLoader().getResource(resourcePath);

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();

        return new MarkdownDocumentReader(resource, config).get();
    }

    @Override
    public String supportedExtension() {
        return "md";
    }
}