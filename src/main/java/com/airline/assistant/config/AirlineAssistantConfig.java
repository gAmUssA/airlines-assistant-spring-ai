package com.airline.assistant.config;

import com.airline.assistant.tools.AirlineTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
public class AirlineAssistantConfig {

  @Value("${airline.assistant.ai-provider:OpenAI (Cloud)}")
  private String aiProvider;

  @Value("${airline.assistant.model-name:gpt-4o-mini}")
  private String modelName;

  @Bean
  public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
    // Customize MessageWindowChatMemory to use a window of 50 messages (Task 3.10)
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(chatMemoryRepository) // Uses auto-configured InMemoryChatMemoryRepository by default
        .maxMessages(50)
        .build();
  }

  @Bean
  @Primary
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, AirlineTools airlineTools, Environment environment) {
    String defaultSystem = "You are a helpful airline loyalty program assistant powered by " + aiProvider + " using " + modelName + ". " +
                       "You help travelers understand and maximize their benefits with " +
                       "airline loyalty programs, particularly Delta SkyMiles and United MileagePlus. " +
                       "Provide accurate, helpful, and friendly responses. " +
                       "Remember our conversation history to provide contextual responses. " +
                       "You have access to various tools to help users with flight information, " +
                       "SMS notifications, calendar events, loyalty point calculations, and status checks. " +
                       "Use these tools when appropriate to provide enhanced assistance.";
    if (environment.acceptsProfiles("local")) {
      defaultSystem += " Note: You are running locally on Ollama for enhanced privacy and offline capability.";
    }
    return chatClientBuilder
        .defaultSystem(defaultSystem)
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
                             .conversationId(ChatMemory.DEFAULT_CONVERSATION_ID)
                             .build())
        .defaultTools(airlineTools)
        .build();
  }
}
