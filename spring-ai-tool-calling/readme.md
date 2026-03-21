# Spring AI Tool Calling

A production-ready Spring AI system with dynamic tool discovery, multi-tool calling, chat memory, and external API integration.

---

## Stack

- Spring Boot + Spring AI (`ChatClient`)
- Ollama / OpenAI / Azure OpenAI LLM
- Tool annotations (`@Tool`, `@ToolParam`)
- H2 / PostgreSQL chat memory
- springdoc-openapi (Swagger UI)

---

## Project Structure

```
com.syscho.ai.tools
├── config
│   ├── ChatConfig.java              # ChatClient, memory, advisors
│   ├── SwaggerConfig.java           # OpenAPI / Swagger config
│   └── ToolRegistry.java            # Auto tool discovery
├── controller
│   └── ChatController.java          # REST entry point
└── tools
    ├── api/FetchGetTool.java         # Generic GET tool
    ├── time/TimeTool.java            # Current time tool
    ├── weather/
    │   ├── WeatherTool.java
    │   └── WeatherApiProperties.java
    └── multiTool/
        ├── TravelTools.java          # Weather + flights + currency tools
        ├── TravelAssistantService.java
        └── TravelController.java
```

---

## How It Works

```
Request → ChatController → ChatClient → Advisors → LLM
                                                    │
                                          ┌─────────┴─────────┐
                                     No tool             Tool needed
                                          │                    │
                                   Direct response      ToolRegistry
                                                              │
                                                       ToolCallback
                                                              │
                                                  ┌──────────┴──────────┐
                                             Single tool         Multiple tools
                                                  │                    │
                                           Tool result       Parallel execution
                                                  └──────────┬──────────┘
                                                             │
                                                    LLM final response
```

---

## Key Components

### ChatController
Accepts `userMessage` + `conversationId`, delegates to `ChatClient`.

### ChatConfig
Wires `ChatClient` with:
- `MessageWindowChatMemory` (last 20 messages, isolated by `conversationId`)
- `MessageChatMemoryAdvisor`
- `SimpleLoggerAdvisor`
- All tools via `defaultToolCallbacks(toolCallbacks)`

### ToolRegistry
Auto-discovers all Spring beans with `@Tool` methods — no manual wiring needed.

```java
ctx.getBeansOfType(Object.class).values().stream()
   .filter(bean -> hasToolAnnotatedMethods(bean))
   .flatMap(bean -> Arrays.stream(ToolCallbacks.from(bean)))
   .toList();
```

### SwaggerConfig

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI syschoAiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Syscho AI - Tool Calling Demo")
                        .description("Spring AI multi-tool calling demo with weather, flights & currency tools")
                        .version("1.0.0"));
    }
}
```

---

## Multi-Tool Calling

When a single user message requires data from multiple sources, the LLM emits multiple `tool_call` blocks in one response. Spring AI executes them and feeds all results back before generating the final answer.

### TravelTools

```java
@Component
public class TravelTools {

    @Tool(description = "Get current weather for a given city")
    public String getWeather(String city) { ... }

    @Tool(description = "Search available flights between two cities")
    public String searchFlights(String from, String to) { ... }

    @Tool(description = "Convert currency amount from one currency to another")
    public String convertCurrency(String from, String to, double amount) { ... }
}
```

### TravelAssistantService

```java
@Service
@RequiredArgsConstructor
public class TravelAssistantService {

    private final ChatClient.Builder chatClientBuilder;
    private final TravelTools travelTools;

    public String ask(String userMessage) {
        return chatClientBuilder
                .build()
                .prompt()
                .user(userMessage)
                .tools(travelTools)        // register at prompt level, not builder
                .call()
                .content();
    }
}
```

> ⚠️ Always register tools via `.tools()` on the prompt, not `.defaultTools()` on the builder — otherwise `No ToolCallback found` errors occur at runtime.

### TravelController

```java
@Tag(name = "Travel Assistant", description = "AI-powered travel query APIs using multi-tool calling")
@RestController
@RequestMapping("/travel")
@RequiredArgsConstructor
public class TravelController {

    private final TravelAssistantService assistantService;

