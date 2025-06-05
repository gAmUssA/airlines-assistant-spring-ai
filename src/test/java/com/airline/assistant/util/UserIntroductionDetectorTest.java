package com.airline.assistant.util;

import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntroductionDetectorTest {

    @Test
    public void testDetectUsername_WithIntroductions() {
        // Test various introduction patterns
        assertTrue(UserIntroductionDetector.detectUsername("Hello, I'm John").isPresent());
        assertTrue(UserIntroductionDetector.detectUsername("My name is Sarah").isPresent());
        assertTrue(UserIntroductionDetector.detectUsername("I am Michael").isPresent());
        assertTrue(UserIntroductionDetector.detectUsername("This is Emma").isPresent());
        assertTrue(UserIntroductionDetector.detectUsername("David here").isPresent());
        
        // Test extracted usernames
        assertEquals("John", UserIntroductionDetector.detectUsername("Hello, I'm John").get());
        assertEquals("Sarah", UserIntroductionDetector.detectUsername("My name is Sarah").get());
        assertEquals("Michael", UserIntroductionDetector.detectUsername("I am Michael").get());
        assertEquals("Emma", UserIntroductionDetector.detectUsername("This is Emma").get());
        assertEquals("David", UserIntroductionDetector.detectUsername("David here").get());
    }
    
    @Test
    public void testDetectUsername_WithoutIntroductions() {
        // Test non-introduction messages
        assertFalse(UserIntroductionDetector.detectUsername("What are the benefits of Gold status?").isPresent());
        assertFalse(UserIntroductionDetector.detectUsername("How many miles do I need for Silver?").isPresent());
        assertFalse(UserIntroductionDetector.detectUsername("Can you tell me about the lounge access?").isPresent());
    }
    
    @Test
    public void testDetectUsername_WithEdgeCases() {
        // Test edge cases
        assertFalse(UserIntroductionDetector.detectUsername("").isPresent());
        assertFalse(UserIntroductionDetector.detectUsername(null).isPresent());
        assertFalse(UserIntroductionDetector.detectUsername("I'm").isPresent()); // No name after "I'm"
        assertFalse(UserIntroductionDetector.detectUsername("My name is").isPresent()); // No name after "My name is"
    }
    
    @Test
    public void testDetectUsername_WithComplexMessages() {
        // Test more complex messages
        assertEquals("John", UserIntroductionDetector.detectUsername("Hello there, I'm John and I have a question about my miles").get());
        assertEquals("Sarah", UserIntroductionDetector.detectUsername("My name is Sarah. Can you help me with my booking?").get());
        assertEquals("Michael", UserIntroductionDetector.detectUsername("I am Michael, a frequent flyer with your airline").get());
    }
}