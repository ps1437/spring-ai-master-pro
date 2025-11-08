package com.syscho.ai.chat.structured_response;

import com.syscho.ai.chat.structured_response.model.CountryCities;
import com.syscho.ai.chat.structured_response.model.HospitalInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StructuredResponseController  implements  StructuredChatApi {

    private final ChatClient chatClient;

    // AI Prompt to instruct model to return structured hospital info
    public static final String HOSPITAL_SYSTEM_PROMPT = """
            You are an internal hospital assistant.
            Your job is to answer patient or visitor queries about hospital services.
            Respond in a structured JSON format that matches the HospitalInfoResponse class:
            - patientName (optional)
            - query
            - responseMessage
            - services: List of objects with type, name, description, cost, availability
            Only include relevant information for the user's query.
            """;

    // AI Prompt for general structured country/city info
    public static final String COUNTRY_SYSTEM_PROMPT = """
            You are an AI assistant that only provides information about countries and cities.
            If the user asks anything outside of countries or cities, politely respond with:
            'Sorry, I can only provide information about countries and cities.'
            Always respond in a structured JSON format that matches CountryCities.
            """;


    @GetMapping("/cities")
    public ResponseEntity<CountryCities> chatBean(@RequestParam("message") String message) {
        CountryCities countryCities = chatClient
                .prompt()
                .system(COUNTRY_SYSTEM_PROMPT)
                .user(message)
                .call()
                .entity(CountryCities.class);
        return ResponseEntity.ok(countryCities);
    }
    @GetMapping("/hospital-info")
    public ResponseEntity<HospitalInfoResponse> hospitalInfo(@RequestParam("message") String message) {
        HospitalInfoResponse response = chatClient
                .prompt()
                .system(HOSPITAL_SYSTEM_PROMPT)
                .user(message)
                .call()
                .entity(HospitalInfoResponse.class);

        return ResponseEntity.ok(response);
    }
}
