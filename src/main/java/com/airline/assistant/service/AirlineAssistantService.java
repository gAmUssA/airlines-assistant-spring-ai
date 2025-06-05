package com.airline.assistant.service;

import com.airline.assistant.util.UserIntroductionDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AirlineAssistantService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AirlineAssistantService.class);
  // Regular expressions to detect Markdown list patterns
  private static final Pattern ORDERED_LIST_PATTERN = Pattern.compile("^(\\d+\\.\\s+.*)$", Pattern.MULTILINE);
  private static final Pattern UNORDERED_LIST_PATTERN = Pattern.compile("^([*-]\\s+.*)$", Pattern.MULTILINE);

  private final ChatClient chatClient;
  private final ChatMemory chatMemory;
  private final KnowledgeBaseService knowledgeBaseService;
  private final UserService userService;

  public AirlineAssistantService(ChatClient chatClient, ChatMemory chatMemory, 
                                KnowledgeBaseService knowledgeBaseService, UserService userService) {
    this.chatClient = chatClient;
    this.chatMemory = chatMemory;
    this.knowledgeBaseService = knowledgeBaseService;
    this.userService = userService;
  }

  public String processMessage(String userMessage, String conversationId) {
    LOGGER.debug("Processing message for session {}: {}", conversationId, userMessage);

    try {
      long startTime = System.currentTimeMillis();

      // Get relevant context from knowledge base
      String relevantContext = knowledgeBaseService.getRelevantContext(userMessage);
      LOGGER.debug("Retrieved relevant context: {}", relevantContext);

      // Check if the message contains a user introduction
      Optional<String> usernameOpt = UserIntroductionDetector.detectUsername(userMessage);

      // Get user information if a username was detected
      String userInfo = "";
      if (usernameOpt.isPresent()) {
          String username = usernameOpt.get();
          LOGGER.debug("Detected user introduction with username: {}", username);
          userInfo = userService.getUserInfoForPrompt(username);
      }

      // Construct prompt with relevant context and user information
      String enhancedPrompt = userMessage;

      // Add context and user information to the prompt if available
      if (!relevantContext.isEmpty() || !userInfo.isEmpty()) {
          StringBuilder promptBuilder = new StringBuilder();

          if (!userInfo.isEmpty()) {
              promptBuilder.append(userInfo).append("\n\n");
              LOGGER.debug("Added user information to prompt: {}", userInfo);
          }

          if (!relevantContext.isEmpty()) {
              promptBuilder.append(relevantContext).append("\n\n");
          }

          promptBuilder.append("User question: ").append(userMessage);
          enhancedPrompt = promptBuilder.toString();
          LOGGER.debug("Enhanced prompt with context and user info: {}", enhancedPrompt);
      }

      String response = chatClient
          .prompt()
          .user(enhancedPrompt)
          .advisors(advisorParams -> advisorParams.param(ChatMemory.CONVERSATION_ID, conversationId))
          .call()
          .content();

      long endTime = System.currentTimeMillis();
      long responseTime = endTime - startTime;

      LOGGER.info("Response generated in {}ms for session {}", responseTime, conversationId);
      LOGGER.debug("Response: {}", response);

      // Format response to enhance list display in UI
      return formatResponseForUI(response);

    } catch (Exception e) {
      LOGGER.error("Error processing message: {}", e.getMessage(), e);
      return "I apologize, but I'm experiencing technical difficulties. Please try again in a moment.";
    }
  }

  public void clearMemory(String conversationId) {
    LOGGER.debug("Clearing memory for session {}", conversationId);
    chatMemory.clear(conversationId);
  }

  /**
   * Formats the LLM response to improve UI rendering of lists
   * @param response The raw LLM response
   * @return Formatted response with enhanced list markup
   */
  private String formatResponseForUI(String response) {
    if (response == null) {
      return "";
    }

    // First, let's convert the markdown response to HTML
    // This is a simpler approach that handles lists more effectively

    // 1. Process ordered lists (1. Item format)
    StringBuilder result = new StringBuilder(response);
    String processed = result.toString();

    // Find ordered list groups and wrap them
    Pattern orderedListGroup = Pattern.compile("((?:^\\d+\\.\\s+.*(?:\n|$))+)", Pattern.MULTILINE);
    Matcher orderedGroupMatcher = orderedListGroup.matcher(processed);

    StringBuffer sb = new StringBuffer();
    while (orderedGroupMatcher.find()) {
      String listContent = orderedGroupMatcher.group(1);
      StringBuilder wrappedItems = new StringBuilder("<div class=\"ai-list-container ai-ordered-list\">");

      // Process each list item
      Pattern itemPattern = Pattern.compile("^(\\d+\\.\\s+.*)$", Pattern.MULTILINE);
      Matcher itemMatcher = itemPattern.matcher(listContent);

      while (itemMatcher.find()) {
        String item = itemMatcher.group(1);
        wrappedItems.append("<div class=\"ai-list-item\">").append(item).append("</div>");
      }

      wrappedItems.append("</div>");
      // Escape $ and backslashes in the replacement
      String replacement = wrappedItems.toString().replace("$", "\\$").replace("\\", "\\\\");
      orderedGroupMatcher.appendReplacement(sb, replacement);
    }
    orderedGroupMatcher.appendTail(sb);
    processed = sb.toString();

    // Find unordered list groups and wrap them
    Pattern unorderedListGroup = Pattern.compile("((?:^[*-]\\s+.*(?:\n|$))+)", Pattern.MULTILINE);
    Matcher unorderedGroupMatcher = unorderedListGroup.matcher(processed);

    sb = new StringBuffer();
    while (unorderedGroupMatcher.find()) {
      String listContent = unorderedGroupMatcher.group(1);
      StringBuilder wrappedItems = new StringBuilder("<div class=\"ai-list-container ai-unordered-list\">");

      // Process each list item
      Pattern itemPattern = Pattern.compile("^([*-]\\s+.*)$", Pattern.MULTILINE);
      Matcher itemMatcher = itemPattern.matcher(listContent);

      while (itemMatcher.find()) {
        String item = itemMatcher.group(1);
        wrappedItems.append("<div class=\"ai-list-item\">").append(item).append("</div>");
      }

      wrappedItems.append("</div>");
      // Escape $ and backslashes in the replacement
      String replacement = wrappedItems.toString().replace("$", "\\$").replace("\\", "\\\\");
      unorderedGroupMatcher.appendReplacement(sb, replacement);
    }
    unorderedGroupMatcher.appendTail(sb);

    return sb.toString();
  }
}
