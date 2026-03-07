package com.syscho.ai.rag.loader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfDocumentLoaderStrategy implements DocumentLoaderStrategy {

    @Override
    public List<Document> load(String resourcePath) {
        Resource resource = new DefaultResourceLoader().getResource(resourcePath);

        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)
                .build();

        return new PagePdfDocumentReader(resource, config).get();
    }

    @Override
    public String supportedExtension() {
        return "pdf";
    }
}