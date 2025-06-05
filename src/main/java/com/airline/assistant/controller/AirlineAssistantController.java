package com.airline.assistant.controller;

import com.airline.assistant.service.AirlineAssistantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AirlineAssistantController {

    private final AirlineAssistantService assistantService;

    public AirlineAssistantController(AirlineAssistantService assistantService) {
        this.assistantService = assistantService;
    }
    
    /**
     * Record classes for request and response objects
     */
    public record ChatRequest(String message) {}
    public record ChatResponse(String response) {}

    /**
     * Chat endpoint that processes user input with conversation memory
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(HttpSession httpSession, @RequestBody ChatRequest request) {
        try {
            String message = request.message();
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Get conversation ID from session
            String conversationId = httpSession.getId();
            String response = assistantService.processMessage(message, conversationId);
            return ResponseEntity.ok(new ChatResponse(response));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Endpoint to clear conversation memory for the current session
     */
    @DeleteMapping("/chat/memory")
    public ResponseEntity<Void> clearMemory(HttpSession httpSession) {
        String conversationId = httpSession.getId();
        assistantService.clearMemory(conversationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
