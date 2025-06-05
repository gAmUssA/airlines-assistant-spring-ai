package com.airline.assistant.service;

import com.airline.assistant.rag.AirlineDocument;
import com.airline.assistant.rag.SimpleVectorStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class KnowledgeBaseServiceTest {

    private KnowledgeBaseService knowledgeBaseService;
    private SimpleVectorStore mockVectorStore;
    private ResourceLoader mockResourceLoader;

    @BeforeEach
    void setUp() {
        mockVectorStore = Mockito.mock(SimpleVectorStore.class);
        mockResourceLoader = Mockito.mock(ResourceLoader.class);
        
        // Create a test instance with mocked dependencies
        knowledgeBaseService = new KnowledgeBaseService(mockResourceLoader, mockVectorStore) {
            // Override the initialization method to avoid loading real files
            @Override
            public void initializeKnowledgeBase() throws IOException {
                // Do nothing in the test
            }
        };
    }

    @Test
    void testGetRelevantContext_WithMatchingDocuments() {
        // Prepare test data
        Map<String, Object> metadata1 = new HashMap<>();
        metadata1.put("source", "delta-skymiles.md");
        
        Map<String, Object> metadata2 = new HashMap<>();
        metadata2.put("source", "united-mileageplus.md");
        
        AirlineDocument doc1 = new AirlineDocument("Delta SkyMiles do not expire.", metadata1);
        AirlineDocument doc2 = new AirlineDocument("United MileagePlus miles expire after 18 months of inactivity.", metadata2);
        
        List<AirlineDocument> mockResults = Arrays.asList(doc1, doc2);
        
        // Configure mock behavior
        when(mockVectorStore.similaritySearch(anyString(), anyInt())).thenReturn(mockResults);
        
        // Execute the method under test
        String result = knowledgeBaseService.getRelevantContext("Do Delta miles expire?");
        
        // Verify the result
        assertTrue(result.contains("Delta SkyMiles do not expire"));
        assertTrue(result.contains("From delta-skymiles.md"));
        assertTrue(result.contains("United MileagePlus miles expire after 18 months"));
        assertTrue(result.contains("From united-mileageplus.md"));
    }

    @Test
    void testGetRelevantContext_WithNoMatchingDocuments() {
        // Configure mock behavior to return empty list
        when(mockVectorStore.similaritySearch(anyString(), anyInt())).thenReturn(List.of());
        
        // Execute the method under test
        String result = knowledgeBaseService.getRelevantContext("Some query with no matches");
        
        // Verify the result is empty
        assertEquals("", result);
    }
}