package com.syscho.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class StreamingAgentService {

    private final ChatClient chatClientWithTools;

    private final ObjectMapper mapper;

    public Flux<AgentEvent> stream(String userMessage, String sessionId) {

        Sinks.Many<AgentEvent> sink = Sinks.many().unicast().onBackpressureBuffer();


        chatClientWithTools
            .prompt()
            .system("You are a DevPortal AI agent. Think step by step.")
            .user(userMessage)
            .stream()
            .chatResponse()
            .doOnNext(response -> {
                String token = response.getResult().getOutput().getContent();
                if (token != null && !token.isBlank()) {
                    sink.tryEmitNext(new AgentEvent.Token(token));
                }
            })
            .doOnComplete(() -> {
                sink.tryEmitNext(new AgentEvent.Done());
                sink.tryEmitComplete();
            })
            .doOnError(e -> {
                sink.tryEmitNext(new AgentEvent.Error(e.getMessage()));
                sink.tryEmitComplete();
            })
            .subscribe();

        return sink.asFlux();
    }
}