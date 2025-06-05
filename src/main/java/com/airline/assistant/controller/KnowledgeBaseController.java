package com.airline.assistant.controller;

import com.airline.assistant.rag.AirlineDocument;
import com.airline.assistant.service.KnowledgeBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/knowledge")
@CrossOrigin(origins = "*")
public class KnowledgeBaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseController.class);
    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    /**
     * Record classes for request and response objects
     */
    public record QueryRequest(String query, int limit) {}
    public record DocumentResponse(String content, Map<String, Object> metadata, double score) {}
    public record QueryResponse(List<DocumentResponse> documents, String formattedContext) {}

    /**
     * Endpoint to query the knowledge base
     */
    @PostMapping("/query")
    public ResponseEntity<QueryResponse> query(@RequestBody QueryRequest request) {
        try {
            String query = request.query();
            int limit = request.limit() > 0 ? request.limit() : 3; // Default to 3 if not specified or invalid
            
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            LOGGER.info("Querying knowledge base with: {}", query);
            
            // Get relevant documents
            List<AirlineDocument> documents = knowledgeBaseService.similaritySearch(query, limit);
            
            // Get formatted context
            String formattedContext = knowledgeBaseService.getRelevantContext(query);
            
            // Convert documents to response format
            List<DocumentResponse> documentResponses = documents.stream()
                .map(doc -> new DocumentResponse(
                    doc.getContent(),
                    doc.getMetadata(),
                    0.0 // We don't have actual scores in our simplified implementation
                ))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new QueryResponse(documentResponses, formattedContext));
            
        } catch (Exception e) {
            LOGGER.error("Error querying knowledge base: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint to get information about the knowledge base
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        try {
            int documentCount = knowledgeBaseService.getVectorStore().size();
            
            return ResponseEntity.ok(Map.of(
                "documentCount", documentCount,
                "status", "active"
            ));
            
        } catch (Exception e) {
            LOGGER.error("Error getting knowledge base info: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}