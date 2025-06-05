package com.airline.assistant.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirlineAssistantServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatMemory chatMemory;

    @Mock
    private KnowledgeBaseService knowledgeBaseService;

    @InjectMocks
    private AirlineAssistantService airlineAssistantService;

    @Test
    void testKnowledgeBaseServiceIsCalledWithUserMessage() {
        // Set up test data
        String userMessage = "Do Delta miles expire?";
        String conversationId = "test-conversation-id";

        // Configure mock behavior
        when(knowledgeBaseService.getRelevantContext(userMessage))
            .thenReturn("Some relevant context");

        // This test will throw an exception when calling chatClient.prompt()
        // but we only want to verify that knowledgeBaseService is called correctly
        try {
            airlineAssistantService.processMessage(userMessage, conversationId);
        } catch (Exception e) {
            // Expected exception, ignore
        }

        // Verify that the knowledge base service was called with the correct user message
        verify(knowledgeBaseService).getRelevantContext(userMessage);
    }

    @Test
    void testClearMemory() {
        // Set up test data
        String conversationId = "test-conversation-id";

        // Execute the method under test
        airlineAssistantService.clearMemory(conversationId);

        // Verify that the chat memory was cleared with the correct conversation ID
        verify(chatMemory).clear(conversationId);
    }
}
