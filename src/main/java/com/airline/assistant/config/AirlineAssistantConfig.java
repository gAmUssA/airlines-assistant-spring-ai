package com.airline.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AirlineAssistantConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("You are a helpful airline loyalty program assistant. " +
                        "You help travelers understand and maximize their benefits with " +
                        "airline loyalty programs, particularly Delta SkyMiles and United MileagePlus. " +
                        "Provide accurate, helpful, and friendly responses.")
                .build();
    }
}
