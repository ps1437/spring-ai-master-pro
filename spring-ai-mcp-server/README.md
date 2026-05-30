# Spring AI Tool Calling 

A hands-on Spring AI project demonstrating tool calling patterns with real integrations.

---

## Package Overview

### `database.booking`
Database-backed tools using JPA + H2.
Covers full CRUD operations — create, fetch, cancel and update flight bookings via natural language.
> Try: *"Book flight AI-202 from BOM to DEL for Rahul"* or *"Cancel booking 3"*

---

### `fs`
File system tools using Java NIO.
Scan directories, search text across files, count lines of code, find TODO comments and analyse directory size.
> Try: *"Find all java files in /tmp/project"* or *"Search for ChatClient in all java files"*

---

### `rest`
REST API integration tools using `RestClient`.
Calls external APIs — weather, flight search via AviationStack, and a generic HTTP GET tool.
> Try: *"What is the weather in Hyderabad?"* or *"Search flights from BOM to DEL"*

---

### `stream`
Streaming response tools using `Flux<String>` and SSE.
Same tool calling flow as REST but streams the final LLM response token by token to the client.
> Try via curl: `curl -N "http://localhost:8089/stream/ask?userMessage=Tell+me+a+joke"`

---

### `html_to_md`
Markdown to HTML conversion tools using Commonmark.
Convert inline markdown, read `.md` files from disk, and save fully styled HTML pages to disk.
> Try: *"Convert this markdown to HTML"* or *"Convert file at /tmp/report.md to HTML and save it"*

---

### `encode`
Encoding and decoding tools using pure JDK.
Base64 encode/decode, URL-safe Base64, and Base64 validation — zero extra dependencies.
> Try: *"Encode 'Hello Rahul' to base64"* or *"Decode SGVsbG8gUmFodWw="*

---

### `systeminfo`
System health and resource monitoring tools using OSHI.
Reports CPU load, memory usage, disk space, OS details and a full health summary in one call.
> Try: *"How much memory is available?"* or *"Give me a full system health summary"*

---

## How Tool Calling Works
```
User message → ChatClient → LLM decides which tool to call
                                        ↓
                              ToolRegistry auto-discovers
                              all @Component beans with @Tool
                                        ↓
                              Tool executes → result returned to LLM
                                        ↓
                              LLM generates final natural language response
```

---

## Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.4 + Spring AI 1.0 |
| LLM | OpenAI / Azure OpenAI |
| Memory | `MessageWindowChatMemory` (last 20 messages) |
| Database | H2 in-memory + JPA |
| Streaming | `Flux<String>` + SSE |
| API Docs | springdoc-openapi + Swagger UI |
| System Info | OSHI 6.6 |
| Markdown | Commonmark 0.22 |

---

## Running
```bash
# Set your OpenAI key
export OPEN_AI_KEY=sk-proj-xxxxxxxx

./mvnw spring-boot:run
```

Swagger UI → `http://localhost:8089/swagger-ui.html`
H2 Console → `http://localhost:8089/h2-console`