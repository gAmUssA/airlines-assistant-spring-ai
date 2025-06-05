package com.airline.assistant.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for detecting user introductions in messages.
 */
public class UserIntroductionDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserIntroductionDetector.class);

    // Patterns to detect common introduction phrases
    private static final Pattern[] INTRODUCTION_PATTERNS = {
        // "I am [name]" or "I'm [name]"
        Pattern.compile("(?i)\\b(?:I am|I'm)\\s+([A-Za-z]+)\\b"),
        
        // "My name is [name]"
        Pattern.compile("(?i)\\bmy name is\\s+([A-Za-z]+)\\b"),
        
        // "Hello, I am [name]" or "Hi, I'm [name]"
        Pattern.compile("(?i)\\b(?:hello|hi|hey),?\\s+(?:I am|I'm)\\s+([A-Za-z]+)\\b"),
        
        // "This is [name]"
        Pattern.compile("(?i)\\bthis is\\s+([A-Za-z]+)\\b"),
        
        // "[Name] here"
        Pattern.compile("(?i)\\b([A-Za-z]+)\\s+here\\b")
    };

    /**
     * Detects if a message contains a user introduction and extracts the username.
     *
     * @param message the message to analyze
     * @return an Optional containing the extracted username if found, or empty if no introduction detected
     */
    public static Optional<String> detectUsername(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Optional.empty();
        }

        for (Pattern pattern : INTRODUCTION_PATTERNS) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String username = matcher.group(1);
                LOGGER.debug("Detected username '{}' in message: {}", username, message);
                return Optional.of(username);
            }
        }

        LOGGER.debug("No username detected in message: {}", message);
        return Optional.empty();
    }
}