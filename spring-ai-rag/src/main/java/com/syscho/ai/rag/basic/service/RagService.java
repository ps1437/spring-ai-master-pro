package com.syscho.ai.rag.basic.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.DEFAULT_CONVERSATION_ID;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String ask(String query, String documentName, String conversionId) {
        String systemPrompt = getSystemPromptRag(query, documentName);

        return chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(systemPrompt))
                .advisors(advisorSpec -> advisorSpec.param(DEFAULT_CONVERSATION_ID, conversionId))
                .user(query).call().content();
    }

    private String getSystemPromptRag(String query, String documentName) {
        SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
                .query(query)
                .topK(5);

        if (StringUtils.hasText(documentName)) {
            searchRequestBuilder.filterExpression("documentName == '" + documentName + "'");
        }

        List<Document> documents = vectorStore.similaritySearch(searchRequestBuilder.build());

        String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n"));

        return """
                Use the context below to answer the question.

                Context:
                %s
                """.formatted(context);
    }
}