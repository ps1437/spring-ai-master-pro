package com.syscho.ai.rag.customer.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagDatabaseService {

    private static final Logger log = LoggerFactory.getLogger(RagDatabaseService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagDatabaseService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    /**
     * Answer a question scoped to a specific customer's data.
     *
     * @param question    Natural language question
     * @param customerId  Customer to scope the vector search to
     * @param conversionId Chat session ID for memory
     */
    public String ask(String question, String customerId, String conversionId) {
        log.info("RAG query for customer='{}' session='{}' question='{}'",
                customerId, conversionId, question);

        String context = retrieveContext(question, customerId);

        return chatClient.prompt()
                .system("""
                        You are a helpful customer support assistant.
                        Answer questions based ONLY on the provided customer data context.
                        If the answer is not in the context, say you don't have that information.
                        Be concise and friendly.
                        """)
                .user(u -> u.text("""
                        Customer Context:
                        {context}
                        
                        Question: {question}
                        """)
                        .param("context", context)
                        .param("question", question))
                .call()
                .content();
    }

    private String retrieveContext(String question, String customerId) {
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5)
                .filterExpression("customerId == '" + customerId + "'")
                .build();

        List<Document> docs = vectorStore.similaritySearch(request);

        if (docs.isEmpty()) {
            log.warn("No context found for customerId={}", customerId);
            return "No data available for this customer.";
        }

        return docs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}