package com.syscho.ai.tools.config;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ToolRegistry {


/*
  Register Static
  @Bean
    public List<ToolCallback> toolCallbacks(TimeTool timeTool) {
        return Stream.of(
                        ToolCallbacks.from(timeTool)
                )
                .toList();
    }*/


    @Bean
    public List<ToolCallback> toolCallbacks(ApplicationContext ctx) {
        return ctx.getBeansOfType(Object.class).values().stream()
                .filter(bean -> Arrays.stream(bean.getClass().getDeclaredMethods())
                        .anyMatch(m -> m.isAnnotationPresent(Tool.class)))
                .flatMap(bean -> Arrays.stream(ToolCallbacks.from(bean)))
                .toList();
    }
}
