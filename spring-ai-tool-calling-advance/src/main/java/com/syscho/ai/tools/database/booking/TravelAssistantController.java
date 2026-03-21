package com.syscho.ai.tools.database.booking;

import com.syscho.ai.tools.database.booking.tools.BookingTool;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Travel Assistant", description = "AI-powered travel assistant with flight search, booking management, weather and currency tools")
@RestController
@RequestMapping("/chat/tools")
@RequiredArgsConstructor
public class TravelAssistantController {

    private final ChatClient chatClient;
    private final BookingTool bookingTool;

    @Operation(
            summary = "Ask the travel assistant",
            description = """
                    Send a natural language query to the AI travel assistant.
                    The assistant can handle multiple tasks in a single message.
                    
                    **Supported operations:**
                    - Flight search: search available flights between cities
                    - Booking: create, view, cancel, update bookings
                    - Weather: get current weather for any city
                    - Currency: convert between currencies
                    - Time: get current time / timezone info
                    
                    **Example queries:**
                    - `What flights are available from BOM to DEL?`
                    - `Book flight AI-202 from BOM to DEL for Rahul Sharma`
                    - `Show all bookings for Rahul Sharma`
                    - `Cancel booking 3`
                    - `What is the weather in Hyderabad?`
                    - `Convert 5000 INR to USD`
                    - `I'm flying from Mumbai to Delhi tomorrow - show me weather, flights and convert 5000 INR to USD`
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI response with tool results merged"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid parameters"),
            @ApiResponse(responseCode = "500", description = "AI or tool execution failure")
    })
    @GetMapping
    public String ask(
            @Parameter(
                    description = "Natural language travel query",
                    required = true,
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Flight search",
                                    value = "What flights are available from BOM to DEL?"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Create booking",
                                    value = "Book flight AI-202 from BOM to DEL for Rahul Sharma"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "View bookings",
                                    value = "Show all bookings for Rahul Sharma"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Cancel booking",
                                    value = "Cancel booking 3"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Weather",
                                    value = "What is the weather in Hyderabad?"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Currency",
                                    value = "Convert 5000 INR to USD"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Multi-tool",
                                    value = "Flying from Mumbai to Delhi tomorrow. Show weather, available flights and convert 5000 INR to USD"
                            )
                    }
            )
            @RequestParam(defaultValue = "What flights are available from BOM to DEL?") String userMessage,

            @Parameter(
                    description = "Unique conversation ID to maintain chat memory across requests",
                    required = true,
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Session 1",
                                    value = "session-001"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "User specific",
                                    value = "user-rahul-123"
                            )
                    }
            )
            @RequestHeader(defaultValue = "session-001") String conversionId
    ) {
        return chatClient.prompt()
                .toolCallbacks(ToolCallbacks.from(bookingTool))
                .user(userMessage)
                .advisors(advisor -> advisor
                        .param(ChatMemory.CONVERSATION_ID, conversionId)
                )
                .call()
                .content();
    }
}