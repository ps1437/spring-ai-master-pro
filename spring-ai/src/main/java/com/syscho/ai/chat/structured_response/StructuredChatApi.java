package com.syscho.ai.chat.structured_response;

import com.syscho.ai.chat.structured_response.model.CountryCities;
import com.syscho.ai.chat.structured_response.model.HospitalInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(
        name = "AI Chat & Response API",
        description = "Unified endpoints for simple chat, memory-based chat, prompt-based interactions, and structured AI responses"
)

public interface StructuredChatApi {

    @Operation(
            summary = "Get structured country and city information",
            description = """
                    Sends a message to the AI model and returns a **structured JSON response** 
                    about countries and cities.  
                    If the user asks something unrelated, the model will respond politely with an error message.
                    """,
            parameters = {
                    @Parameter(
                            name = "message",
                            description = "User query related to countries or cities",
                            example = "Tell me about cities in Japan"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Structured Country and Cities response",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CountryCities.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "country": "Japan",
                                              "cities": ["Tokyo", "Osaka", "Kyoto"],
                                              "message": "Japan is an island nation with major cities like Tokyo, Osaka, and Kyoto."
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    ResponseEntity<CountryCities> chatBean(@RequestParam("message") String message);

    @Operation(
            summary = "Get structured hospital service information",
            description = """
                    Sends a question about hospital services or patient inquiries to the AI model.  
                    The model replies with a **structured JSON response** matching the `HospitalInfoResponse` class.  
                    This includes:
                    - patient details (optional)
                    - main query and AI-generated answer
                    - list of hospital services with cost and availability.
                    """,
            parameters = {
                    @Parameter(
                            name = "message",
                            description = "User query about hospital information or services",
                            example = "Show me the available cardiology services"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Structured hospital response",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HospitalInfoResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "patientName": "Alice",
                                              "query": "Show me cardiology services",
                                              "responseMessage": "Here are the available cardiology services.",
                                              "services": [
                                                {
                                                  "type": "Consultation",
                                                  "name": "Cardiology Department",
                                                  "description": "Heart health consultations and checkups",
                                                  "cost": "500 INR",
                                                  "availability": "Available daily"
                                                }
                                              ]
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid query", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    ResponseEntity<HospitalInfoResponse> hospitalInfo(@RequestParam("message") String message);
}