    @Operation(
            summary = "Ask travel assistant",
            description = "Accepts a natural language query. The AI may invoke multiple tools " +
                          "(weather, flights, currency) in parallel to generate the response."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI-generated response with tool results merged"),
            @ApiResponse(responseCode = "500", description = "Internal error or AI tool failure")
    })
    @GetMapping("/ask")
    public String ask(
            @Parameter(description = "Natural language travel query",
                       example = "Flying from Mumbai to Delhi tomorrow. Weather, flights, and convert 5000 INR to USD?")
            @RequestParam String q
    ) {
        return assistantService.ask(q);
    }
}
```

---

## Spring AI Internal Classes

### `ToolCallingManager` → `DefaultToolCallingManager`

Auto-registered as a `@Bean` by Spring AI. You never call it directly — `ChatClient` delegates to it when the LLM returns a tool call.

**Internal method flow:**

| Method | What it does |
|--------|-------------|
| `executeToolCalls(prompt, chatResponse)` | Entry point — finds `Generation` with non-empty `ToolCalls` |
| `buildToolContext()` | Injects `TOOL_CALL_HISTORY` into context map |
| `executeToolCall()` | Loops over each `AssistantMessage.ToolCall` |
| `toolCallbackResolver.resolve(name)` | Finds `ToolCallback` via `DelegatingToolCallbackResolver` |
| `toolCallback.call(args, context)` | Invokes your `@Tool` method via reflection |
| `ToolExecutionExceptionProcessor` | Catches `ToolExecutionException` → converts to String |
| `buildConversationHistoryAfterToolExecution()` | Appends `AssistantMessage` + `ToolResponseMessage` to history |
| Returns `ToolExecutionResult` | Holds updated `conversationHistory` + `returnDirect` flag |

**Key internals worth knowing:**
- Each tool call is wrapped in a **Micrometer observation** for metrics and tracing
- `returnDirect = true` on a tool skips the second LLM call — result goes straight to user
- Multiple tool calls in one LLM response are all executed in the same loop before re-sending

**Override only when needed:**

```java
@Bean
public ToolCallingManager toolCallingManager() {
    return DefaultToolCallingManager.builder()
            .toolCallbackResolver(new DelegatingToolCallbackResolver(List.of()))
            .toolExecutionExceptionProcessor(
                DefaultToolExecutionExceptionProcessor.builder().build()
            )
            .build();
}
```

---

## Tools

| Tool | Trigger | Description |
|------|---------|-------------|
| `FetchGetTool` | Any URL query | Executes external GET APIs |
| `TimeTool` | "What time is it?" | Returns current system time |
| `WeatherTool.getCurrentWeather` | "Weather in X?" | Current conditions |
| `WeatherTool.getWeatherForecast` | "Forecast for X?" | 1–14 day forecast |
| `TravelTools.getWeather` | Travel queries | City weather via travel assistant |
| `TravelTools.searchFlights` | "Flights from X to Y?" | Available flight options |
| `TravelTools.convertCurrency` | "Convert X to Y" | Currency conversion |

---

## Configuration

```yaml
weather:
  api:
    key: ${WEATHER_API_KEY}
    base-url: https://api.weatherapi.com/v1

spring:
  ai:
    openai:
      api-key: ${OPEN_AI_KEY}

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
  api-docs:
    path: /v3/api-docs
```

Set `WEATHER_API_KEY` and `OPEN_AI_KEY` as environment variables in your run configuration.

---

## Dependencies

```xml
<!-- Spring AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>

<!-- Swagger / OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

---

## Sample Requests

```bash
# Single tool
GET /chat/tools?userMessage=What is the weather in Hyderabad?
Header: conversationId: session-123

# Multi-tool — triggers weather + flights + currency in parallel
GET /travel/ask?q=Flying from Mumbai to Delhi tomorrow. Weather, flights, and convert 5000 INR to USD?
```

Swagger UI available at: `http://localhost:8080/swagger-ui.html`

---

## Diagrams

![ChatClient to LLM Flow](chatclient_to_llm_flow.svg)
![Tool Execution Loop](tool_execution_loop_full.svg)
![DefaultToolCallingManager Internals](default_tool_calling_manager_source_flow.svg)

---

## Common Errors

| Error | Cause | Fix |
|-------|-------|-----|
| `No ToolCallback found for tool name: X` | Tools registered on builder, not prompt | Move to `.tools(bean)` on `.prompt()` |
| `@Tool` method not discovered | Missing `@Component` or method not `public` | Add `@Component`, make method `public` |
| Wrong `@Tool` import | Using non-Spring AI annotation | Use `org.springframework.ai.tool.annotation.Tool` |

---

## Notes

- Tool `description` must be clear — LLM uses it to pick the right tool
- Input validation lives inside each tool, not in the controller
- Each tool round-trip adds latency (~500ms–2s depending on LLM)
- `returnDirect = true` on `@Tool` skips the second LLM call — use for simple lookups
- Multi-tool calls in one LLM response are executed before the final response is generated