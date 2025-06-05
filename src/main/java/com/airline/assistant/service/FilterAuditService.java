package com.airline.assistant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for auditing and logging content filter events
 */
@Service
public class FilterAuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(FilterAuditService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("FILTER_AUDIT");
    
    private final AtomicLong filterEventCounter = new AtomicLong(0);
    
    /**
     * Log a content filter event for audit purposes
     */
    public void logFilterEvent(String conversationId, String originalContent, String filteredContent, String reason) {
        long eventId = filterEventCounter.incrementAndGet();
        
        // Structured audit log
        auditLogger.info("FILTER_EVENT|eventId={}|timestamp={}|conversationId={}|reason={}|originalLength={}|filteredLength={}",
                eventId,
                LocalDateTime.now(),
                conversationId,
                reason,
                originalContent != null ? originalContent.length() : 0,
                filteredContent != null ? filteredContent.length() : 0
        );
        
        // Debug log with content (be careful with sensitive data)
        logger.debug("Content filtered - Event ID: {}, Conversation: {}, Reason: {}", 
                eventId, conversationId, reason);
        
        // Log metrics
        logger.info("Filter event #{} recorded for conversation {}", eventId, conversationId);
    }
    
    /**
     * Log safe chat usage
     */
    public void logSafeChatUsage(String conversationId, boolean filterTriggered) {
        auditLogger.info("SAFE_CHAT_USAGE|timestamp={}|conversationId={}|filterTriggered={}",
                LocalDateTime.now(),
                conversationId,
                filterTriggered
        );
    }
    
    /**
     * Get total filter events count
     */
    public long getTotalFilterEvents() {
        return filterEventCounter.get();
    }
    
    /**
     * Log filter configuration change
     */
    public void logConfigurationChange(String setting, String oldValue, String newValue, String changedBy) {
        auditLogger.info("CONFIG_CHANGE|timestamp={}|setting={}|oldValue={}|newValue={}|changedBy={}",
                LocalDateTime.now(),
                setting,
                oldValue,
                newValue,
                changedBy
        );
        
        logger.info("Filter configuration changed: {} from '{}' to '{}' by {}", 
                setting, oldValue, newValue, changedBy);
    }
}
