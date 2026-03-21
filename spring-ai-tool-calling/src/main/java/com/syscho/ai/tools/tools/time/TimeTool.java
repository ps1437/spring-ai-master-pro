package com.syscho.ai.tools.tools.time;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TimeTool {

    @Tool(name = "currentTime", description = "Call when user ask current time")
    public LocalDateTime currentTime() {
        log.info("Current Time Tools Executed");
        return LocalDateTime.now();
    }
}
