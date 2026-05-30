package com.syscho.ai.skills.persist;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity(name = "chat_session")
public class ChatSessionEntity {

    @Id
    private String id;
    private String title;
    private long lastUpdated;

    public ChatSessionEntity() {}

    public ChatSessionEntity(String id, String title, long lastUpdated) {
        this.id = id;
        this.title = title;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
