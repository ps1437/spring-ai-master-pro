package com.syscho.ai.skills.persist;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity(name = "chat_message")
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sessionId;
    private String role;

    @Lob
    private String content;

    private long timestamp;

    public ChatMessageEntity() {}

    public ChatMessageEntity(String sessionId, String role, String content, long timestamp) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
