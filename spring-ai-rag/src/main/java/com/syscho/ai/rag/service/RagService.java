package com.syscho.ai.rag.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

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

    public String ask(String query, String conversionId) {

        String systemPrompt = getSystemPromptRag(query);

        return chatClient.prompt().system(promptSystemSpec -> promptSystemSpec.text(systemPrompt)).advisors(advisorSpec -> advisorSpec.param(DEFAULT_CONVERSATION_ID, conversionId)).user(query).call().content();
    }

    private String getSystemPromptRag(String query) {
        List<Document> documents = vectorStore.similaritySearch(query);


        String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n"));

        return """
                Answer the question using the context below.

                Context:
                %s
                """.formatted(context);
    }
}