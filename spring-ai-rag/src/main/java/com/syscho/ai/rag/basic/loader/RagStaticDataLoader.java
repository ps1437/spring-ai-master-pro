package com.syscho.ai.rag.basic.loader;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RagStaticDataLoader {

    private final VectorStore vectorStore;

    public RagStaticDataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void loadData() {

        List<Document> docs = List.of(
                new Document("Java is a popular programming language used to build enterprise applications and backend systems."),
                new Document("Spring Boot simplifies Java application development by providing auto configuration and embedded servers."),
                new Document("Spring Boot allows developers to quickly build REST APIs using the @RestController annotation."),
                new Document("Python is widely used for data science, machine learning, and automation tasks."),
                new Document("Python frameworks like Flask and Django are commonly used for web development."),
                new Document("Spring AI helps integrate large language models into Spring Boot applications."),
                new Document("Vector databases store embeddings and allow semantic similarity search."),
                new Document("RAG stands for Retrieval Augmented Generation and improves LLM answers using external data."),

                new Document("Cooking pasta requires boiling water, adding salt, and cooking the pasta for 8 to 12 minutes."),
                new Document("Indian cooking commonly uses spices like turmeric, cumin, coriander, and garam masala."),
                new Document("A healthy breakfast may include eggs, fruits, whole grains, and yogurt."),
                new Document("Grilling vegetables with olive oil and herbs enhances their natural flavor."),

                new Document("Personal finance involves budgeting, saving, investing, and managing expenses."),
                new Document("Investing in stocks can provide long term financial growth but also involves risk."),
                new Document("A good financial plan includes an emergency fund covering 3 to 6 months of expenses."),
                new Document("Mutual funds allow investors to diversify their investments across multiple assets."),

                new Document("Online shopping allows customers to purchase products through websites and mobile apps."),
                new Document("E-commerce platforms often provide discounts, reviews, and home delivery services."),
                new Document("Comparing prices across different stores helps save money while shopping."),
                new Document("Popular online shopping categories include electronics, fashion, and groceries."),

                new Document("Regular exercise improves cardiovascular health and strengthens muscles."),
                new Document("Drinking enough water daily helps maintain hydration and body functions."),
                new Document("Travel planning should include booking flights, accommodation, and preparing an itinerary."),
                new Document("Popular travel destinations often include beaches, mountains, and cultural cities."),
                new Document("Time management helps improve productivity and reduce stress in daily work.")

        );

        vectorStore.add(docs);

        System.out.println("Loaded dummy RAG documents: " + docs.size());
    }
}