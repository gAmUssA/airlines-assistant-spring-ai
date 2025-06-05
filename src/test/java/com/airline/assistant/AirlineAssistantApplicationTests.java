package com.airline.assistant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.airline.assistant.service.KnowledgeBaseService;
import com.airline.assistant.rag.SimpleVectorStore;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-key",
    "spring.ai.embedding.openai.api-key=test-key"
})
@Import(AirlineAssistantApplicationTests.TestConfig.class)
class AirlineAssistantApplicationTests {

    @Test
    void contextLoads() {
        // Just verify that the context loads successfully
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public KnowledgeBaseService knowledgeBaseService() {
            return Mockito.mock(KnowledgeBaseService.class);
        }

        @Bean
        public SimpleVectorStore simpleVectorStore() {
            return Mockito.mock(SimpleVectorStore.class);
        }
    }
}
