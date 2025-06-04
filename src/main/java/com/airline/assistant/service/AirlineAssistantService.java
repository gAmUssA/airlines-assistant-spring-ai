package com.airline.assistant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AirlineAssistantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AirlineAssistantService.class);

    private final ChatClient chatClient;

    public AirlineAssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String processMessage(String userMessage) {
        LOGGER.debug("Processing message: {}", userMessage);
        
        try {
            long startTime = System.currentTimeMillis();
            
            String response = chatClient
                    .prompt()
                    .user(userMessage)
                    .call()
                    .content();
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            LOGGER.info("Response generated in {}ms", responseTime);
            LOGGER.debug("Response: {}", response);
            
            return response;
            
        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage(), e);
            return "I apologize, but I'm experiencing technical difficulties. Please try again in a moment.";
        }
    }
}
