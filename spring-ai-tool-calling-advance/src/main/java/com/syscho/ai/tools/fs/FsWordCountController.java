package com.syscho.ai.tools.fs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Tag(name = "Word Counter", description = "Upload a text file and count words")
@RestController
@RequestMapping("/wordcount")
@RequiredArgsConstructor
public class FsWordCountController {

    private final ChatClient chatClient;

    @Operation(summary = "Upload a .txt file and count words")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String countFromFile(
            @RequestPart("file") MultipartFile file,
            @RequestHeader(defaultValue = "session-001") String conversationId
    ) throws Exception {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        return chatClient.prompt()
                .user("Count the number of words in this text:\n\n" + content)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}