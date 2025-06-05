package com.airline.assistant.controller;

import com.airline.assistant.model.User;
import com.airline.assistant.repository.UserRepository;
import com.airline.assistant.service.AirlineAssistantService;
import com.airline.assistant.service.SafeChatService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AirlineAssistantController {

  private final AirlineAssistantService assistantService;
  private final UserRepository userRepository;
  private final SafeChatService safeChatService;

  @Value("${airline.assistant.ai-provider:OpenAI (Cloud)}")
  private String aiProvider;

  @Value("${airline.assistant.model-name:gpt-4o-mini}")
  private String modelName;

  public AirlineAssistantController(AirlineAssistantService assistantService, UserRepository userRepository, SafeChatService safeChatService) {
    this.assistantService = assistantService;
    this.userRepository = userRepository;
    this.safeChatService = safeChatService;
  }

  /**
   * Record classes for request and response objects
   */
  public record ChatRequest(String message) {

  }

  public record ChatResponse(String response) {

  }

  public record AiProviderResponse(String provider, String model) {

  }

  public record UserProfileResponse(
      String username,
      String lastName,
      String loyaltyNumber,
      String loyaltyStatus,
      String preferredAirport,
      String airline
  ) {
    public static UserProfileResponse from(User user) {
      return new UserProfileResponse(
          user.getUsername(),
          user.getLastName(),
          user.getLoyaltyNumber(),
          user.getLoyaltyStatus(),
          user.getPreferredAirport(),
          user.getAirline()
      );
    }
  }

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
   * Safe chat endpoint with content filtering enabled
   */
  @PostMapping("/chat/safe")
  public ResponseEntity<ChatResponse> safeChat(HttpSession httpSession, @RequestBody ChatRequest request) {
    try {
      String message = request.message();
      if (message == null || message.trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
      }

      // Get conversation ID from session
      String conversationId = httpSession.getId();
      String response = safeChatService.processSafeMessage(message, conversationId);
      return ResponseEntity.ok(new ChatResponse(response));

    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Get content filter status
   */
  @GetMapping("/chat/filter-status")
  public ResponseEntity<SafeChatService.FilterStatus> getFilterStatus() {
    try {
      return ResponseEntity.ok(safeChatService.getFilterStatus());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Search for users by username (for user recognition)
   */
  @GetMapping("/users/search")
  public ResponseEntity<List<UserProfileResponse>> searchUsers(@RequestParam String query) {
    try {
      // Simple search by username containing the query (case-insensitive)
      List<User> users = userRepository.findAll().stream()
          .filter(user -> user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                         (user.getLastName() != null && user.getLastName().toLowerCase().contains(query.toLowerCase())))
          .limit(5) // Limit to 5 results
          .toList();
      
      List<UserProfileResponse> response = users.stream()
          .map(UserProfileResponse::from)
          .toList();
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Get user profile by username
   */
  @GetMapping("/users/{username}")
  public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
    try {
      Optional<User> user = userRepository.findByUsername(username);
      if (user.isPresent()) {
        return ResponseEntity.ok(UserProfileResponse.from(user.get()));
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint to get AI provider information
   */
  @GetMapping("/ai-provider")
  public ResponseEntity<AiProviderResponse> getAiProvider() {
    return ResponseEntity.ok(new AiProviderResponse(aiProvider, modelName));
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
