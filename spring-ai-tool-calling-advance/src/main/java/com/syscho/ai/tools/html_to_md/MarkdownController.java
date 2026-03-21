package com.syscho.ai.tools.html_to_md;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Markdown Tools", description = "AI-powered markdown to HTML conversion tools")
@RestController
@RequestMapping("/markdown")
@RequiredArgsConstructor
public class MarkdownController {

    private final ChatClient chatClient;
    private final MarkdownTool markdownTool;

    @Operation(
            summary = "Convert inline markdown to HTML",
            description = "Pass raw markdown text directly. The AI converts it to HTML and returns the result in the response."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTML string returned"),
            @ApiResponse(responseCode = "500", description = "Conversion failed")
    })
    @PostMapping("/convert")
    public String convert(
            @Parameter(
                    description = "Raw markdown content",
                    examples = {
                            @ExampleObject(name = "Headings and lists", value = """
                                    # My Report
                                    
                                    ## Summary
                                    This is a **bold** statement and _italic_ text.
                                    
                                    - Item one
                                    - Item two
                                    - Item three
                                    """),
                            @ExampleObject(name = "Table", value = """
                                    ## Flight Schedule
                                    
                                    | Flight | From | To  | Status    |
                                    |--------|------|-----|-----------|
                                    | AI-202 | BOM  | DEL | On Time   |
                                    | 6E-441 | DEL  | BLR | Delayed   |
                                    """),
                            @ExampleObject(name = "Code block", value = """
                                    ## Setup
                                    
                                    Run the following command:
```bash
                                    ./mvnw spring-boot:run
```
                                    """)
                    }
            )
            @RequestBody String markdown,

            @Parameter(description = "Conversation ID for chat memory", example = "session-001")
            @RequestHeader(defaultValue = "session-001") String conversationId
    ) {
        return chatClient.prompt()
                .toolCallbacks(ToolCallbacks.from(markdownTool))
                .user("Convert this markdown to HTML:\n\n" + markdown)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    @Operation(
            summary = "Convert a markdown file to HTML",
            description = "Provide the file path of a .md file on the server. The AI reads and converts it to HTML."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTML string returned"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Conversion failed")
    })
    @GetMapping("/convert-file")
    public String convertFile(
            @Parameter(
                    description = "Absolute or relative path to the .md file on the server",
                    examples = {
                            @ExampleObject(name = "Tmp file",     value = "/tmp/report.md"),
                            @ExampleObject(name = "Docs folder",  value = "/docs/README.md"),
                            @ExampleObject(name = "Project root", value = "README.md")
                    }
            )
            @RequestParam(defaultValue = "README.md") String filePath,

            @Parameter(description = "Conversation ID for chat memory", example = "session-001")
            @RequestHeader(defaultValue = "session-001") String conversationId
    ) {
        return chatClient.prompt()
                .toolCallbacks(ToolCallbacks.from(markdownTool))
                .user("Convert the markdown file at " + filePath + " to HTML")
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }



}