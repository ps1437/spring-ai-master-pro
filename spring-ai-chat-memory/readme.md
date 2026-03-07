# 🤖 Spring AI Customer Support Chat — with Memory

An AI-powered e-commerce customer support chatbot built with **Spring Boot**, **Spring AI**, **Ollama (llama3.2)**, and **JDBC-backed Chat Memory (H2)**.

The bot remembers full conversation context per customer session — no need to repeat order IDs or previous requests.

---

## 📁 Project Structure

```
src/main/
├── java/com/syscho/ai/chatmemory/
│   ├── SpringAiChatMemoryApplication.java         # Entry point
│   ├── config/
│   │   └── ChatConfig.java                        # ChatClient + Memory config
│   ├── controller/
│   │   └── ChatController.java                    # General chat endpoint
│   └── customer_order_inqury/
│       ├── ChatRequest.java                       # Request DTO
│       ├── SupportChatService.java                # Core chat + intent logic
│       ├── SupportController.java                 # Support REST API
│       ├── customer/
│       │   ├── CustomerEntity.java                # Customer JPA entity
│       │   └── CustomerRepository.java            # Customer data access
│       └── order/
│           ├── OrderEntity.java                   # Order JPA entity
│           ├── OrderRepository.java               # Order data access
│           ├── OrderService.java                  # Order business logic
│           └── OrderStatus.java                   # Order status enum
└── resources/
    ├── application.yml
    ├── schema.sql                                 # Table definitions
    ├── data.sql                                   # Sample data
    └── prompts/
        ├── system-prompt.st                       # General assistant prompt
        └── customer-support.st                    # Support agent prompt
```

---

## ⚙️ Prerequisites

| Tool | Version | Download |
|---|---|---|
| Java | 21+ | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org |
| Ollama | Latest | https://ollama.com |

### Pull Required Ollama Models

```bash
ollama pull llama3.2:1b
ollama pull nomic-embed-text
```

### Verify Ollama is Running

```bash
ollama list
# Should show llama3.2:1b
```

---

## 🚀 Running the Application

```bash
# Clone and build
mvn clean install

# Run
mvn spring-boot:run
```

Application starts at → `http://localhost:8089`

---

## 🔗 Key URLs

| URL | Purpose |
|---|---|
| `http://localhost:8089/swagger-ui.html` | Swagger UI — test all APIs |
| `http://localhost:8089/api-docs` | OpenAPI JSON spec |
| `http://localhost:8089/h2-console` | H2 database console |

**H2 Console settings:**
```
JDBC URL  : jdbc:h2:mem:shopdb
Username  : sa
Password  : (leave blank)
```

---

## 📦 Maven Dependencies

```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<!-- Spring AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-jdbc-chat-memory</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Swagger -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.5</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 🗄️ Sample Data

The following customers and orders are auto-loaded on startup via `data.sql`:

| Customer ID | Name | Tier | Order ID | Order Status |
|---|---|---|---|---|
| `cust-001` | John Smith | 🥇 GOLD | `ORD-001` | OUT_FOR_DELIVERY |
| `cust-002` | Sarah Connor | 🥈 SILVER | `ORD-002` | SHIPPED |
| `cust-003` | Raj Patel | 🥉 BRONZE | `ORD-003` | PROCESSING |
| `cust-004` | Emily Watson | 🥇 GOLD | `ORD-004` | DELIVERED |
| `cust-005` | David Lee | 🥈 SILVER | `ORD-005` | PLACED |

---

## 🌐 API Endpoints

### 1. Customer Support Chat — `POST /support/chat`

Send a message to the AI support agent. Memory is maintained per `customerId`.

**Request:**
```json
{
  "customerId": "cust-001",
  "message": "Where is my order?"
}
```

**Response:**
```
Hi John! 👋 Your order ORD-001 for Dell XPS 15 Laptop is currently
OUT FOR DELIVERY and is expected to arrive today!
Is there anything else I can help you with?
```

---

### 2. General Chat — `GET /chat`

General-purpose assistant with memory per `conversionId` header.

**Request:**
```
GET /chat?userMessage=What is Spring AI?
Headers: conversionId: session-abc
```

**Response:**
```
Spring AI is a framework that simplifies building AI-powered applications
using Spring Boot. It provides abstractions over LLM providers like Ollama,
OpenAI, and more...
```

---

## 💬 Full Conversation Flow — Requests & Responses

### Scenario: John tracks and cancels his order

---

#### Step 1 — Track Order

**Request:**
```json
POST /support/chat
{
  "customerId": "cust-001",
  "message": "Hi, where is my order?"
}
```

**Response:**
```
Hi John! 👋 Your order ORD-001 for Dell XPS 15 Laptop is currently
OUT FOR DELIVERY. It's expected to arrive today!
Tracking number: TRK-1001. Is there anything else I can help with?
```

---

#### Step 2 — Ask Delivery Date (bot remembers order from Step 1)

**Request:**
```json
POST /support/chat
{
  "customerId": "cust-001",
  "message": "When exactly will it arrive?"
}
```

**Response:**
```
Your Dell XPS 15 Laptop (ORD-001) is estimated to arrive today.
Since it's already out for delivery, it should reach you very soon!
```
> ✅ Bot remembers order ID — no need to repeat it.

---

#### Step 3 — Cancel Order (bot knows it's out for delivery)

**Request:**
```json
POST /support/chat
{
  "customerId": "cust-001",
  "message": "Actually I want to cancel it"
}
```

**Response:**
```
I understand, John. Since your order ORD-001 is already out for delivery,
I'm unable to cancel it directly. However, I've flagged it for return on
arrival and initiated a refund. Shall I proceed?
```
> ✅ Bot knows status is OUT_FOR_DELIVERY — applies correct flow automatically.

---

#### Step 4 — Confirm (bot remembers cancel intent)

**Request:**
```json
POST /support/chat
{
  "customerId": "cust-001",
  "message": "Yes please"
}
```

**Response:**
```
Done! ✅ Your order ORD-001 has been flagged for return on arrival.
A return label will be sent to john@example.com.
Your refund of ₹85,000.00 will be processed within 3–5 business days.
Have a wonderful day! 😊
```
> ✅ Bot confirms refund — no repeated questions.

---

#### Step 5 — Follow-up Refund Query (full memory intact)

**Request:**
```json
POST /support/chat
{
  "customerId": "cust-001",
  "message": "Which email will the refund confirmation go to?"
}
```

**Response:**
```
The refund confirmation for order ORD-001 will be sent to john@example.com.
You should receive it within 3–5 business days. Is there anything else I
can help you with?
```

---

### Scenario: Sarah asks about her shipped headphones

**Request:**
```json
POST /support/chat
{
  "customerId": "cust-002",
  "message": "Hi, I ordered headphones. What is the status?"
}
```

**Response:**
```
Hi Sarah! 👋 Your order ORD-002 for Sony WH-1000XM5 Headphones
is currently SHIPPED. Estimated delivery in 2 days.
Tracking number: TRK-1002. Let me know if you need anything else!
```

---

### Scenario: Raj cancels a processing order (direct cancel — no return needed)

**Request:**
```json
POST /support/chat
{
  "customerId": "cust-003",
  "message": "I want to cancel my phone order"
}
```

**Response:**
```
Hi Raj! I can see your order ORD-003 for Samsung Galaxy S24
is still being processed. I've gone ahead and cancelled it for you.
Your refund of ₹45,000.00 will be processed within 3–5 business days. 😊
```

---

## 🧠 How Chat Memory Works

```
Customer sends message
        │
        ▼
