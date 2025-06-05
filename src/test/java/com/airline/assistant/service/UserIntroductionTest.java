package com.airline.assistant.service;

import com.airline.assistant.model.User;
import com.airline.assistant.repository.UserRepository;
import com.airline.assistant.util.UserIntroductionDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for user introduction detection functionality.
 * This test focuses on the UserIntroductionDetector utility and UserService integration.
 */
public class UserIntroductionTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    public void testUserIntroductionDetection() {
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

        // Test non-introduction messages
        assertFalse(UserIntroductionDetector.detectUsername("What are the benefits of Gold status?").isPresent());
        assertFalse(UserIntroductionDetector.detectUsername("How many miles do I need for Silver?").isPresent());
    }

    @Test
    public void testUserInfoForPrompt() {
        // Set up a mock user
        User mockUser = new User("John", "Smith", "DL123456", "Gold", "ATL", "Delta");
        when(userRepository.findByUsername("John")).thenReturn(Optional.of(mockUser));

        // Get user info for prompt
        String userInfo = userService.getUserInfoForPrompt("John");

        // Verify the user info contains the expected information
        assertTrue(userInfo.contains("User Information"));
        assertTrue(userInfo.contains("Username: John"));
        assertTrue(userInfo.contains("Last Name: Smith"));
        assertTrue(userInfo.contains("Loyalty Number: DL123456"));
        assertTrue(userInfo.contains("Loyalty Status: Gold"));
        assertTrue(userInfo.contains("Preferred Airport: ATL"));
        assertTrue(userInfo.contains("Airline: Delta"));
    }

    @Test
    public void testUserInfoForPromptWhenUserNotFound() {
        // Set up repository to return no user
        when(userRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        // Get user info for prompt
        String userInfo = userService.getUserInfoForPrompt("Unknown");

        // Verify the user info is empty
        assertEquals("", userInfo);
    }
}
