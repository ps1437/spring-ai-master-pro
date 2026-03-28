package com.syscho.ai.tools.encode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class Base64Tool {

    @Tool(name = "encodeBase64",
          description = "Encode a plain text string to Base64 format")
    public String encodeBase64(
            @ToolParam(description = "Plain text to encode") String text
    ) {
        log.info("[tool=encodeBase64] input length={}", text.length());
        String encoded = Base64.getEncoder()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
        return "Encoded: " + encoded;
    }

    @Tool(name = "decodeBase64",
          description = "Decode a Base64 encoded string back to plain text")
    public String decodeBase64(
            @ToolParam(description = "Base64 encoded string to decode") String encoded
    ) {
        log.info("[tool=decodeBase64] input={}", encoded);
        try {
            byte[] decoded = Base64.getDecoder().decode(encoded.trim());
            return "Decoded: " + new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            log.error("[tool=decodeBase64] invalid base64 input={}", encoded);
            return "Invalid Base64 string. Please check the input and try again.";
        }
    }

    @Tool(name = "encodeUrlBase64",
          description = "Encode a string to URL-safe Base64 format (safe for use in URLs and HTTP headers)")
    public String encodeUrlBase64(
            @ToolParam(description = "Plain text to URL-safe encode") String text
    ) {
        log.info("[tool=encodeUrlBase64] input length={}", text.length());
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
        return "URL-safe Base64: " + encoded;
    }

    @Tool(name = "isValidBase64",
          description = "Check whether a given string is a valid Base64 encoded value")
    public String isValidBase64(
            @ToolParam(description = "String to validate") String input
    ) {
        log.info("[tool=isValidBase64] input={}", input);
        try {
            Base64.getDecoder().decode(input.trim());
            return "'" + input + "' is a valid Base64 string.";
        } catch (IllegalArgumentException e) {
            return "'" + input + "' is NOT a valid Base64 string.";
        }
    }
}