package com.airline.assistant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Use WebMvcTest to avoid loading the full application context with AI components
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
               properties = {"spring.ai.openai.api-key=test-key"})
class AirlineAssistantApplicationTests {

    @Test
    void contextLoads() {
        // Just verify that the context loads successfully
    }
}
