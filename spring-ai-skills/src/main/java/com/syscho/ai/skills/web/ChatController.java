package com.syscho.ai.skills.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping({"/", "chat"})
    public String chatPage(Model model, @RequestParam(required = false) String sessionId) {
        model.addAttribute("sessionId", getSessionID(sessionId));
        return "chat";
    }

    // ── Sessions ────────────────────────────────────────────────

    @GetMapping(path = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ChatService.SessionInfo> sessions() {
        return chatService.listSessions();
    }

    @PostMapping(path = "/api/sessions")
    public @ResponseBody String createSession() {
        return chatService.createSession();
    }

    /** Delete a single session by ID */
    @DeleteMapping(path = "/api/sessions/{sessionId}")
    public @ResponseBody String deleteSession(@PathVariable String sessionId) {
        chatService.deleteSession(sessionId);
        return "ok";
    }

    /** Delete multiple sessions at once — pass a JSON array of IDs */
    @DeleteMapping(path = "/api/sessions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String deleteSessions(@RequestBody List<String> sessionIds) {
        chatService.deleteSessions(sessionIds);
        return "ok";
    }

    /** Delete ALL sessions */
    @DeleteMapping(path = "/api/sessions/all")
    public @ResponseBody String deleteAllSessions() {
        chatService.deleteAllSessions();
        return "ok";
    }

    // ── Chat ────────────────────────────────────────────────────

    @PostMapping(path = "/api/chat")
    public @ResponseBody String postMessage(@RequestParam String sessionId,
                                            @RequestParam String message) {
        return chatService.sendUserMessage(sessionId, message);
    }

    // ── History ─────────────────────────────────────────────────

    @GetMapping(path = "/api/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ChatMessage> history(@RequestParam String sessionId) {
        return chatService.getHistory(sessionId);
    }

    @PostMapping(path = "/api/history/clear")
    public @ResponseBody String clearHistory(@RequestParam String sessionId,
                                             @RequestParam String scope,
                                             @RequestParam(required = false) List<Integer> indices) {
        chatService.clearHistory(sessionId, scope, indices);
        return "ok";
    }

    // ── Util ────────────────────────────────────────────────────

    private static String getSessionID(String sessionId) {
        return sessionId != null && !sessionId.isBlank() ? sessionId : UUID.randomUUID().toString();
    }
}