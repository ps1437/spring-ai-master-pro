package com.syscho.ai.tools.fs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WordCounterTool {

    @Tool(name = "countWords",
            description = "Count the number of words in a given text")
    public String countWords(
            @ToolParam(description = "Text content to count words in") String text
    ) {
        log.info("[tool=countWords]");

        if (text == null || text.isBlank()) {
            return "No text provided.";
        }

        int words = text.trim().split("\\s+").length;
        int characters = text.length();
        int lines = text.split("\n").length;

        return "Word count: " + words + " | Characters: " + characters + " | Lines: " + lines;
    }
}