MessageChatMemoryAdvisor
        │── loads previous messages for conversationId = customerId
        │── prepends history to the prompt
        ▼
Ollama LLM (llama3.2:1b)
        │── receives: system prompt + history + new message
        │── generates context-aware response
        ▼
MessageChatMemoryAdvisor
        │── saves new user message + assistant response to H2
        ▼
Response returned to customer
```

**H2 Memory Table after 3 messages:**
```
CONVERSATION_ID  │ ROLE       │ CONTENT
─────────────────┼────────────┼──────────────────────────────────────
cust-001         │ USER       │ Hi, where is my order?
cust-001         │ ASSISTANT  │ Hi John! ORD-001 is out for delivery!
cust-001         │ USER       │ Actually I want to cancel it
cust-001         │ ASSISTANT  │ Flagged for return. Shall I proceed?
```

---

## 🎯 Intent Detection

`SupportChatService` detects intent **before** calling the LLM and takes real DB actions:

| Customer Says | Intent Detected | DB Action |
|---|---|---|
| "cancel", "return" | CANCEL/RETURN | `OrderStatus → RETURN_REQUESTED` or `CANCELLED` |
| "yes", "proceed", "ok", "sure" | CONFIRM | `OrderStatus → REFUND_INITIATED` |
| anything else | QUERY | No DB action — pure LLM response |

---

## 🔄 Switching to PostgreSQL (Production)

Just update `application.yml` — zero code changes needed:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/chatdb
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: secret
```

And swap the H2 dependency for PostgreSQL in `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 🛠️ Configuration Reference (`application.yml`)

```yaml
server:
  port: 8089

spring:
  application:
    name: spring-ai-chat-memory
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: llama3.2:1b
        options:
          temperature: 0.7
          num-ctx: 4096
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: always
  datasource:
    url: jdbc:h2:mem:shopdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true

logging:
  level:
    org:
      springframework:
        ai:
          chat:
            client:
              advisor: DEBUG
```

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────┐
│                  REST Layer                      │
│   SupportController    ChatController            │
└──────────────┬──────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────┐
│              Service Layer                       │
│   SupportChatService  ──►  OrderService          │
│        │                       │                 │
│        │ intent detection       │ DB updates      │
└────────┼───────────────────────┼─────────────────┘
         │                       │
┌────────▼───────────────────────▼─────────────────┐
│              Spring AI Layer                     │
│   ChatClient (supportChatClient)                 │
│   MessageChatMemoryAdvisor ──► H2 (chat memory)  │
│   SimpleLoggerAdvisor                            │
└──────────────┬───────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────┐
│           Ollama (llama3.2:1b)                   │
│           localhost:11434                        │
└──────────────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────┐
│           Data Layer (H2)                        │
│   customers table   orders table                 │
│   SPRING_AI_CHAT_MEMORY table                    │
└──────────────────────────────────────────────────┘
```

---

## 📝 License

MIT License — free to use and modify.
