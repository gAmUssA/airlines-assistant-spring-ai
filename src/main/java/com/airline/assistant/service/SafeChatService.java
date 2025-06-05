package com.airline.assistant.service;

import com.airline.assistant.config.SafeGuardConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service for managing safe chat interactions with content filtering
 */
@Service
public class SafeChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(SafeChatService.class);
    
    private final ChatClient regularChatClient;
    private final ChatClient safeChatClient;
    private final SafeGuardConfig safeGuardConfig;
    private final FilterAuditService auditService;
    
    public SafeChatService(ChatClient regularChatClient, 
                          @Qualifier("safeChatClient") ChatClient safeChatClient,
                          SafeGuardConfig safeGuardConfig,
                          FilterAuditService auditService) {
        this.regularChatClient = regularChatClient;
        this.safeChatClient = safeChatClient;
        this.safeGuardConfig = safeGuardConfig;
        this.auditService = auditService;
        
        logger.info("SafeChatService initialized - Safe mode enabled: {}", safeGuardConfig.isEnabled());
    }
    
    /**
     * Process message with content filtering enabled
     */
    public String processSafeMessage(String message, String conversationId) {
        logger.debug("Processing safe message for conversation {}: {}", conversationId, message);
        
        if (!safeGuardConfig.isEnabled()) {
            logger.warn("Safe mode is disabled, falling back to regular chat");
            auditService.logSafeChatUsage(conversationId, false);
            return processRegularMessage(message, conversationId);
        }
        
        try {
            String response = safeChatClient
                    .prompt()
                    .user(message)
                    .call()
                    .content();
                    
            // Log safe chat usage
            auditService.logSafeChatUsage(conversationId, true);
            
            // Check if response was filtered (simple heuristic)
            if (response != null && response.contains(safeGuardConfig.getWarningMessage())) {
                auditService.logFilterEvent(conversationId, message, response, "Content policy violation");
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error processing safe message: {}", e.getMessage(), e);
            auditService.logFilterEvent(conversationId, message, safeGuardConfig.getWarningMessage(), "Processing error");
            return safeGuardConfig.getWarningMessage();
        }
    }
    
    /**
     * Process message without content filtering
     */
    public String processRegularMessage(String message, String conversationId) {
        logger.debug("Processing regular message for conversation {}: {}", conversationId, message);
        
        try {
            return regularChatClient
                    .prompt()
                    .user(message)
                    .call()
                    .content();
        } catch (Exception e) {
            logger.error("Error processing regular message: {}", e.getMessage(), e);
            return "I apologize, but I'm experiencing technical difficulties. Please try again in a moment.";
        }
    }
    
    /**
     * Check if safe mode is enabled
     */
    public boolean isSafeModeEnabled() {
        return safeGuardConfig.isEnabled();
    }
    
    /**
     * Get current sensitivity level
     */
    public String getSensitivityLevel() {
        return safeGuardConfig.getSensitivityLevel();
    }
    
    /**
     * Get filter status information
     */
    public FilterStatus getFilterStatus() {
        return new FilterStatus(
            safeGuardConfig.isEnabled(),
            safeGuardConfig.getSensitivityLevel(),
            safeGuardConfig.getAllFilterKeywords().size()
        );
    }
    
    /**
     * Record class for filter status
     */
    public record FilterStatus(boolean enabled, String sensitivityLevel, int activeRulesCount) {}
}
