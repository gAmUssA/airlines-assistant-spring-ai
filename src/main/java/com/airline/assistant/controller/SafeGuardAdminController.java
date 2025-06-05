package com.airline.assistant.controller;

import com.airline.assistant.config.SafeGuardConfig;
import com.airline.assistant.service.FilterAuditService;
import com.airline.assistant.service.SafeChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin controller for SafeGuard content filtering management
 */
@RestController
@RequestMapping("/api/v1/admin/safeguard")
@CrossOrigin(origins = "*")
public class SafeGuardAdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(SafeGuardAdminController.class);
    
    private final SafeGuardConfig safeGuardConfig;
    private final FilterAuditService auditService;
    private final SafeChatService safeChatService;
    
    public SafeGuardAdminController(SafeGuardConfig safeGuardConfig, 
                                   FilterAuditService auditService,
                                   SafeChatService safeChatService) {
        this.safeGuardConfig = safeGuardConfig;
        this.auditService = auditService;
        this.safeChatService = safeChatService;
    }
    
    /**
     * Get current SafeGuard configuration
     */
    @GetMapping("/config")
    public ResponseEntity<SafeGuardConfigResponse> getConfig() {
        try {
            SafeGuardConfigResponse config = new SafeGuardConfigResponse(
                safeGuardConfig.isEnabled(),
                safeGuardConfig.getSensitivityLevel(),
                safeGuardConfig.getWarningMessage(),
                safeGuardConfig.getProfanityKeywords(),
                safeGuardConfig.getInappropriateKeywords(),
                safeGuardConfig.getPolicyViolationKeywords(),
                safeGuardConfig.getAllFilterKeywords().size()
            );
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Error getting SafeGuard config: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get filter statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<FilterStatsResponse> getStats() {
        try {
            FilterStatsResponse stats = new FilterStatsResponse(
                auditService.getTotalFilterEvents(),
                safeChatService.getFilterStatus()
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting filter stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Update sensitivity level
     */
    @PutMapping("/sensitivity")
    public ResponseEntity<String> updateSensitivity(@RequestBody SensitivityUpdateRequest request) {
        try {
            String oldLevel = safeGuardConfig.getSensitivityLevel();
            // Note: In a real implementation, you'd update the configuration
            // For now, we'll just log the change
            auditService.logConfigurationChange("sensitivity-level", oldLevel, request.level(), "admin");
            logger.info("Sensitivity level change requested: {} -> {}", oldLevel, request.level());
            return ResponseEntity.ok("Sensitivity level update logged. Restart required for changes to take effect.");
        } catch (Exception e) {
            logger.error("Error updating sensitivity: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Test content filtering
     */
    @PostMapping("/test")
    public ResponseEntity<FilterTestResponse> testFilter(@RequestBody FilterTestRequest request) {
        try {
            List<String> allKeywords = safeGuardConfig.getAllFilterKeywords();
            boolean wouldFilter = allKeywords.stream()
                .anyMatch(keyword -> request.content().toLowerCase().contains(keyword.toLowerCase()));
            
            String result = wouldFilter ? "FILTERED" : "ALLOWED";
            String explanation = wouldFilter ? 
                "Content contains filtered keywords" : 
                "Content passes filter checks";
                
            FilterTestResponse response = new FilterTestResponse(
                request.content(),
                result,
                explanation,
                wouldFilter ? safeGuardConfig.getWarningMessage() : null
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error testing filter: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Record classes for request/response objects
    public record SafeGuardConfigResponse(
        boolean enabled,
        String sensitivityLevel,
        String warningMessage,
        List<String> profanityKeywords,
        List<String> inappropriateKeywords,
        List<String> policyViolationKeywords,
        int totalKeywords
    ) {}
    
    public record FilterStatsResponse(
        long totalFilterEvents,
        SafeChatService.FilterStatus currentStatus
    ) {}
    
    public record SensitivityUpdateRequest(String level) {}
    
    public record FilterTestRequest(String content) {}
    
    public record FilterTestResponse(
        String content,
        String result,
        String explanation,
        String warningMessage
    ) {}
}
