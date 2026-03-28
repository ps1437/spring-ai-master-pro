package com.syscho.ai;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AgentEvent.Thinking.class,    name = "thinking"),
    @JsonSubTypes.Type(value = AgentEvent.ToolCall.class,    name = "tool_call"),
    @JsonSubTypes.Type(value = AgentEvent.ToolResult.class,  name = "tool_result"),
    @JsonSubTypes.Type(value = AgentEvent.Token.class,       name = "token"),
    @JsonSubTypes.Type(value = AgentEvent.Done.class,        name = "done"),
    @JsonSubTypes.Type(value = AgentEvent.Error.class,       name = "error"),
})
public sealed interface AgentEvent {
    record Thinking(String text)                         implements AgentEvent {}
    record ToolCall(String tool, Object input)           implements AgentEvent {}
    record ToolResult(String tool, Object result)        implements AgentEvent {}
    record Token(String text)                            implements AgentEvent {}
    record Done()                                        implements AgentEvent {}
    record Error(String message)                         implements AgentEvent {}
}