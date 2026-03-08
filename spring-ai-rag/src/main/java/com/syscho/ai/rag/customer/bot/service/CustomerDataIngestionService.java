package com.syscho.ai.rag.customer.bot.service;

import com.syscho.ai.rag.basic.dto.IngestionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CustomerDataIngestionService {

    private static final Logger log = LoggerFactory.getLogger(CustomerDataIngestionService.class);
    private static final String METADATA_KEY = "customerId";

    private final JdbcTemplate jdbc;
    private final VectorStore vectorStore;

    public CustomerDataIngestionService(JdbcTemplate jdbc, VectorStore vectorStore) {
        this.jdbc = jdbc;
        this.vectorStore = vectorStore;
    }

    /**
     * Ingest all customers from the database.
     */
    public List<IngestionResult> ingestAll() {
        List<String> customerIds = jdbc.queryForList(
                "SELECT id FROM customers", String.class);

        log.info("Starting ingestion for {} customers", customerIds.size());

        return customerIds.stream()
                .map(this::ingest)
                .toList();
    }

    /**
     * Ingest (or re-ingest) a single customer's data.
     * Replaces any existing vectors for this customer.
     */
    public IngestionResult ingest(String customerId) {
        log.info("Ingesting data for customer: {}", customerId);

        deleteChunks(customerId);

        List<Document> documents = buildDocuments(customerId);
        if (documents.isEmpty()) {
            log.warn("No data found for customer: {}", customerId);
            return IngestionResult.failed(customerId, "No data found");
        }

        vectorStore.add(documents);
        log.info("Ingested {} document(s) for customer: {}", documents.size(), customerId);
        return new IngestionResult(customerId, documents.size(), documents.size());
    }

    // -------------------------------------------------------------------------
    // Document builders
    // -------------------------------------------------------------------------

    private List<Document> buildDocuments(String customerId) {
        return List.of(
                buildProfileDocument(customerId),
                buildOrdersDocument(customerId),
                buildCartDocument(customerId)
        );
    }

    /**
     * Customer profile: name, email, city etc.
     */
    private Document buildProfileDocument(String customerId) {
        Map<String, Object> row = jdbc.queryForMap(
                "SELECT id, name, email, phone, city FROM customers WHERE id = ?", customerId);

        String text = """
                Customer Profile:
                ID: %s
                Name: %s
                Email: %s
                Phone: %s
                City: %s
                """.formatted(
                row.get("id"),
                row.get("name"),
                row.get("email"),
                row.get("phone"),
                row.get("city")
        );

        return new Document(text, metadata(customerId, "profile"));
    }

    /**
     * All orders with line items for this customer.
     */
    private Document buildOrdersDocument(String customerId) {
        List<Map<String, Object>> orders = jdbc.queryForList("""
                SELECT o.id         AS order_id,
                       o.order_date,
                       o.status,
                       o.total_amount,
                       p.name       AS product_name,
                       oi.quantity,
                       oi.unit_price
                FROM orders o
                JOIN order_items oi ON oi.order_id  = o.id
                JOIN products    p  ON p.id         = oi.product_id
                WHERE o.customer_id = ?
                ORDER BY o.order_date DESC, o.id
                """, customerId);

        if (orders.isEmpty()) {
            return new Document("Customer " + customerId + " has no orders.", metadata(customerId, "orders"));
        }

        StringBuilder sb = new StringBuilder("Order History for Customer ").append(customerId).append(":\n\n");

        String currentOrderId = null;
        for (Map<String, Object> row : orders) {
            String orderId = (String) row.get("order_id");

            if (!orderId.equals(currentOrderId)) {
                currentOrderId = orderId;
                sb.append("Order ID: ").append(orderId).append("\n")
                  .append("  Date:   ").append(row.get("order_date")).append("\n")
                  .append("  Status: ").append(row.get("status")).append("\n")
                  .append("  Total:  $").append(row.get("total_amount")).append("\n")
                  .append("  Items:\n");
            }

            sb.append("    - ").append(row.get("product_name"))
              .append(" x").append(row.get("quantity"))
              .append(" @ $").append(row.get("unit_price")).append(" each\n");
        }

        return new Document(sb.toString(), metadata(customerId, "orders"));
    }

    /**
     * Current cart contents for this customer.
     */
    private Document buildCartDocument(String customerId) {
        List<Map<String, Object>> cartItems = jdbc.queryForList("""
                SELECT p.name     AS product_name,
                       c.quantity,
                       p.price    AS unit_price,
                       (c.quantity * p.price) AS subtotal,
                       c.added_at
                FROM cart c
                JOIN products p ON p.id = c.product_id
                WHERE c.customer_id = ?
                ORDER BY c.added_at DESC
                """, customerId);

        if (cartItems.isEmpty()) {
            return new Document("Customer " + customerId + " has an empty cart.", metadata(customerId, "cart"));
        }

        StringBuilder sb = new StringBuilder("Current Cart for Customer ").append(customerId).append(":\n\n");

        double cartTotal = 0;
        for (Map<String, Object> row : cartItems) {
            double subtotal = ((Number) row.get("subtotal")).doubleValue();
            cartTotal += subtotal;

            sb.append("- ").append(row.get("product_name"))
              .append(" x").append(row.get("quantity"))
              .append(" @ $").append(row.get("unit_price"))
              .append(" = $").append(String.format("%.2f", subtotal))
              .append(" (added: ").append(row.get("added_at")).append(")\n");
        }

        sb.append("\nCart Total: $").append(String.format("%.2f", cartTotal));

        return new Document(sb.toString(), metadata(customerId, "cart"));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void deleteChunks(String customerId) {
        Filter.Expression filter = new FilterExpressionBuilder()
                .eq(METADATA_KEY, customerId)
                .build();
        vectorStore.delete(filter);
        log.info("Deleted existing vectors for customer: {}", customerId);
    }

    private Map<String, Object> metadata(String customerId, String section) {
        Map<String, Object> meta = new HashMap<>();
        meta.put(METADATA_KEY, customerId);
        meta.put("section", section);   // profile | orders | cart
        return meta;
    }
}