package com.syscho.ai.skills.web;

public record ChatMessage(String role, String content, long timestamp) {
}
