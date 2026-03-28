package com.syscho.ai.tools.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class FunTool {

    private static final String JOKE_API = "https://v2.jokeapi.dev/joke";

    private final RestClient restClient = RestClient.create();


    @Tool(name = "getRandomJoke",
          description = "Get a random joke to make the user laugh")
    public String getRandomJoke() {
        log.info("[tool=getRandomJoke]");
        return fetchJoke(JOKE_API + "/Any?blacklistFlags=nsfw,racist,sexist");
    }

    @Tool(name = "getJokeByCategory",
          description = "Get a joke by category. Categories: Programming, Misc, Pun, Spooky, Christmas, Dark")
    public String getJokeByCategory(
            @ToolParam(description = "Joke category: Programming, Misc, Pun, Spooky, Christmas, Dark") String category
    ) {
        log.info("[tool=getJokeByCategory] category={}", category);
        return fetchJoke(JOKE_API + "/" + category + "?blacklistFlags=nsfw,racist,sexist");
    }



    @Tool(name = "getProgrammingJoke",
          description = "Get a programming / developer joke")
    public String getProgrammingJoke() {
        log.info("[tool=getProgrammingJoke]");
        return fetchJoke(JOKE_API + "/Programming?blacklistFlags=nsfw,racist,sexist");
    }


    private String fetchJoke(String url) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            String type = (String) response.get("type");

            if ("single".equals(type)) {
                return (String) response.get("joke");
            } else {
                String setup    = (String) response.get("setup");
                String delivery = (String) response.get("delivery");
                return setup + "\n\n... " + delivery;
            }

        } catch (Exception e) {
            log.error("[tool=fetchJoke] failed url={} error={}", url, e.getMessage());
            return "Joke API is taking a coffee break. Try again later!";
        }
    }
}