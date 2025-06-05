package com.airline.assistant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties for SafeGuard content filtering
 */
@Component
@ConfigurationProperties(prefix = "airline.assistant.safeguard")
public class SafeGuardConfig {
    
    /**
     * List of keywords/phrases to filter for profanity
     */
    private List<String> profanityKeywords = List.of(
        "damn", "hell", "crap", "stupid", "idiot", "moron", "dumb"
    );
    
    /**
     * List of keywords/phrases to filter for inappropriate content
     */
    private List<String> inappropriateKeywords = List.of(
        "inappropriate", "harmful", "offensive", "rude", "disrespectful",
        "unprofessional", "threatening", "abusive", "discriminatory"
    );
    
    /**
     * List of airline-specific policy violations
     */
    private List<String> policyViolationKeywords = List.of(
        "bomb", "weapon", "hijack", "terrorist", "attack", "violence",
        "illegal", "drugs", "smuggle", "contraband"
    );
    
    /**
     * Enable/disable content filtering
     */
    private boolean enabled = true;
    
    /**
     * Filter sensitivity level: LOW, MEDIUM, HIGH
     */
    private String sensitivityLevel = "MEDIUM";
    
    /**
     * Custom warning message for filtered content
     */
    private String warningMessage = "I need to maintain professional communication standards. Let me help you with your airline travel needs in an appropriate manner.";

    // Getters and setters
    public List<String> getProfanityKeywords() {
        return profanityKeywords;
    }

    public void setProfanityKeywords(List<String> profanityKeywords) {
        this.profanityKeywords = profanityKeywords;
    }

    public List<String> getInappropriateKeywords() {
        return inappropriateKeywords;
    }

    public void setInappropriateKeywords(List<String> inappropriateKeywords) {
        this.inappropriateKeywords = inappropriateKeywords;
    }

    public List<String> getPolicyViolationKeywords() {
        return policyViolationKeywords;
    }

    public void setPolicyViolationKeywords(List<String> policyViolationKeywords) {
        this.policyViolationKeywords = policyViolationKeywords;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSensitivityLevel() {
        return sensitivityLevel;
    }

    public void setSensitivityLevel(String sensitivityLevel) {
        this.sensitivityLevel = sensitivityLevel;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }
    
    /**
     * Get all filter keywords combined based on sensitivity level
     */
    public List<String> getAllFilterKeywords() {
        return switch (sensitivityLevel.toUpperCase()) {
            case "LOW" -> inappropriateKeywords;
            case "HIGH" -> {
                var combined = new java.util.ArrayList<>(profanityKeywords);
                combined.addAll(inappropriateKeywords);
                combined.addAll(policyViolationKeywords);
                yield combined;
            }
            default -> { // MEDIUM
                var combined = new java.util.ArrayList<>(inappropriateKeywords);
                combined.addAll(policyViolationKeywords);
                yield combined;
            }
        };
    }
}
