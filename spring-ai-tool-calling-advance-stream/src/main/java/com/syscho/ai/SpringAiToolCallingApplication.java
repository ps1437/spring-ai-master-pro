package com.syscho.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringAiToolCallingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiToolCallingApplication.class, args);
    }

}
