package com.syscho.ai.skills.web;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import com.syscho.ai.skills.persist.ChatMessageEntity;
import com.syscho.ai.skills.persist.ChatMessageRepository;
import com.syscho.ai.skills.persist.ChatSessionEntity;
import com.syscho.ai.skills.persist.ChatSessionRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private final Map<String, List<ChatMessage>> histories = new ConcurrentHashMap<>();
    private final ChatClient chatClient;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public record SessionInfo(String id, String title, String preview, long lastUpdated) {}

    public ChatService(ChatClient.Builder builder,
                       ChatSessionRepository sessionRepository,
                       ChatMessageRepository messageRepository) {
        this.chatClient = builder.build();
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        loadFromDatabase();
    }

    public List<ChatMessage> getHistory(String sessionId) {
        return histories.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }

    public List<SessionInfo> listSessions() {
        List<SessionInfo> out = new ArrayList<>();
        histories.forEach((id, msgs) -> {
            String preview = "";
            long last = 0L;
            if (!msgs.isEmpty()) {
                ChatMessage m = msgs.get(msgs.size() - 1);
                String content = m.content();
                preview = content.length() > 80 ? content.substring(0, 77) + "..." : content;
                last = m.timestamp();
            }
            out.add(new SessionInfo(id, "Conversation", preview, last));
        });
        return out;
    }

    public String createSession() {
        String id = UUID.randomUUID().toString();
        histories.computeIfAbsent(id, k -> new ArrayList<>());
        try {
            sessionRepository.save(new ChatSessionEntity(id, "Conversation", Instant.now().toEpochMilli()));
        } catch (Exception ignored) {}
        return id;
    }

    public String sendUserMessage(String sessionId, String message) {
        List<ChatMessage> history = getHistory(sessionId);
        ChatMessage userMsg = new ChatMessage("user", message, Instant.now().toEpochMilli());
        history.add(userMsg);
        saveMessageToPersistence(sessionId, userMsg);

        String reply = generateReply(message);

        ChatMessage assistantMsg = new ChatMessage("assistant", reply, Instant.now().toEpochMilli());
        history.add(assistantMsg);
        saveMessageToPersistence(sessionId, assistantMsg);
        return reply;
    }

    private String generateReply(String userMessage) {
        try {
            String content = chatClient.prompt().user(userMessage).call().content();
            return content == null ? "" : content;
        } catch (Exception e) {
            return "(AI error) " + e.getMessage() + "\n\nFallback echo: " + userMessage;
        }
    }

    public void clearHistory(String sessionId, String scope, List<Integer> indices) {
        if (!"all".equalsIgnoreCase(scope)) return;
        getHistory(sessionId).clear();
        try { messageRepository.deleteBySessionId(sessionId); } catch (Exception ignored) {}
    }

    /** Delete a single session — memory + DB */
    public void deleteSession(String sessionId) {
        histories.remove(sessionId);
        try {
            messageRepository.deleteBySessionId(sessionId);
            sessionRepository.deleteById(sessionId);
        } catch (Exception ignored) {}
    }

    public void deleteSessions(List<String> sessionIds) {
        sessionIds.forEach(this::deleteSession);
    }


    public void deleteAllSessions() {
        histories.clear();
        try {
            messageRepository.deleteAll();
            sessionRepository.deleteAll();
        } catch (Exception ignored) {}
    }

    private void saveMessageToPersistence(String sessionId, ChatMessage msg) {
        try {
            messageRepository.save(new ChatMessageEntity(sessionId, msg.role(), msg.content(), msg.timestamp()));
            sessionRepository.findById(sessionId).ifPresent(s -> {
                s.setLastUpdated(msg.timestamp());
                sessionRepository.save(s);
            });
        } catch (Exception ignored) {}
    }

    private void loadFromDatabase() {
        try {
            for (ChatSessionEntity s : sessionRepository.findAll()) {
                List<ChatMessage> converted = messageRepository
                        .findBySessionIdOrderByTimestampAsc(s.getId())
                        .stream()
                        .map(me -> new ChatMessage(me.getRole(), me.getContent(), me.getTimestamp()))
                        .toList();
                histories.put(s.getId(), new ArrayList<>(converted));
            }
        } catch (Exception ignored) {}
    }
}