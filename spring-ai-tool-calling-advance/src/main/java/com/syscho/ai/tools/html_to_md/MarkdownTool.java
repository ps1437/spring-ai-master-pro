package com.syscho.ai.tools.html_to_md;

import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MarkdownTool {

    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    @Tool(name = "convertMarkdownToHtml",
            description = "Convert markdown text to HTML. Use when user provides markdown content and wants it as HTML.")
    public String convertMarkdownToHtml(
            @ToolParam(description = "Raw markdown content to convert") String markdown
    ) {
        log.info("[tool=convertMarkdownToHtml] length={}", markdown.length());

        Node document = PARSER.parse(markdown);
        String html = RENDERER.render(document);

        log.debug("[tool=convertMarkdownToHtml] result={}", html);
        return html;
    }

    @Tool(name = "convertMarkdownFileToHtml",
            description = "Read a markdown file from disk and convert its content to HTML")
    public String convertMarkdownFileToHtml(
            @ToolParam(description = "Absolute or relative file path to the .md file") String filePath
    ) {
        log.info("[tool=convertMarkdownFileToHtml] path={}", filePath);

        try {
            String markdown = new String(java.nio.file.Files.readAllBytes(java.nio.file.Path.of(filePath)));
            Node document = PARSER.parse(markdown);
            String html = RENDERER.render(document);
            log.debug("[tool=convertMarkdownFileToHtml] converted successfully");
            return html;
        } catch (java.io.IOException e) {
            log.error("[tool=convertMarkdownFileToHtml] failed path={} error={}", filePath, e.getMessage());
            return "Failed to read file: " + filePath + ". Error: " + e.getMessage();
        }
    }


}