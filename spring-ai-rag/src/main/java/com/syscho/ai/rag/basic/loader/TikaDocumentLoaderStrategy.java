package com.syscho.ai.rag.basic.loader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Tika-based loader handles Word, PowerPoint, Excel, HTML, and other rich formats.
 */
@Component
public class TikaDocumentLoaderStrategy implements DocumentLoaderStrategy {

    private static final Set<String> SUPPORTED = Set.of("docx", "doc", "pptx", "ppt", "xlsx", "xls", "html", "htm", "odt");

    @Override
    public List<Document> load(String resourcePath) {
        Resource resource = new DefaultResourceLoader().getResource(resourcePath);
        return new TikaDocumentReader(resource).get();
    }

    @Override
    public String supportedExtension() {
        // Primary extension — factory resolves others via supportsExtension()
        return "docx";
    }

    /**
     * Tika handles multiple formats; override to check all supported types.
     */
    public boolean supportsExtension(String extension) {
        return SUPPORTED.contains(extension.toLowerCase());
    }
}