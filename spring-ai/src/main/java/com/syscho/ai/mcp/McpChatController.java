package com.syscho.ai.mcp;

import com.syscho.ai.mcp.tools.DepartmentTool;
import com.syscho.ai.mcp.tools.EmployeeTool;
import com.syscho.ai.mcp.tools.NewsTool;
import com.syscho.ai.mcp.tools.WeatherTool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
@Tag(
        name = "AI Chat with MCP (Database Tools)",
        description = """
                AI endpoints that use MCP tools connected to an in-memory H2 database.
                The AI can query structured company data using registered tools:
                                
                | Tool | Description | Example Question |
                |------|--------------|------------------|
                | 🧑‍💻 EmployeeTool | Retrieve employee details or list employees by department | "Show all employees in Engineering" |
                | 🏢 DepartmentTool | Get department info including location and employee count | "Where is the HR department located?" |
                | 🌤️ WeatherTool | (External) Fetch weather data | "What's the weather in Delhi?" |
                | 📰 NewsTool | (External) Fetch latest headlines | "Show me top business news" |
                """
)
public class McpChatController {

    private final ChatClient chatClient;
    private final EmployeeTool employeeTool;
    private final DepartmentTool departmentTool;
    private final WeatherTool weatherTool;
    private final NewsTool newsTool;

    @GetMapping("/ask")
    @Operation(
            summary = "Ask an AI question enriched by H2 database + MCP tools",
            description = """
                    Ask a natural question like:
                    - "List all employees in the Engineering department"
                    - "Show details of the HR department"
                    The AI may call MCP tools to retrieve live or database data.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "AI-generated response with structured data from H2 and APIs"
                    )
            }
    )
    public String ask(
            @RequestParam(defaultValue = "Show all employees") String question
    ) {
        return chatClient
                .prompt()

                .tools(employeeTool, departmentTool, weatherTool, newsTool)
                .user(question)
                .call()
                .content();
    }
}
